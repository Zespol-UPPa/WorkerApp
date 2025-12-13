package parkflow.deskoptworker.Controllers.Reports;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.Controllers.Components.FilterBarController;
import parkflow.deskoptworker.Controllers.Components.MetricCardController;
import parkflow.deskoptworker.Controllers.Components.PerformanceItemController;
import parkflow.deskoptworker.Controllers.Components.PerformanceItemController.ParkingPerformanceData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ReportOccupancyController {

    @FXML private GridPane metricsGrid;
    @FXML private AreaChart<String, Number> hourlyOccupancyChart;
    @FXML private BarChart<String, Number> weeklyOccupancyChart;
    @FXML private GridPane peakHoursGrid;
    @FXML private VBox parkingPerformanceContainer;
    @FXML private FilterBarController filterBarController;

    @FXML
    public void initialize() {
        System.out.println("ReportOccupancyController initialized");
        setupTopMetrics();
        setupHourlyOccupancyChart();
        setupWeeklyOccupancyChart();
        setupPeakHoursCards();
        setupParkingPerformanceItems();
    }

    /**
     * Setup top 4 metric cards using MetricCard with colored gradients, NO ICONS
     */
    private void setupTopMetrics() {
        // Card 1: Current Occupancy (White card)
        MetricCardController currentCard = loadMetricCard(
                "Current Occupancy",
                "87 %",
                "3088/3550 spots",
                null,  // NO ICON
                "card-purple"
        );
        if (currentCard != null) {
            metricsGrid.add(currentCard.getRoot(), 0, 0);
        }

        // Card 2: Daily Average (Blue gradient)
        MetricCardController avgCard = loadMetricCard(
                "Daily Average",
                "72 %",
                "Consistent usage",
                null,  // NO ICON
                "card-blue"
        );
        if (avgCard != null) {
            metricsGrid.add(avgCard.getRoot(), 1, 0);
        }

        // Card 3: Peak Today (Red gradient)
        MetricCardController peakCard = loadMetricCard(
                "Peak Today",
                "95 %",
                "At 14:00",
                null,  // NO ICON
                "card-red"
        );
        if (peakCard != null) {
            metricsGrid.add(peakCard.getRoot(), 2, 0);
        }

        // Card 4: Low Today (Green gradient)
        MetricCardController lowCard = loadMetricCard(
                "Low Today",
                "45 %",
                "At 04:00",
                null,  // NO ICON
                "card-green"
        );
        if (lowCard != null) {
            metricsGrid.add(lowCard.getRoot(), 3, 0);
        }
    }

    /**
     * Setup hourly occupancy pattern area chart
     */
    private void setupHourlyOccupancyChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Occupancy");

        // Generate hourly data from 00:00 to 23:00
        int[] occupancyData = {15, 12, 10, 8, 10, 15, 25, 45, 65, 75, 82, 88, 92, 95, 93, 90, 85, 78, 70, 60, 50, 40, 30, 20};

        for (int hour = 0; hour < 24; hour++) {
            String timeLabel = String.format("%02d:00", hour);
            series.getData().add(new XYChart.Data<>(timeLabel, occupancyData[hour]));
        }

        hourlyOccupancyChart.getData().add(series);
        hourlyOccupancyChart.setAnimated(true);
    }

    /**
     * Setup weekly occupancy trend bar chart
     */
    private void setupWeeklyOccupancyChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Occupancy");

        // Weekly data: Mon, Tue, Wed, Thu, Fri, Sat, Sun
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        double[] occupancy = {85, 82, 88, 86, 92, 78, 68};

        for (int i = 0; i < days.length; i++) {
            series.getData().add(new XYChart.Data<>(days[i], occupancy[i]));
        }

        weeklyOccupancyChart.getData().add(series);
        weeklyOccupancyChart.setAnimated(true);
    }

    /**
     * Setup 3 peak hours analysis cards
     */
    private void setupPeakHoursCards() {
        // Peak Period 1
        VBox peak1 = createPeakHourCard("#1 Peak Period", "14:00-15:00", "201 active sessions");
        peakHoursGrid.add(peak1, 0, 0);

        // Peak Period 2
        VBox peak2 = createPeakHourCard("#2 Peak Period", "15:00-16:00", "201 active sessions");
        peakHoursGrid.add(peak2, 1, 0);

        // Peak Period 3
        VBox peak3 = createPeakHourCard("#3 Peak Period", "16:00-17:00", "201 active sessions");
        peakHoursGrid.add(peak3, 2, 0);
    }

    /**
     * Create a single peak hour card with pink/salmon background
     */
    private VBox createPeakHourCard(String title, String timeRange, String sessions) {
        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color: #fce4ec;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 20;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.08), 8, 0, 0, 2);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-text-fill: #880e4f;"
        );

        Label timeLabel = new Label(timeRange);
        timeLabel.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-text-fill: #ad1457;"
        );

        Label sessionsLabel = new Label(sessions);
        sessionsLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #c2185b;"
        );

        card.getChildren().addAll(titleLabel, timeLabel, sessionsLabel);
        return card;
    }

    /**
     * Setup individual parking performance items
     */
    private void setupParkingPerformanceItems() {
        // Example parking data
        List<ParkingPerformanceData> parkings = Arrays.asList(
                new ParkingPerformanceData(
                        "Galeria Krakowska",
                        "ul. Pawia 5, Kraków",
                        87,
                        1305,
                        1500,
                        1500.0,
                        342,
                        "2.8h",
                        "14:00"
                ),
                new ParkingPerformanceData(
                        "Bonarka City Center",
                        "ul. Kamieńskiego 11, Kraków",
                        72,
                        865,
                        1200,
                        1200.0,
                        256,
                        "3.2h",
                        "15:30"
                ),
                new ParkingPerformanceData(
                        "M1 Marki",
                        "ul. Warszawska 50, Marki",
                        65,
                        520,
                        800,
                        950.0,
                        198,
                        "2.5h",
                        "13:00"
                )
        );

        for (ParkingPerformanceData data : parkings) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/components/ParkingPerfItem.fxml")
                );
                VBox item = loader.load();
                PerformanceItemController controller = loader.getController();

                // Use the setData method from PerformanceItemController
                controller.setData(data);

                parkingPerformanceContainer.getChildren().add(item);
            } catch (IOException e) {
                System.err.println("Error loading ParkingPerfItem: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Load a metric card component WITHOUT ICON
     */
    private MetricCardController loadMetricCard(String title, String value, String subtitle,
                                                String iconPath, String colorClass) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/components/MetricCard.fxml")
            );
            VBox card = loader.load();
            MetricCardController controller = loader.getController();

            // Use the setData method - iconPath is null (NO ICONS)
            controller.setData(title, value, subtitle, iconPath, colorClass);

            return controller;
        } catch (IOException e) {
            System.err.println("Error loading MetricCard: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}