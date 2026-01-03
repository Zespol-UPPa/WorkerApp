package parkflow.deskoptworker.Controllers.sharedPanels;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import parkflow.deskoptworker.Controllers.Components.MetricCardController;
import parkflow.deskoptworker.Controllers.Components.ParkingDashCompController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    // === TIME LABELS ===
    @FXML private Label currentTimeLabel;
    @FXML private Label currentDateLabel;

    // === TOP 3 METRIC CARDS ===
    @FXML private MetricCardController occupancyCardController;
    @FXML private MetricCardController revenueCardController;
    @FXML private MetricCardController customersCardController;

    // === PARKING CONTAINER ===
    @FXML private VBox parkingContainer;

    private Timeline clockTimeline;

    @FXML
    public void initialize() {
        setupTopMetricCards();
        loadParkingPerformance();

        if (currentTimeLabel != null && currentDateLabel != null) {
            startClock();
        } else {
            System.err.println("WARNING: currentTimeLabel lub currentDateLabel is NULL - nie można uruchomić zegara");
        }
    }

    /**
     * Setup top 3 Metric Cards
     */
    private void setupTopMetricCards() {
        // Current Occupancy - Orange
        occupancyCardController.setData(
                "Current Occupancy",
                "87 %",
                "3088/3550",
                "/parkflow/deskoptworker/images/target.png",
                "card-orange"
        );

        // Today's Revenue - Green
        revenueCardController.setData(
                "Today's Revenue",
                "32450.50 $",
                null,
                "/parkflow/deskoptworker/images/dollar.png",
                "card-green"
        );

        // Active Customers - Purple
        customersCardController.setData(
                "Active Customers",
                "287",
                "Activity in last month",
                "/parkflow/deskoptworker/images/group.png",
                "card-purple"
        );
    }

    // ==================== UPDATE METHODS ====================

    /**
     * Aktualizuje wszystkie dane dashboardu
     */
    public void updateAllData(DashboardData data) {
        updateTopMetrics(data);
        // TODO: updatePerformanceCards(data);
        // TODO: updateCharts(data);
    }

    /**
     * Aktualizuje top 3 Metric Cards
     */
    public void updateTopMetrics(DashboardData data) {
        occupancyCardController.setValue(String.format("%.0f %%", data.occupancyPercent));
        occupancyCardController.setSubtitle(String.format("%d/%d", data.occupiedSpots, data.totalSpots));

        revenueCardController.setValue(String.format("%.2f $", data.todayRevenue));

        customersCardController.setValue(String.valueOf(data.activeCustomers));
    }

    // ==================== CLOCK ====================

    private void startClock() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");

        clockTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, _ -> {
                    LocalDateTime now = LocalDateTime.now();
                    currentTimeLabel.setText(now.format(timeFormatter));
                    currentDateLabel.setText(now.format(dateFormatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );

        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    // ==================== PARKING PERFORMANCE ====================

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

    // ==================== DATA CLASS ====================

    /**
     * Klasa pomocnicza do przekazywania danych z API
     */
    public static class DashboardData {
        // Occupancy
        public double occupancyPercent;
        public int occupiedSpots;
        public int totalSpots;

        // Revenue
        public double todayRevenue;

        // Customers
        public int activeCustomers;

        // Performance (TODO)
        public int completedSessions;
        public int newReservations;
        public double walletDeposits;
        public double avgSessionTime;
        public int activeReservations;
        public double pendingPayments;
    }
}