package parkflow.deskoptworker.Controllers.sharedPanels.Reports;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.Controllers.Components.FilterBarController;
import parkflow.deskoptworker.Controllers.Components.MetricCardController;
import parkflow.deskoptworker.Controllers.Components.PerformanceItemController;
import parkflow.deskoptworker.Controllers.Components.PerformanceItemController.ParkingPerformanceData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ReportOccupancyController {

    // Top 3 Metric Cards (white cards, no icons)
    @FXML private MetricCardController currentOccupancyCardController;
    @FXML private MetricCardController peakTodayCardController;
    @FXML private MetricCardController lowTodayCardController;



    // Charts
    @FXML private AreaChart<String, Number> hourlyOccupancyChart;
    @FXML private BarChart<String, Number> weeklyOccupancyChart;

    // Parking Performance Container
    @FXML private VBox parkingPerformanceContainer;

    @FXML
    public void initialize() {
        System.out.println("ReportOccupancyController initialized");
        setupTopMetrics();
        setupHourlyOccupancyChart();
        setupWeeklyOccupancyChart();
        setupParkingPerformanceItems();
    }

    /**
     * Setup top 3 metric cards (white cards, no icons)
     */
    private void setupTopMetrics() {
        // Current Occupancy
        currentOccupancyCardController.setData(
                "Current Occupancy",
                "87 %",
                "3088/3550 spots",
                null,  // NO ICON
                "card-white"
        );

        // Peak Today
        peakTodayCardController.setData(
                "Peak Today",
                "95 %",
                "At 14:00",
                null,  // NO ICON
                "card-white"
        );
        peakTodayCardController.setValueColor("value-red");

        // Low Today
        lowTodayCardController.setData(
                "Low Today",
                "45 %",
                "At 04:00",
                null,  // NO ICON
                "card-white"
        );
        lowTodayCardController.setValueColor("value-blue");
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
     * Refresh data based on filter selection
     */
    public void refreshData(String timePeriod, String parking) {
        System.out.println("Refreshing occupancy data for: " + timePeriod + " | " + parking);
        // TODO: Fetch data from API and update charts/metrics
    }
}