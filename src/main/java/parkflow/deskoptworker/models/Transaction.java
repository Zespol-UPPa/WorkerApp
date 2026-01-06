package parkflow.deskoptworker.models;

import javafx.beans.property.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model reprezentujący transakcję parkingową.
 * Typy transakcji:
 * - PARKING_SESSION: opłata za sesję parkingową
 * - RESERVATION_FEE: opłata za rezerwację miejsca
 */
public class Transaction {

    public enum TransactionType {
        PARKING_SESSION("Parking"),
        RESERVATION_FEE("Reservation");

        @Getter
        private final String displayName;

        TransactionType(String displayName) {
            this.displayName = displayName;
        }
    }

    public enum TransactionStatus {
        COMPLETED("Completed"),
        PENDING("Pending"),
        FAILED("Failed"),
        REFUNDED("Refunded");

        @Getter
        private final String displayName;

        TransactionStatus(String displayName) {
            this.displayName = displayName;
        }
    }

    // Properties
    private final IntegerProperty transactionId;
    private final ObjectProperty<LocalDateTime> transactionDate;
    private final ObjectProperty<TransactionType> type;
    private final ObjectProperty<TransactionStatus> status;
    private final DoubleProperty amount;
    private final StringProperty description;

    // Customer info
    private final IntegerProperty customerId;
    private final StringProperty customerName;
    private final StringProperty licensePlate;

    // Parking info
    private final IntegerProperty parkingId;
    private final StringProperty parkingName;

    // Related session/reservation ID
    private final IntegerProperty relatedId;

    // Date formatter
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Transaction() {
        this.transactionId = new SimpleIntegerProperty();
        this.transactionDate = new SimpleObjectProperty<>();
        this.type = new SimpleObjectProperty<>();
        this.status = new SimpleObjectProperty<>();
        this.amount = new SimpleDoubleProperty();
        this.description = new SimpleStringProperty();
        this.customerId = new SimpleIntegerProperty();
        this.customerName = new SimpleStringProperty();
        this.licensePlate = new SimpleStringProperty();
        this.parkingId = new SimpleIntegerProperty();
        this.parkingName = new SimpleStringProperty();
        this.relatedId = new SimpleIntegerProperty();
    }

    public Transaction(int transactionId, LocalDateTime transactionDate, TransactionType type,
                       TransactionStatus status, double amount, String description,
                       int customerId, String customerName, String licensePlate,
                       int parkingId, String parkingName) {
        this();
        setTransactionId(transactionId);
        setTransactionDate(transactionDate);
        setType(type);
        setStatus(status);
        setAmount(amount);
        setDescription(description);
        setCustomerId(customerId);
        setCustomerName(customerName);
        setLicensePlate(licensePlate);
        setParkingId(parkingId);
        setParkingName(parkingName);
    }

    // ==================== GETTERS & SETTERS ====================

    // Transaction ID
    public int getTransactionId() { return transactionId.get(); }
    public void setTransactionId(int value) { transactionId.set(value); }
    public IntegerProperty transactionIdProperty() { return transactionId; }

    // Transaction Date
    public LocalDateTime getTransactionDate() { return transactionDate.get(); }
    public void setTransactionDate(LocalDateTime value) { transactionDate.set(value); }
    public ObjectProperty<LocalDateTime> transactionDateProperty() { return transactionDate; }

    // Type
    public TransactionType getType() { return type.get(); }
    public void setType(TransactionType value) { type.set(value); }
    public ObjectProperty<TransactionType> typeProperty() { return type; }

    // Status
    public TransactionStatus getStatus() { return status.get(); }
    public void setStatus(TransactionStatus value) { status.set(value); }
    public ObjectProperty<TransactionStatus> statusProperty() { return status; }

    // Amount
    public double getAmount() { return amount.get(); }
    public void setAmount(double value) { amount.set(value); }
    public DoubleProperty amountProperty() { return amount; }

    // Description
    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    // Customer ID
    public int getCustomerId() { return customerId.get(); }
    public void setCustomerId(int value) { customerId.set(value); }
    public IntegerProperty customerIdProperty() { return customerId; }

    // Customer Name
    public String getCustomerName() { return customerName.get(); }
    public void setCustomerName(String value) { customerName.set(value); }
    public StringProperty customerNameProperty() { return customerName; }

    // License Plate
    public String getLicensePlate() { return licensePlate.get(); }
    public void setLicensePlate(String value) { licensePlate.set(value); }
    public StringProperty licensePlateProperty() { return licensePlate; }

    // Parking ID
    public int getParkingId() { return parkingId.get(); }
    public void setParkingId(int value) { parkingId.set(value); }
    public IntegerProperty parkingIdProperty() { return parkingId; }

    // Parking Name
    public String getParkingName() { return parkingName.get(); }
    public void setParkingName(String value) { parkingName.set(value); }
    public StringProperty parkingNameProperty() { return parkingName; }

    // Related ID
    public int getRelatedId() { return relatedId.get(); }
    public void setRelatedId(int value) { relatedId.set(value); }
    public IntegerProperty relatedIdProperty() { return relatedId; }

    // ==================== FORMATTED GETTERS ====================

    public String getFormattedDate() {
        if (transactionDate.get() == null) return "-";
        return transactionDate.get().format(DATE_FORMATTER);
    }

    public String getFormattedAmount() {
        return String.format("%.2f $", amount.get());
    }

    public String getTypeDisplayName() {
        return type.get() != null ? type.get().getDisplayName() : "-";
    }

    public String getStatusDisplayName() {
        return status.get() != null ? status.get().getDisplayName() : "-";
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + transactionId.get() +
                ", type=" + getTypeDisplayName() +
                ", customer='" + customerName.get() + '\'' +
                ", amount=" + getFormattedAmount() +
                ", status=" + getStatusDisplayName() +
                '}';
    }
}