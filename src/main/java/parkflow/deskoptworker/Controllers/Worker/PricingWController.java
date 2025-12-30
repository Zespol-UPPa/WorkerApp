package parkflow.deskoptworker.Controllers.Worker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import parkflow.deskoptworker.models.Parking;

public class PricingWController {
    @FXML
    private Label freeMinutesLabel;
    @FXML private Label ratePerMinLabel;
    @FXML private Label reservationFeeLabel;
    @FXML private Label pricingTitle;

    Parking parking;

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
            freeMinutesLabel.setText(parking.getFreeMinutes() + " min");
            ratePerMinLabel.setText(String.format("%.2f $", parking.getRatePerMinute()));
            reservationFeeLabel.setText(String.format("%.2f $", parking.getReservationFee()));
        }
    }
}
