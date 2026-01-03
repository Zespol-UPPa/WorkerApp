package parkflow.deskoptworker.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    // Gettery i settery
    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String pesel;
    private UserRole role;
    private boolean active;

    // Konstruktor z wszystkimi polami
    public User(int id, String firstName, String lastName, String phoneNumber,
                String email, String pesel, UserRole role, boolean active) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.pesel = pesel;
        this.role = role;
        this.active = active;
    }

    // Konstruktor bez ID (dla nowych użytkowników przed zapisem do DB)
    public User(String firstName, String lastName, String phoneNumber,
                String email, String pesel, UserRole role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.pesel = pesel;
        this.role = role;
        this.active = true;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Metody pomocnicze
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isWorker() {
        return role == UserRole.WORKER;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", pesel='" + pesel + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}

