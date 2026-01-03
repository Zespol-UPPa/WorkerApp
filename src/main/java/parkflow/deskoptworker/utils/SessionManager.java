package parkflow.deskoptworker.utils;

import lombok.Getter;
import lombok.Setter;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;

public class SessionManager {
    @Getter
    @Setter
    private static User currentUser;

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == UserRole.ADMIN;
    }

    public static void logout() {
        currentUser = null;
    }
}