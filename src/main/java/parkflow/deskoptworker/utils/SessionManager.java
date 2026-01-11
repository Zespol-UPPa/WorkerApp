package parkflow.deskoptworker.utils;

import lombok.Getter;
import lombok.Setter;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;

@Setter
@Getter
public class SessionManager {

    private static SessionManager instance;

    private User currentUser;
    private String token;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == UserRole.ADMIN;
    }

    public boolean isWorker() {
        return currentUser != null && currentUser.getRole() == UserRole.WORKER;
    }


    public void clear() {
        currentUser = null;
        token = null;
    }
}
