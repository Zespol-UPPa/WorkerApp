package parkflow.deskoptworker.api;

import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;
import parkflow.deskoptworker.utils.SessionManager;

import java.util.Map;

public class AccountService {

    private final ApiClient api = new ApiClient();

    /**
     * Get current user's profile from backend
     * Dynamically uses /admin/profile or /worker/profile based on role
     */
    public User getCurrentUserProfile() {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser == null) {
                System.err.println("No user in session");
                return null;
            }

            // Build path based on role: /admin/profile or /worker/profile
            String path = currentUser.getRole().getPath() + "/profile";

            Map<String, Object> response = api.get(
                    path,
                    true, // requires auth
                    Map.class
            );

            // Parse response to User object
            return parseUserFromResponse(response);

        } catch (RuntimeException e) {
            System.err.println("Failed to get user profile: " + e.getMessage());
            return null;
        }
    }

    /**
     * Update personal information
     * Uses PUT /admin/profile or PUT /worker/profile
     */
    public boolean updatePersonalInfo(String firstName, String lastName, String phoneNumber) {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser == null) {
                System.err.println("No user in session");
                return false;
            }

            // Build path: /admin/profile or /worker/profile
            String path = currentUser.getRole().getPath() + "/profile";

            // Build query params (backend expects query params for PUT)
            String queryParams = String.format("?firstName=%s&lastName=%s&phoneNumber=%s",
                    urlEncode(firstName),
                    urlEncode(lastName),
                    urlEncode(phoneNumber)
            );

            api.put(path + queryParams, null, true, Void.class);
            return true;

        } catch (RuntimeException e) {
            System.err.println("Failed to update personal info: " + e.getMessage());
            return false;
        }
    }

    /**
     * Change password
     * Uses PUT /admin/password or PUT /worker/password
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser == null) {
                System.err.println("No user in session");
                return false;
            }

            // Build path: /admin/password or /worker/password
            String path = currentUser.getRole().getPath() + "/password";

            Map<String, String> request = Map.of(
                    "currentPassword", currentPassword,
                    "newPassword", newPassword
            );

            api.put(path, request, true, Void.class);
            return true;

        } catch (RuntimeException e) {
            // ApiClient already showed error alert
            System.err.println("Failed to change password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Parse user data from API response
     */
    private User parseUserFromResponse(Map<String, Object> response) {
        int id = ((Number) response.get("accountId")).intValue();
        String firstName = (String) response.getOrDefault("firstName", "");
        String lastName = (String) response.getOrDefault("lastName", "");
        String phoneNumber = (String) response.getOrDefault("phoneNumber", "");
        String email = (String) response.getOrDefault("email", "");
        String pesel = (String) response.getOrDefault("peselNumber", "");
        String roleStr = (String) response.get("role");
        UserRole role = UserRole.valueOf(roleStr);
        boolean active = (boolean) response.getOrDefault("active", true);

        User user = new User(id, firstName, lastName, phoneNumber, email, pesel, role, active);

        // Optional: Parse companyId if present
        if (response.containsKey("companyId")) {
            user.setCompanyId(((Number) response.get("companyId")).longValue());
        }

        return user;
    }

    /**
     * URL encode helper
     */
    private String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }
}