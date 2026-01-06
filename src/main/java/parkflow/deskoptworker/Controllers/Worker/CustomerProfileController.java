package parkflow.deskoptworker.Controllers.Worker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import parkflow.deskoptworker.models.Customer;
import parkflow.deskoptworker.models.Transaction;
import parkflow.deskoptworker.models.Vehicle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerProfileController {

    // Header
    @FXML private Label customerNameLabel;
    @FXML private Label customerEmailLabel;
    @FXML private Label walletBalanceLabel;
    @FXML private Label totalSpentLabel;

    // Pending Payments Alert
    @FXML private HBox pendingPaymentsAlert;
    @FXML private Label pendingPaymentsText;

    // Vehicles
    @FXML private VBox vehiclesContainer;


    // Recent Activity
    @FXML private VBox recentActivityContainer;

    private Customer currentCustomer;

    @Setter
    private CustomerProfileListener listener;

    // Mock data dla przykładu - w rzeczywistości to by było z API
    private List<Transaction> customerTransactions = new ArrayList<>();

    @FXML
    public void initialize() {
        System.out.println("CustomerProfileController initialized");
    }

    /**
     * Ustawia klienta i wypełnia dane
     */
    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        loadMockTransactions(); // TODO: zastąpić prawdziwym API call
        updateView();
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

        // Wyczyść poprzednie klasy koloru
        walletBalanceLabel.getStyleClass().removeAll("low", "ok");

        // Dodaj odpowiednią klasę koloru
        if (balance < 10) {
            walletBalanceLabel.getStyleClass().add("low");
        } else {
            walletBalanceLabel.getStyleClass().add("ok");
        }

        totalSpentLabel.setText(String.format("%.2f $", currentCustomer.getTotalSpent()));

        // Load sections
        loadVehicles();
        checkAndShowPendingPayments();
        loadRecentActivity();
    }

    /**
     * Ładuje pojazdy klienta
     */
    private void loadVehicles() {
        vehiclesContainer.getChildren().clear();

        if (currentCustomer.getVehicles().isEmpty()) {
            Label noVehicles = new Label("No vehicles registered");
            noVehicles.getStyleClass().add("customer-email-label"); // Używamy istniejącej klasy dla szarego tekstu
            vehiclesContainer.getChildren().add(noVehicles);
            return;
        }

        for (Vehicle vehicle : currentCustomer.getVehicles()) {
            Label vehicleCard = createVehicleCard(vehicle);
            vehiclesContainer.getChildren().add(vehicleCard);
        }
    }

    /**
     * Tworzy kartę pojazdu - UPROSZCZONE: tylko Label z CSS
     */
    private Label createVehicleCard(Vehicle vehicle) {
        Label vehicleLabel = new Label(vehicle.getRegistrationNumber());
        vehicleLabel.getStyleClass().add("vehicle_label");
        vehicleLabel.setMaxWidth(Double.MAX_VALUE); // Zajmuje całą szerokość
        return vehicleLabel;
    }

    /**
     * Sprawdza czy klient ma pending payments i pokazuje alert
     */
    private void checkAndShowPendingPayments() {
        List<Transaction> pending = customerTransactions.stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.PENDING)
                .toList();

        if (!pending.isEmpty()) {
            double totalAmount = pending.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            showPendingPayments(pending.size(), totalAmount);
        } else {
            pendingPaymentsAlert.setVisible(false);
            pendingPaymentsAlert.setManaged(false);
        }
    }



    /**
     * Ładuje ostatnią aktywność (tylko zakończone/aktywne postoje)
     */
    private void loadRecentActivity() {
        recentActivityContainer.getChildren().clear();

        // Filtruj tylko PARKING_SESSION (nie WALLET DEPOSITS, nie RESERVATION_FEE)
        List<Transaction> parkingSessions = customerTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.PARKING_SESSION)
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.COMPLETED ||
                        t.getStatus() == Transaction.TransactionStatus.PENDING)
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate())) // Najnowsze pierwsze
                .limit(5)
                .toList();

        if (parkingSessions.isEmpty()) {
            Label noActivity = new Label("No recent parking activity");
            noActivity.getStyleClass().add("customer-email-label");
            recentActivityContainer.getChildren().add(noActivity);
            return;
        }

        for (Transaction transaction : parkingSessions) {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/components/ActivityItem.fxml")
                );
                HBox activityItem = loader.load();

                parkflow.deskoptworker.Controllers.Components.ActivityItemController controller =
                        loader.getController();
                controller.setTransaction(transaction);

                recentActivityContainer.getChildren().add(activityItem);

            } catch (java.io.IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load ActivityItem.fxml");
            }
        }
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

    @FXML
    private void onViewAllActivity() {
        if (listener != null) {
            listener.onViewAllActivity(currentCustomer, customerTransactions);
        }
    }

    // ===== MOCK DATA =====

    /**
     * Ładuje przykładowe transakcje dla klienta
     * TODO: Zastąpić prawdziwym API call
     */
    private void loadMockTransactions() {
        customerTransactions.clear();

        if (currentCustomer == null) return;

        // Tylko dla Jana Kowalskiego (id=1) przykładowe dane
        if (currentCustomer.getCustomerId() == 1) {
            customerTransactions.add(new Transaction(
                    1,
                    LocalDateTime.of(2025, 1, 3, 14, 30),
                    Transaction.TransactionType.PARKING_SESSION,
                    Transaction.TransactionStatus.COMPLETED,
                    12.00,
                    "Parking session (2h 15min)",
                    1, "Jan Kowalski", "KR 12345",
                    98, "Galeria Krakowska"
            ));

            customerTransactions.add(new Transaction(
                    2,
                    LocalDateTime.of(2025, 1, 2, 10, 15),
                    Transaction.TransactionType.PARKING_SESSION,
                    Transaction.TransactionStatus.PENDING,
                    8.50,
                    "Parking session (1h 30min)",
                    1, "Jan Kowalski", "KR 12345",
                    98, "Galeria Krakowska"
            ));

            customerTransactions.add(new Transaction(
                    3,
                    LocalDateTime.of(2024, 12, 28, 16, 45),
                    Transaction.TransactionType.PARKING_SESSION,
                    Transaction.TransactionStatus.COMPLETED,
                    15.00,
                    "Parking session (3h)",
                    1, "Jan Kowalski", "KR 12345",
                    99, "Downtown Plaza"
            ));

            customerTransactions.add(new Transaction(
                    4,
                    LocalDateTime.of(2024, 12, 20, 9, 30),
                    Transaction.TransactionType.PARKING_SESSION,
                    Transaction.TransactionStatus.COMPLETED,
                    6.50,
                    "Parking session (1h 5min)",
                    1, "Jan Kowalski", "KR 12345",
                    99, "Downtown Plaza"
            ));
        }
    }

    // ===== LISTENER INTERFACE =====

    public interface CustomerProfileListener {
        void onClearFilter();
        void onViewPendingPayments(Customer customer);
        void onViewAllReservations(Customer customer);
        void onViewAllPayments(Customer customer);
        void onViewAllActivity(Customer customer, List<Transaction> transactions);
    }
}