package parkflow.deskoptworker.Controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Getter;
import parkflow.deskoptworker.models.Reservation;

public class ReservationComponentController {

    @FXML private Label typeLabel;
    @FXML private Label reservationIdLabel;
    @FXML private Label customerNameLabel;
    @FXML private Label licenseLabel;
    @FXML private Label parkingSpotLabel;
    @FXML private Label dateLabel;
    @FXML private Label costLabel;

    @Getter
    private Reservation reservation;


    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        updateUI();
    }

    private void updateUI() {
        if (reservation == null) return;

        reservationIdLabel.setText(reservation.getFormattedId());
        customerNameLabel.setText(reservation.getCustomerName());
        licenseLabel.setText(reservation.getLicensePlate());
        parkingSpotLabel.setText(reservation.getParkingSpotCode());
        dateLabel.setText(reservation.getFormattedDate());
        costLabel.setText(reservation.getFormattedCost());

        updateStatusBadge();
    }

    private void updateStatusBadge() {
        // Usuń poprzednie klasy statusu
        typeLabel.getStyleClass().removeAll(
                "status-upcoming",
                "status-active",
                "status-completed"
        );

        String status = reservation.getStatusReservation();
        if (status == null) status = "upcoming";

        switch (status.toLowerCase()) {
            case "upcoming":
                typeLabel.setText("Upcoming");
                typeLabel.getStyleClass().add("status-upcoming");
                break;
            case "active":
                typeLabel.setText("Active");
                typeLabel.getStyleClass().add("status-active");
                break;
            case "completed":
                typeLabel.setText("Completed");
                typeLabel.getStyleClass().add("status-completed");
                break;
            default:
                typeLabel.setText(status);
                typeLabel.getStyleClass().add("status-upcoming");
        }
    }

}