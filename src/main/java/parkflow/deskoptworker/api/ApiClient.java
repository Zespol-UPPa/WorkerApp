package parkflow.deskoptworker.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import lombok.Setter;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.utils.AlertHelper;
import parkflow.deskoptworker.utils.SessionManager;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private static final String API_BASE_URL = "http://localhost:8090";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private static final ObjectMapper mapper = new ObjectMapper();

    private final HttpClient client;
    private boolean showAlertsEnabled = true;

    /**
     * -- SETTER --
     *  Set callback to be executed when session expires (401)
     *  Typically this would logout user and return to login screen
     */
    // Callback for handling session expiration (401)
    @Setter
    private static Runnable sessionExpiredCallback = null;

    public ApiClient() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        this.client = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .cookieHandler(cookieManager)
                .build();
    }


    /* ========================
       TOKEN MANAGEMENT
     ======================== */

    public void setAuthToken(String token) {
        SessionManager.getInstance().setToken(token);
    }

    public void clearAuthToken() {
        SessionManager.getInstance().clear();
    }

    /* ========================
       ALERT CONFIGURATION
     ======================== */

    public void setShowAlerts(boolean enabled) {
        this.showAlertsEnabled = enabled;
    }

    /* ========================
       PUBLIC HTTP METHODS
     ======================== */

    public <T> T get(String path, boolean requiresAuth, Class<T> responseType) {
        return send("GET", path, null, requiresAuth, responseType, true);
    }

    public <T> T post(String path, Object body, boolean requiresAuth, Class<T> responseType) {
        return send("POST", path, body, requiresAuth, responseType, true);
    }

    public <T> T put(String path, Object body, boolean requiresAuth, Class<T> responseType) {
        return send("PUT", path, body, requiresAuth, responseType, true);
    }

    public <T> T delete(String path, boolean requiresAuth, Class<T> responseType) {
        return send("DELETE", path, null, requiresAuth, responseType, true);
    }

    /**
     * Overloaded methods with explicit alert control
     * Use these when you want to handle errors manually (e.g., logout)
     */
    public <T> T get(String path, boolean requiresAuth, Class<T> responseType, boolean showAlert) {
        return send("GET", path, null, requiresAuth, responseType, showAlert);
    }

    public <T> T post(String path, Object body, boolean requiresAuth, Class<T> responseType, boolean showAlert) {
        return send("POST", path, body, requiresAuth, responseType, showAlert);
    }

    public <T> T put(String path, Object body, boolean requiresAuth, Class<T> responseType, boolean showAlert) {
        return send("PUT", path, body, requiresAuth, responseType, showAlert);
    }

    public <T> T delete(String path, boolean requiresAuth, Class<T> responseType, boolean showAlert) {
        return send("DELETE", path, null, requiresAuth, responseType, showAlert);
    }

    /* ========================
       CORE REQUEST METHOD
     ======================== */

    private <T> T send(
            String method,
            String path,
            Object body,
            boolean requiresAuth,
            Class<T> responseType,
            boolean showAlert
    ) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + path))
                    .timeout(TIMEOUT)
                    .header("Content-Type", "application/json");

            if (requiresAuth) {
                String token = SessionManager.getInstance().getToken();

                System.out.println("=========================");
                System.out.println("Token: " + token);
                System.out.println("=========================");
                if (token == null) {
                    if (showAlert && showAlertsEnabled) {
                        showErrorAlert("Authentication Error", "No authentication token found. Please log in again.");
                    }
                    throw new RuntimeException("No auth token");
                }
                builder.header("Authorization", "Bearer " + token);

                User currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser != null) {
                    builder.header("X-Account-Id", String.valueOf(currentUser.getId()));
                    System.out.println("Sending X-Account-Id: " + currentUser.getId());
                }
            }

            if (body != null) {
                String json = mapper.writeValueAsString(body);
                builder.method(method, HttpRequest.BodyPublishers.ofString(json));
            } else {
                builder.method(method, HttpRequest.BodyPublishers.noBody());
            }

            HttpResponse<String> response = client.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            int status = response.statusCode();

            // ===== AUTH ERRORS =====
            if (status == 401) {
                // Clear session immediately
                SessionManager.getInstance().clear();

                if (showAlert && showAlertsEnabled) {
                    showErrorAlert("Session Expired", "Your session has expired. Please log in again.");
                }

                // Trigger session expired callback (logout and return to login)
                if (sessionExpiredCallback != null) {
                    Platform.runLater(sessionExpiredCallback);
                }

                throw new RuntimeException("Unauthorized");
            }

            if (status == 403) {
                if (showAlert && showAlertsEnabled) {
                    showErrorAlert("Access Denied", "You don't have permission to perform this action.");
                }
                throw new RuntimeException("Forbidden");
            }

            // ===== CLIENT ERRORS (400-499) =====
            if (status >= 400 && status < 500) {
                String errorMessage = extractErrorMessage(response.body());
                if (showAlert && showAlertsEnabled) {
                    showErrorAlert("Request Error", errorMessage);
                }
                throw new RuntimeException("Bad Request: " + status + " - " + errorMessage);
            }

            // ===== SERVER ERRORS =====
            if (status >= 500) {
                if (showAlert && showAlertsEnabled) {
                    showErrorAlert("Server Error", "The server encountered an error. Please try again later.");
                }
                throw new RuntimeException("Server error: " + status);
            }

            // ===== NO CONTENT =====
            if (status == 204 || responseType == Void.class) {
                return null;
            }

            // ===== PARSE RESPONSE =====
            return mapper.readValue(response.body(), responseType);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            if (showAlert && showAlertsEnabled) {
                showErrorAlert("Connection Error", "Failed to connect to server. Please check your internet connection.");
            }
            throw new RuntimeException("API request failed: " + e.getMessage(), e);
        }
    }

    /* ========================
       HELPER METHODS
     ======================== */

    private String extractErrorMessage(String responseBody) {
        try {
            var json = mapper.readTree(responseBody);
            if (json.has("error")) {
                return json.get("error").asText();
            }
            if (json.has("message")) {
                return json.get("message").asText();
            }
        } catch (Exception e) {
            // If parsing fails, return raw body
        }
        return responseBody != null && !responseBody.isEmpty()
                ? responseBody
                : "Unknown error occurred";
    }

    private void showErrorAlert(String title, String message) {
        if (Platform.isFxApplicationThread()) {
            AlertHelper.showError(title, message);
        } else {
            Platform.runLater(() -> AlertHelper.showError(title, message));
        }
    }
}