package parkflow.deskoptworker;

import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == UserRole.ADMIN;
    }

    public static void logout() {
        currentUser = null;
    }
}