package parkflow.deskoptworker.Controllers.Admin;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import parkflow.deskoptworker.api.ParkingService;
import parkflow.deskoptworker.models.Parking;
import parkflow.deskoptworker.utils.AlertHelper;

public class PricingControllerA {

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
    @FXML private Button closeBtn;

    private Parking parking;
    private Long pricingId; // ID rekordu cennika w bazie
    private final ParkingService parkingService = new ParkingService();

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

        // Fetch pricingId from backend
        fetchPricingId();

        updateViewMode();
    }

    /**
     * Fetch pricingId from backend
     */
    private void fetchPricingId() {
        new Thread(() -> {
            try {
                // Get pricingId from backend
                Long fetchedPricingId = parkingService.getPricingIdByParkingId((long) parking.getId());

                Platform.runLater(() -> {
                    if (fetchedPricingId != null) {
                        pricingId = fetchedPricingId;
                        System.out.println("Fetched pricingId: " + pricingId + " for parkingId: " + parking.getId());
                    } else {
                        System.err.println("No pricingId found for parking " + parking.getId());
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.err.println("Failed to fetch pricingId: " + e.getMessage());
                });
            }
        }).start();
    }

    /**
     * Zamyka okno modalne
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
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

    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }

        // Check if we have pricingId
        if (pricingId == null) {
            AlertHelper.showError("Error", "Pricing ID not found. Please close and reopen this window.");
            return;
        }

        // Disable save button during save
        saveButton.setDisable(true);
        saveButton.setText("Saving...");

        // Get values from fields
        int freeMinutes = Integer.parseInt(freeMinutesField.getText());
        double ratePerMinute = Double.parseDouble(ratePerMinuteField.getText());
        double reservationFee = Double.parseDouble(reservationFeeField.getText());

        // Save in background thread
        new Thread(() -> {
            try {
                boolean success = parkingService.updatePricing(
                        pricingId, // Use correct pricingId
                        freeMinutes,
                        ratePerMinute,
                        reservationFee
                );

                Platform.runLater(() -> {
                    if (success) {
                        // Update local parking object
                        parking.setFreeMinutes(freeMinutes);
                        parking.setRatePerMinute(ratePerMinute);
                        parking.setReservationFee(reservationFee);

                        // Update view
                        updateViewMode();
                        setEditMode(false);

                        AlertHelper.showInfo("Success", "Pricing updated successfully!");
                    } else {
                        AlertHelper.showError("Error", "Failed to update pricing. Please try again.");
                    }

                    saveButton.setDisable(false);
                    saveButton.setText("Save Changes");
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertHelper.showError("Error", "An error occurred: " + e.getMessage());
                    saveButton.setDisable(false);
                    saveButton.setText("Save Changes");
                });
            }
        }).start();
    }

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

        closeBtn.setVisible(!isEdit);
        closeBtn.setManaged(!isEdit);
    }

    private void updateViewMode() {
        if (parking != null) {
            pricingTitle.setText("Pricing - " + parking.getName());
            freeMinutesLabel.setText(parking.getFreeMinutes() + " min");
            ratePerMinuteLabel.setText(String.format("%.2f PLN", parking.getRatePerMinute()));
            reservationFeeLabel.setText(String.format("%.2f PLN", parking.getReservationFee()));
        }
    }

    private boolean validateInputs() {
        if (freeMinutesField.getText().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Please enter free minutes!");
            return false;
        }

        if (ratePerMinuteField.getText().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Please enter rate per minute!");
            return false;
        }

        if (reservationFeeField.getText().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Please enter reservation fee!");
            return false;
        }

        try {
            int mins = Integer.parseInt(freeMinutesField.getText());
            if (mins < 0) {
                AlertHelper.showWarning("Validation Error", "Free minutes cannot be negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation Error", "Invalid free minutes value!");
            return false;
        }

        try {
            double rate = Double.parseDouble(ratePerMinuteField.getText());
            if (rate < 0) {
                AlertHelper.showWarning("Validation Error", "Rate per minute cannot be negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation Error", "Invalid rate per minute value!");
            return false;
        }

        try {
            double fee = Double.parseDouble(reservationFeeField.getText());
            if (fee < 0) {
                AlertHelper.showWarning("Validation Error", "Reservation fee cannot be negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation Error", "Invalid reservation fee value!");
            return false;
        }

        return true;
    }

    // Metoda do ustawienia nazwy parkingu (opcjonalnie)
    public void setParkingName(String name) {
        pricingTitle.setText("Pricing - " + name);
    }
}