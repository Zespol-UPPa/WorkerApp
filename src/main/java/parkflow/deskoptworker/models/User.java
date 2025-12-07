package parkflow.deskoptworker.models;

public class User {
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

    // Gettery i settery
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
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

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

}

