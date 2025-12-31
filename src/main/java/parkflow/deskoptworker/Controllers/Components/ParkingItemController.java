package parkflow.deskoptworker.Controllers.Components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import parkflow.deskoptworker.Controllers.Admin.PricingController;
import parkflow.deskoptworker.Controllers.Worker.PricingWController;
import parkflow.deskoptworker.models.Parking;
import parkflow.deskoptworker.models.UserRole;
import parkflow.deskoptworker.utils.NavigationManager;

import java.io.IOException;

public class ParkingItemController {

    @FXML
    private Label idLabel;
    @FXML private Label nameLabel;
    @FXML private Label addressLabel;
    @FXML private Label spacesValueLabel;
    @FXML private Label availValueLabel;
    @FXML private Button viewPricingBtn;
    @FXML private Button reservationBtn;

    private Parking parking;
    private UserRole userRole;


    /**
     * Ustawia dane parkingu i konfiguruje widok na podstawie roli użytkownika
     */
    public void setData(Parking parking, UserRole role) {
        this.parking = parking;
        this.userRole = role;

        // Ustaw podstawowe dane
        idLabel.setText("#" + parking.getId());
        nameLabel.setText(parking.getName());
        addressLabel.setText(parking.getAddress());
        spacesValueLabel.setText(String.valueOf(parking.getTotalSpaces()));

        // Oblicz i ustaw dostępność
        setAvailability(parking.getAvailableSpaces(), parking.getTotalSpaces());

        // Konfiguruj przyciski na podstawie roli
        configureButtonsForRole(role);
    }

    /**
     * Oblicza procent dostępności i ustawia odpowiedni kolor i tekst
     */
    private void setAvailability(int available, int total) {
        if (total == 0) {
            availValueLabel.setText("• N/A");
            availValueLabel.getStyleClass().removeAll("red", "orange", "green");
            return;
        }

        double percentage = (double) available / total * 100;

        // Usuń poprzednie style kolorów
        availValueLabel.getStyleClass().removeAll("red", "orange", "green");

        if (percentage > 90) {
            availValueLabel.setText("• High");
            availValueLabel.getStyleClass().add("green");
        } else if (percentage > 75) {
            availValueLabel.setText("• Medium");
            availValueLabel.getStyleClass().add("orange");
        } else {
            availValueLabel.setText("• Low");
            availValueLabel.getStyleClass().add("red");
        }
    }

    /**
     * Konfiguruje widoczność i dostępność przycisków na podstawie roli
     */
    private void configureButtonsForRole(UserRole role) {
        if (role == UserRole.ADMIN) {
            // Admin nie ma przycisku rezerwacji
            reservationBtn.setVisible(false);
            reservationBtn.setManaged(false);
        } else {
            // Worker ma wszystkie przyciski
            reservationBtn.setVisible(true);
            reservationBtn.setManaged(true);
        }
    }

    /**
     * Obsługuje kliknięcie w "View pricing" - przechodzi do odpowiedniego widoku
     */
    @FXML
    private void handleViewPricing() {
        try {
            FXMLLoader loader;
            Parent root;

            if (userRole == UserRole.ADMIN) {
                // Dla admina - PricingController
                loader = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/admin/pricingDetailsA.fxml"));
                root = loader.load();

                PricingController controller = loader.getController();
                controller.setParkingData(parking);

            } else {
                // Dla workera - PricingWController
                loader = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/worker/pricingDetailsE.fxml"));
                root = loader.load();

                PricingWController controller = loader.getController();
                controller.setParkingData(parking);
            }

            // Utwórz nowe modalne okno
            Stage pricingStage = new Stage();
            pricingStage.setTitle("View Pricing - " + parking.getName());
            pricingStage.setScene(new Scene(root));

            // Ustaw rozmiar okna
            pricingStage.setWidth(580);
            pricingStage.setHeight(420);


            // Zablokuj zmianę rozmiaru
            pricingStage.setResizable(false);

            // Ustaw jako modalne (blokuje główne okno)
            pricingStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // Ustaw właściciela (parent window)
            pricingStage.initOwner(viewPricingBtn.getScene().getWindow());

            // Wyśrodkuj okno
            pricingStage.centerOnScreen();

            // Pokaż okno i czekaj aż się zamknie
            pricingStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading pricing details: " + e.getMessage());
        }
    }


    /**
     * Obsługuje kliknięcie "Reservations" - nawiguje do Customers → Reservations z filtrem parkingu
     */
    @FXML
    private void handleReservations() {
        System.out.println("Navigating to reservations for parking: " + parking.getName());

        // Użyj NavigationManager do nawigacji
        NavigationManager.getInstance().navigateToReservationsWithParkingFilter(parking);
    }
    /**
     * Obsługuje kliknięcie w ikonę mapy
     */
    @FXML
    private void handleMapClick() {
        // TODO: Otwórz mapę z lokalizacją parkingu
        System.out.println("Opening map for: " + parking.getName());
    }
}
