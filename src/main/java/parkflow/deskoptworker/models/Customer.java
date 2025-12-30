package parkflow.deskoptworker.models;

import javafx.beans.property.*;
import lombok.Builder;



public class Customer {
    private final IntegerProperty customerId;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty email;
    private final ListProperty<Vehicle> vehicles;
    private final DoubleProperty walletBalance;
    private final DoubleProperty totalSpent;


    public Customer(int customerId, String firstName, String lastName, String email,
                    double walletBalance, double totalSpent) {
        this.customerId = new SimpleIntegerProperty(customerId);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.vehicles = new SimpleListProperty<>(javafx.collections.FXCollections.observableArrayList());
        this.walletBalance = new SimpleDoubleProperty(walletBalance);
        this.totalSpent = new SimpleDoubleProperty(totalSpent);
    }


    // Customer ID
    public int getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    // First Name
    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    // Last Name
    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    // Full Name (computed property)
    public String getFullName() {
        return firstName.get() + " " + lastName.get();
    }

    // Email
    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    // Vehicles
    public javafx.collections.ObservableList<Vehicle> getVehicles() {
        return vehicles.get();
    }

    public void setVehicles(javafx.collections.ObservableList<Vehicle> vehicles) {
        this.vehicles.set(vehicles);
    }

    public ListProperty<Vehicle> vehiclesProperty() {
        return vehicles;
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        this.vehicles.remove(vehicle);
    }

    // Vehicles count
    public int getVehiclesCount() {
        return vehicles.size();
    }

    // Wallet Balance
    public double getWalletBalance() {
        return walletBalance.get();
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance.set(walletBalance);
    }

    public DoubleProperty walletBalanceProperty() {
        return walletBalance;
    }

    // Total Spent
    public double getTotalSpent() {
        return totalSpent.get();
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent.set(totalSpent);
    }

    public DoubleProperty totalSpentProperty() {
        return totalSpent;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + customerId.get() +
                ", name='" + getFullName() + '\'' +
                ", email='" + email.get() + '\'' +
                ", vehicles=" + vehicles.size() +
                ", wallet=" + walletBalance.get() +
                ", totalSpent=" + totalSpent.get() +
                '}';
    }
}