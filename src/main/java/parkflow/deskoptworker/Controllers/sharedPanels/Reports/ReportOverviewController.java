package parkflow.deskoptworker.Controllers.sharedPanels.Reports;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.Controllers.Components.MetricCardController;
import parkflow.deskoptworker.Controllers.Components.PerformanceItemController;
import parkflow.deskoptworker.Controllers.Components.PerformanceItemController.ParkingPerformanceData;

public class ReportOverviewController {

    @FXML private GridPane metricsGrid;
    @FXML private AreaChart<String, Number> revenueChart;
    @FXML private LineChart<String, Number> occupancyChart;
    @FXML private VBox performanceContainer;

    @FXML
    public void initialize() {
        System.out.println("ReportOverviewController initialized");
        loadMetricCards();
        loadRevenueChart();
        loadOccupancyChart();
        loadParkingPerformance();
    }

    private void loadMetricCards() {
        // Card 1: Total Revenue (Green)
        addMetricCard(
                "Total Revenue",
                "5.20 $",
                "+25% vs last month",
                "/parkflow/deskoptworker/images/dollar.png",
                "card-green",
                0
        );

        // Card 2: Avg Occupancy (Blue)
        addMetricCard(
                "Avg Occupancy",
                "5.20 $",
                "1350/3540 spots",
                "/parkflow/deskoptworker/images/parking.png",
                "card-blue",
                1
        );

        // Card 3: Avg Session Time (Purple)
        addMetricCard(
                "Avg Session Time",
                "5.20 $",
                "19723 total sessions",
                "/parkflow/deskoptworker/images/clockWhite.png",
                "card-purple",
                2
        );

        // Card 4: Active customers (Orange)
        addMetricCard(
                "Active customers",
                "5.20 $",
                "+256 new this month",
                "/parkflow/deskoptworker/images/customersWhite.png",
                "card-orange",
                3
        );
    }

    private void addMetricCard(String title, String value, String subtitle,
                               String iconPath, String colorClass, int column) {
        try {
            // Try components folder first
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/MetricCard.fxml"));
            VBox cardRoot = loader.load();
            MetricCardController controller = loader.getController();
            controller.setData(title, value, subtitle, iconPath, colorClass);

            metricsGrid.add(cardRoot, column, 0);
        } catch (Exception e) {
            System.err.println("Failed to load metric card from components folder: " + e.getMessage());
            // Try reports folder as fallback
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/shared/reports/MetricCard.fxml"));
                VBox cardRoot = loader.load();
                MetricCardController controller = loader.getController();
                controller.setData(title, value, subtitle, iconPath, colorClass);

                metricsGrid.add(cardRoot, column, 0);
            } catch (Exception e2) {
                System.err.println("Failed to load metric card from reports folder too: " + e2.getMessage());
                e2.printStackTrace();
            }
        }
    }

    private void loadRevenueChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        // Sample data for 6 months
        series.getData().add(new XYChart.Data<>("Jul", 45000));
        series.getData().add(new XYChart.Data<>("Aug", 52000));
        series.getData().add(new XYChart.Data<>("Sep", 48000));
        series.getData().add(new XYChart.Data<>("Oct", 61000));
        series.getData().add(new XYChart.Data<>("Nov", 58000));
        series.getData().add(new XYChart.Data<>("Dec", 67000));

        revenueChart.getData().add(series);

        // Apply custom styling to the chart
        revenueChart.setCreateSymbols(false);
        revenueChart.lookup(".chart-series-area-fill").setStyle("-fx-fill: rgba(34, 197, 94, 0.3);");
        revenueChart.lookup(".chart-series-area-line").setStyle("-fx-stroke: #22c55e; -fx-stroke-width: 2px;");
    }

    private void loadOccupancyChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Occupancy");

        // Sample data for hourly occupancy pattern
        String[] hours = {"00:00", "02:00", "04:00", "06:00", "08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00"};
        int[] occupancy = {15, 10, 8, 20, 45, 65, 75, 85, 80, 70, 50, 30};

        for (int i = 0; i < hours.length; i++) {
            series.getData().add(new XYChart.Data<>(hours[i], occupancy[i]));
        }

        occupancyChart.getData().add(series);

        // Apply custom styling
        occupancyChart.setCreateSymbols(true);

        // Rotate X axis labels for better readability
        if (occupancyChart.getXAxis() instanceof CategoryAxis) {
            CategoryAxis xAxis = (CategoryAxis) occupancyChart.getXAxis();
            xAxis.setTickLabelRotation(45);
        }
    }

    private void loadParkingPerformance() {
        // Create 4 parking performance items
        ParkingPerformanceData[] parkings = {
                new ParkingPerformanceData(
                        "Galeria Krakowska",
                        "ul. Pawia 5, Krakow",
                        87,
                        1350,
                        1500,
                        18000.00,
                        342,
                        "2.8h",
                        "14:00"
                ),
                new ParkingPerformanceData(
                        "Downtown Plaza",
                        "Rynek Główny 1, Krakow",
                        96,
                        44,
                        50,
                        5200.00,
                        342,
                        "2.8h",
                        "14:00"
                ),
                new ParkingPerformanceData(
                        "Parking Podwawelski",
                        "Bernardynska 3, Krakow",
                        87,
                        1350,
                        1500,
                        18000.00,
                        342,
                        "2.8h",
                        "16:00"
                ),
                new ParkingPerformanceData(
                        "CH Bonarka",
                        "ul. Kamieńskiego 11, Krakow",
                        74,
                        890,
                        1200,
                        12500.00,
                        278,
                        "3.2h",
                        "15:00"
                )
        };

        for (ParkingPerformanceData parking : parkings) {
            try {
                // Try with your existing naming first
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/parkingPerfItem.fxml"));
                VBox itemRoot = loader.load();
                PerformanceItemController controller = loader.getController();
                controller.setData(parking);

                performanceContainer.getChildren().add(itemRoot);
            } catch (Exception e) {
              
                e.printStackTrace();
            }

        }
    }


public void refreshData() {
    System.out.println("Refreshing report data...");
    metricsGrid.getChildren().clear();
    performanceContainer.getChildren().clear();

    loadMetricCards();
    loadParkingPerformance();
}
}