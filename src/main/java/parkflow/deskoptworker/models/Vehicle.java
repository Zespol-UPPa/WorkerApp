package parkflow.deskoptworker.models;

import javafx.beans.property.*;

public class Vehicle {
    private final IntegerProperty vehicleId;
    private final StringProperty registrationNumber;
    private final IntegerProperty customerId; // Foreign key do Customer

    public Vehicle(int vehicleId, String registrationNumber, String vehicleType, int customerId) {
        this.vehicleId = new SimpleIntegerProperty(vehicleId);
        this.registrationNumber = new SimpleStringProperty(registrationNumber);
        this.customerId = new SimpleIntegerProperty(customerId);
    }

    // Simplified constructor (without vehicleType)
    public Vehicle(int vehicleId, String registrationNumber, int customerId) {
        this(vehicleId, registrationNumber, "Car", customerId);
    }

    // Vehicle ID
    public int getVehicleId() {
        return vehicleId.get();
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId.set(vehicleId);
    }

    public IntegerProperty vehicleIdProperty() {
        return vehicleId;
    }

    // Registration Number
    public String getRegistrationNumber() {
        return registrationNumber.get();
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber.set(registrationNumber);
    }

    public StringProperty registrationNumberProperty() {
        return registrationNumber;
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

    @Override
    public String toString() {
        return registrationNumber.get();
    }
}