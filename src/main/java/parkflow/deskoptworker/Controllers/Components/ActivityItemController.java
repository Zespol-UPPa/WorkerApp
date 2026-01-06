package parkflow.deskoptworker.Controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import parkflow.deskoptworker.models.Transaction;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ActivityItemController {

    @FXML private ImageView activityIcon;
    @FXML private Label parkingNameLabel;
    @FXML private Label dateLabel;
    @FXML private Label statusBadge;
    @FXML private Label amountLabel;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Ustawia dane transakcji
     */
    public void setTransaction(Transaction transaction) {
        // Icon
        try {
            activityIcon.setImage(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/parkflow/deskoptworker/images/parking.png")
            )));
        } catch (Exception e) {
            System.err.println("Failed to load parking icon");
        }

        // Parking name
        parkingNameLabel.setText(transaction.getParkingName());

        // Date
        dateLabel.setText(transaction.getTransactionDate().format(DATE_FORMATTER));

        // Status badge
        statusBadge.setText(transaction.getStatusDisplayName());
        if (transaction.getStatus() == Transaction.TransactionStatus.COMPLETED) {
            statusBadge.getStyleClass().add("transaction-status-completed");
        } else if (transaction.getStatus() == Transaction.TransactionStatus.PENDING) {
            statusBadge.getStyleClass().add("transaction-status-pending");
        } else {
            statusBadge.getStyleClass().add("transaction-status-default");
        }

        // Amount
        amountLabel.setText(String.format("-%.2f $", transaction.getAmount()));
    }
}