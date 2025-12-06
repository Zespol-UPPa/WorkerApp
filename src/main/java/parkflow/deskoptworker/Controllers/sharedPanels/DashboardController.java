package parkflow.deskoptworker.Controllers.sharedPanels;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import parkflow.deskoptworker.Controllers.ParkingDashCompController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {
    @FXML private Label currentTimeLabel;
    @FXML private Label currentDateLabel;

    @FXML
    private VBox parkingContainer;

    private Timeline clockTimeline;

    @FXML
    public void initialize() {
        loadParkingPerformance();
        if (currentTimeLabel != null && currentDateLabel != null) {
            startClock();
        } else {
            System.err.println("WARNING: currentTimeLabel lub currentDateLabel is NULL - nie można uruchomić zegara");
        }
    }

    private void startClock() {
        // Formatery dla czasu i daty
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a"); // 06:09:58 PM
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"); // Tuesday, 25 November 2025

        // Timeline - odświeża co sekundę
        clockTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> {
                    LocalDateTime now = LocalDateTime.now();
                    currentTimeLabel.setText(now.format(timeFormatter));
                    currentDateLabel.setText(now.format(dateFormatter));
                }),
                new KeyFrame(Duration.seconds(1)) // Co 1 sekundę
        );

        clockTimeline.setCycleCount(Animation.INDEFINITE); // W nieskończoność
        clockTimeline.play(); // Start
    }

    private void loadParkingPerformance() {
        // Przykładowe dane - później z bazy danych
        addParkingItem(1, "Galeria Krakowska", 8900, 342, 87);
        addParkingItem(2, "Galeria Krakowska", 7480, 342, 90);
        addParkingItem(3, "Galeria Krakowska", 4200, 342, 87);
    }
    private void addParkingItem(int number, String name, double revenue, int sessions, int percent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/parking_component_dash.fxml"));
            VBox parkingItem = loader.load();

            ParkingDashCompController controller = loader.getController();
            controller.setData(number, name, revenue, sessions, percent);

            parkingContainer.getChildren().add(parkingItem);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
