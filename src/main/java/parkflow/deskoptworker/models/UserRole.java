package parkflow.deskoptworker.models;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("/admin"),
    WORKER("/worker");

    private final String path;

    UserRole(String path) {
        this.path = path;
    }

}
