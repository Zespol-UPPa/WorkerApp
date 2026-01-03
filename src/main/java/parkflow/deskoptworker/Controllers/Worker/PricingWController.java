package parkflow.deskoptworker.Controllers.Worker;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import parkflow.deskoptworker.models.Parking;

public class PricingWController {
    @FXML private Label freeMinutesLabel;
    @FXML private Label ratePerMinLabel;
    @FXML private Label reservationFeeLabel;
    @FXML private Label pricingTitle;
    @FXML private Button closeBtn;

    private Parking parking;

    /**
     * Ustawia dane parkingu i wyświetla jego cennik
     */
    public void setParkingData(Parking parking) {
        this.parking = parking;
        updateViewMode();
    }

    /**
     * Aktualizuje widok z danymi z parkingu
     */
    private void updateViewMode() {
        if (parking != null) {
            pricingTitle.setText("Pricing - " + parking.getName());
            freeMinutesLabel.setText(parking.getFreeMinutes() + " min");
            ratePerMinLabel.setText(String.format("%.2f $", parking.getRatePerMinute()));
            reservationFeeLabel.setText(String.format("%.2f $", parking.getReservationFee()));
        }
    }

    /**
     * Zamyka okno modalne
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}