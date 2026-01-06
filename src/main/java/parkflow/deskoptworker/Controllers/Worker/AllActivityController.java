package parkflow.deskoptworker.Controllers.Worker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import parkflow.deskoptworker.models.Customer;
import parkflow.deskoptworker.models.Transaction;

import java.io.IOException;
import java.util.List;

public class AllActivityController {

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private VBox activityContainer;

    private Customer currentCustomer;

    @Setter
    private AllActivityListener listener;

    /**
     * Ustawia klienta i ładuje wszystkie jego aktywności
     */
    public void setCustomer(Customer customer, List<Transaction> transactions) {
        this.currentCustomer = customer;

        titleLabel.setText(customer.getFullName() + "'s Activity");
        subtitleLabel.setText("Complete parking history");

        loadActivities(transactions);
    }

    /**
     * Ładuje wszystkie aktywności do kontenera
     */
    private void loadActivities(List<Transaction> transactions) {
        activityContainer.getChildren().clear();

        if (transactions.isEmpty()) {
            Label noActivity = new Label("No parking activity found");
            noActivity.getStyleClass().add("customer-email-label");
            noActivity.setStyle("-fx-padding: 40; -fx-font-size: 16px;");
            activityContainer.getChildren().add(noActivity);
            return;
        }

        // Filtruj tylko parking sessions (completed + pending)
        List<Transaction> parkingSessions = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.PARKING_SESSION)
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.COMPLETED ||
                        t.getStatus() == Transaction.TransactionStatus.PENDING)
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()))
                .toList();

        for (Transaction transaction : parkingSessions) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/components/ActivityItem.fxml")
                );
                HBox activityItem = loader.load();

                parkflow.deskoptworker.Controllers.Components.ActivityItemController controller =
                        loader.getController();
                controller.setTransaction(transaction);

                activityContainer.getChildren().add(activityItem);

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load ActivityItem.fxml");
            }
        }
    }

    @FXML
    private void onClose() {
        if (listener != null) {
            listener.onCloseAllActivity();
        }
    }

    public interface AllActivityListener {
        void onCloseAllActivity();
    }
}