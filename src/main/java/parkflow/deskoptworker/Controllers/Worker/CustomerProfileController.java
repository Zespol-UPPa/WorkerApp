package parkflow.deskoptworker.Controllers.Worker;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.models.Customer;
import parkflow.deskoptworker.models.Vehicle;

import java.util.Objects;

public class CustomerProfileController {

    // Header
    @FXML private Button clearFilterButton;
    @FXML private Label customerNameLabel;
    @FXML private Label customerEmailLabel;
    @FXML private Label walletBalanceLabel;
    @FXML private Label totalSpentLabel;

    // Pending Payments Alert
    @FXML private HBox pendingPaymentsAlert;
    @FXML private Label pendingPaymentsText;

    // Vehicles
    @FXML private VBox vehiclesContainer;

    // Active Reservation
    @FXML private VBox activeReservationContainer;
    @FXML private Label reservationParkingName;
    @FXML private Label reservationVehicle;
    @FXML private Label reservationDate;
    @FXML private Label reservationSpot;
    @FXML private Label reservationCost;

    // Recent Activity
    @FXML private VBox recentActivityContainer;

    private Customer currentCustomer;
    private CustomerProfileListener listener;

    @FXML
    public void initialize() {
        System.out.println("CustomerProfileController initialized");
    }

    /**
     * Ustawia klienta i wypełnia dane
     */
    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        updateView();
    }


    public void setListener(CustomerProfileListener listener) {
        this.listener = listener;
    }

    /**
     * Aktualizuje widok na podstawie danych klienta
     */
    private void updateView() {
        if (currentCustomer == null) return;

        // Update header
        customerNameLabel.setText(currentCustomer.getFullName());
        customerEmailLabel.setText(currentCustomer.getEmail());

        // Update wallet - kolor zależny od salda
        double balance = currentCustomer.getWalletBalance();
        walletBalanceLabel.setText(String.format("%.2f $", balance));
        String balanceColor = balance < 10 ? "#DC3545" : "#28A745";
        walletBalanceLabel.setStyle(
                "-fx-font-family: 'Inter 18pt Bold'; " +
                        "-fx-font-size: 20px; " +
                        "-fx-text-fill: " + balanceColor + ";"
        );

        totalSpentLabel.setText(String.format("%.2f $", currentCustomer.getTotalSpent()));

        loadVehicles();

        loadRecentActivity();

        // Pending payments - na razie ukryte
        pendingPaymentsAlert.setVisible(false);
        pendingPaymentsAlert.setManaged(false);

        // Active reservation - na razie ukryte
        activeReservationContainer.setVisible(false);
        activeReservationContainer.setManaged(false);
    }

    /**
     * Ładuje pojazdy klienta
     */
    private void loadVehicles() {
        vehiclesContainer.getChildren().clear();

        if (currentCustomer.getVehicles().isEmpty()) {
            Label noVehicles = new Label("No vehicles registered");
            noVehicles.setStyle("-fx-font-family: 'Inter 18pt Regular'; -fx-font-size: 14px; -fx-text-fill: #8E8E93;");
            vehiclesContainer.getChildren().add(noVehicles);
            return;
        }

        for (Vehicle vehicle : currentCustomer.getVehicles()) {
            VBox vehicleCard = createVehicleCard(vehicle);
            vehiclesContainer.getChildren().add(vehicleCard);
        }
    }

    /**
     * Tworzy kartę pojazdu
     */
    private VBox createVehicleCard(Vehicle vehicle) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: #E3F2FD; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-padding: 20;"
        );

        Label registrationLabel = new Label(vehicle.getRegistrationNumber());
        registrationLabel.setStyle(
                "-fx-font-family: 'Inter 18pt Bold'; " +
                        "-fx-font-size: 18px; " +
                        "-fx-text-fill: #1976D2;"
        );

        card.getChildren().add(registrationLabel);
        return card;
    }

    /**
     * Ładuje ostatnią aktywność (przykładowe dane)
     */
    private void loadRecentActivity() {
        recentActivityContainer.getChildren().clear();

        // Przykładowe aktywności
        recentActivityContainer.getChildren().addAll(
                createActivityItem("parking", "Parking at Downtown Plaza", "2025-11-20", "-8.50 $", false),
                createActivityItem("wallet", "Wallet top-up", "2025-11-20", "+50.00 $", true)
        );
    }

    /**
     * Tworzy element aktywności
     */
    private HBox createActivityItem(String iconType, String description, String date, String amount, boolean isPositive) {
        HBox item = new HBox(16);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12px; " +
                        "-fx-padding: 16;"
        );

        // Icon
        ImageView icon = new ImageView();
        icon.setFitWidth(32);
        icon.setFitHeight(32);
        icon.setPreserveRatio(true);

        try {
            String iconPath = iconType.equals("parking") ?
                    "/parkflow/deskoptworker/images/car.png" :
                    "/parkflow/deskoptworker/images/wallet.png";
            icon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
        } catch (Exception e) {
            System.err.println("Failed to load activity icon");
        }

        // Circle background for icon
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setStyle(
                "-fx-background-color: " + (iconType.equals("parking") ? "#E3F2FD" : "#E8F5E9") + "; " +
                        "-fx-background-radius: 50%; " +
                        "-fx-pref-width: 48; " +
                        "-fx-pref-height: 48;"
        );
        iconContainer.getChildren().add(icon);

        // Description and date
        VBox textContainer = new VBox(4);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textContainer, javafx.scene.layout.Priority.ALWAYS);

        Label descLabel = new Label(description);
        descLabel.setStyle(
                "-fx-font-family: 'Inter 18pt SemiBold'; " +
                        "-fx-font-size: 16px; " +
                        "-fx-text-fill: #333333;"
        );

        Label dateLabel = new Label(date);
        dateLabel.setStyle(
                "-fx-font-family: 'Inter 18pt Regular'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #8E8E93;"
        );

        textContainer.getChildren().addAll(descLabel, dateLabel);

        // Amount
        Label amountLabel = new Label(amount);
        String amountColor = isPositive ? "#10B981" : "#333333";
        amountLabel.setStyle(
                "-fx-font-family: 'Inter 18pt Bold'; " +
                        "-fx-font-size: 16px; " +
                        "-fx-text-fill: " + amountColor + ";"
        );

        item.getChildren().addAll(iconContainer, textContainer, amountLabel);
        return item;
    }

    /**
     * Pokazuje pending payments alert
     */
    public void showPendingPayments(int count, double amount) {
        pendingPaymentsText.setText(
                String.format("This customer has %d unpaid parking session%s. Total amount: %.2f $",
                        count, count > 1 ? "s" : "", amount)
        );
        pendingPaymentsAlert.setVisible(true);
        pendingPaymentsAlert.setManaged(true);
    }

    /**
     * Pokazuje aktywną rezerwację
     */
    public void showActiveReservation(String parkingName, String vehicle, String date, String spot, double cost) {
        reservationParkingName.setText(parkingName);
        reservationVehicle.setText(vehicle);
        reservationDate.setText(date);
        reservationSpot.setText(spot);
        reservationCost.setText(String.format("%.2f $", cost));

        activeReservationContainer.setVisible(true);
        activeReservationContainer.setManaged(true);
    }

    // ===== EVENT HANDLERS =====

    @FXML
    private void onClearFilter() {
        if (listener != null) {
            listener.onClearFilter();
        }
    }

    @FXML
    private void onViewPendingPayments() {
        if (listener != null) {
            listener.onViewPendingPayments(currentCustomer);
        }
    }

    @FXML
    private void onViewAllReservations() {
        if (listener != null) {
            listener.onViewAllReservations(currentCustomer);
        }
    }

    @FXML
    private void onViewAllPayments() {
        if (listener != null) {
            listener.onViewAllPayments(currentCustomer);
        }
    }

    // ===== LISTENER INTERFACE =====

    public interface CustomerProfileListener {
        void onClearFilter();
        void onViewPendingPayments(Customer customer);
        void onViewAllReservations(Customer customer);
        void onViewAllPayments(Customer customer);
    }
}