package parkflow.deskoptworker;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import parkflow.deskoptworker.models.Parking;

public class PricingController {

    // View Mode Elements
    @FXML private HBox freeMinutesViewHBox;
    @FXML private HBox ratePerMinuteViewHBox;
    @FXML private HBox reservationFeeViewHBox;
    @FXML private Label freeMinutesLabel;
    @FXML private Label ratePerMinuteLabel;
    @FXML private Label reservationFeeLabel;
    @FXML private Button editButton;

    // Edit Mode Elements
    @FXML private HBox freeMinutesEditHBox;
    @FXML private HBox ratePerMinuteEditHBox;
    @FXML private HBox reservationFeeEditHBox;
    @FXML private TextField freeMinutesField;
    @FXML private TextField ratePerMinuteField;
    @FXML private TextField reservationFeeField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML private Label pricingTitle;


    private Parking parking;

    @FXML
    public void initialize() {


        // Walidacja - tylko liczby
        setupNumericValidation(freeMinutesField, false);
        setupNumericValidation(ratePerMinuteField, true);
        setupNumericValidation(reservationFeeField, true);
    }

    private void setupNumericValidation(TextField field, boolean allowDecimal) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (allowDecimal) {
                if (!newVal.matches("\\d*\\.?\\d*")) {
                    field.setText(oldVal);
                }
            } else {
                if (!newVal.matches("\\d*")) {
                    field.setText(oldVal);
                }
            }
        });
    }

    /**
     * Ustawia dane parkingu i wyświetla jego cennik
     */
    public void setParkingData(Parking parking) {
        this.parking = parking;
        updateViewMode();
    }


    @FXML
    private void handleEdit() {
        // Przejdź do Edit Mode
        setEditMode(true);

        // Wypełnij pola aktualnymi wartościami
        freeMinutesField.setText(String.valueOf(parking.getFreeMinutes()));
        ratePerMinuteField.setText(String.format("%.2f", parking.getRatePerMinute()));
        reservationFeeField.setText(String.format("%.2f", parking.getReservationFee()));
    }
//
//    @FXML
//    private void handleSave() {
//        // Walidacja
//        if (!validateInputs()) {
//            return;
//        }
//
//        // Zapisz nowe wartości
//        freeMinutes = Integer.parseInt(freeMinutesField.getText());
//        ratePerMinute = Double.parseDouble(ratePerMinuteField.getText());
//        reservationFee = Double.parseDouble(reservationFeeField.getText());
//
//        // TODO: Zapisz do bazy danych
//        System.out.println("=== Pricing Updated ===");
//        System.out.println("Free minutes: " + freeMinutes);
//        System.out.println("Rate per minute: $" + ratePerMinute);
//        System.out.println("Reservation fee: $" + reservationFee);
//
//        // Wróć do View Mode
//        updateViewMode();
//        setEditMode(false);
//
//        showAlert("Success", "Pricing updated successfully!");
//    }

    @FXML
    private void handleCancel() {
        // Wróć do View Mode bez zapisywania
        setEditMode(false);
    }

    private void setEditMode(boolean isEdit) {
        // Toggle widoczność elementów VIEW MODE
        freeMinutesViewHBox.setVisible(!isEdit);
        freeMinutesViewHBox.setManaged(!isEdit);
        ratePerMinuteViewHBox.setVisible(!isEdit);
        ratePerMinuteViewHBox.setManaged(!isEdit);
        reservationFeeViewHBox.setVisible(!isEdit);
        reservationFeeViewHBox.setManaged(!isEdit);
        editButton.setVisible(!isEdit);
        editButton.setManaged(!isEdit);

        // Toggle widoczność elementów EDIT MODE
        freeMinutesEditHBox.setVisible(isEdit);
        freeMinutesEditHBox.setManaged(isEdit);
        ratePerMinuteEditHBox.setVisible(isEdit);
        ratePerMinuteEditHBox.setManaged(isEdit);
        reservationFeeEditHBox.setVisible(isEdit);
        reservationFeeEditHBox.setManaged(isEdit);
        saveButton.setVisible(isEdit);
        saveButton.setManaged(isEdit);
        cancelButton.setVisible(isEdit);
        cancelButton.setManaged(isEdit);
    }

    private void updateViewMode() {
//        freeMinutesLabel.setText(freeMinutes + " min");
//        ratePerMinuteLabel.setText(String.format("%.2f $", ratePerMinute));
//        reservationFeeLabel.setText(String.format("%.2f $", reservationFee));
    }

    private boolean validateInputs() {
        if (freeMinutesField.getText().isEmpty()) {
            showAlert("Validation Error", "Please enter free minutes!");
            return false;
        }

        if (ratePerMinuteField.getText().isEmpty()) {
            showAlert("Validation Error", "Please enter rate per minute!");
            return false;
        }

        if (reservationFeeField.getText().isEmpty()) {
            showAlert("Validation Error", "Please enter reservation fee!");
            return false;
        }

        try {
            int mins = Integer.parseInt(freeMinutesField.getText());
            if (mins < 0) {
                showAlert("Validation Error", "Free minutes cannot be negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid free minutes value!");
            return false;
        }

        try {
            double rate = Double.parseDouble(ratePerMinuteField.getText());
            if (rate < 0) {
                showAlert("Validation Error", "Rate per minute cannot be negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid rate per minute value!");
            return false;
        }

        try {
            double fee = Double.parseDouble(reservationFeeField.getText());
            if (fee < 0) {
                showAlert("Validation Error", "Reservation fee cannot be negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid reservation fee value!");
            return false;
        }

        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }



    // Metoda do ustawienia nazwy parkingu (opcjonalnie)
    public void setParkingName(String name) {
        pricingTitle.setText("Pricing - " + name);
    }
}