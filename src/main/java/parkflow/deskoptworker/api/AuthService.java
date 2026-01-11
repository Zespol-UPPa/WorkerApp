package parkflow.deskoptworker.api;

import parkflow.deskoptworker.dto.LoginRequest;
import parkflow.deskoptworker.dto.LoginResponse;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;
import parkflow.deskoptworker.utils.SessionManager;

public class AuthService {

    private final ApiClient api = new ApiClient();

    public boolean login(String id, String password) {
        try {
            LoginRequest request = new LoginRequest(id, password);

            LoginResponse response = api.post(
                    "/api/auth/login/staff",
                    request,
                    false,
                    LoginResponse.class
            );

            if (response.getToken() == null) {
                // This shouldn't happen if server is working correctly
                return false;
            }

            DecodedToken decoded = JwtDecoder.decode(response.getToken());

            User user = new User(
                    Integer.parseInt(decoded.accountId()),
                    null,
                    null,
                    null,
                    null,
                    null,
                    UserRole.valueOf(decoded.role()),
                    true
            );

            SessionManager session = SessionManager.getInstance();
            session.setToken(response.getToken());
            session.setCurrentUser(user);

            api.setAuthToken(response.getToken());

            return true;

        } catch (RuntimeException e) {
            // Alert already shown by ApiClient
            return false;
        }
    }

    public void logout() {
        try {
            // Call backend with showAlert=false (no error alert for logout)
            api.post("/api/auth/logout", null, true, Void.class, false);
            System.out.println("Backend logout successful");
        } catch (Exception e) {
            // Even if backend fails, continue with local logout
            System.err.println("Backend logout failed: " + e.getMessage());
        }

        // ALWAYS clear local session (even if backend call failed)
        SessionManager.getInstance().clear();
        api.clearAuthToken();
    }
}