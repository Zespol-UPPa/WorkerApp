package parkflow.deskoptworker.Controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PerformanceItemController {

    @FXML private VBox root;
    @FXML private Label parkingNameLabel;
    @FXML private Label parkingAddressLabel;
    @FXML private Label occupancyPercentLabel;
    @FXML private Label occupancySpotsLabel;
    @FXML private Label revenueLabel;
    @FXML private Label sessionsLabel;
    @FXML private Label avgTimeLabel;
    @FXML private Label peakHourLabel;

    public void setData(ParkingPerformanceData data) {
        parkingNameLabel.setText(data.name);
        parkingAddressLabel.setText(data.address);
        occupancyPercentLabel.setText(data.occupancyPercent + "%");
        occupancySpotsLabel.setText(data.occupiedSpots + "/" + data.totalSpots + " spots");
        revenueLabel.setText(String.format("%.2f $", data.revenue));
        sessionsLabel.setText(String.valueOf(data.sessions));
        avgTimeLabel.setText(data.avgTime);
        peakHourLabel.setText(data.peakHour);

        updateOccupancyColor(data.occupancyPercent);
    }

    private void updateOccupancyColor(int percent) {
        // Remove all occupancy classes
        root.getStyleClass().removeAll("occupancy-low", "occupancy-medium", "occupancy-high");

        // Add appropriate class based on percentage
        if (percent >= 90) {
            root.getStyleClass().add("occupancy-high"); // Red
        } else if (percent >= 70) {
            root.getStyleClass().add("occupancy-medium"); // Orange/Yellow
        } else {
            root.getStyleClass().add("occupancy-low"); // Green
        }
    }

    public VBox getRoot() {
        return root;
    }

    // Helper class for data
    public static class ParkingPerformanceData {
        public String name;
        public String address;
        public int occupancyPercent;
        public int occupiedSpots;
        public int totalSpots;
        public double revenue;
        public int sessions;
        public String avgTime;
        public String peakHour;

        public ParkingPerformanceData(String name, String address, int occupancyPercent,
                                      int occupiedSpots, int totalSpots, double revenue,
                                      int sessions, String avgTime, String peakHour) {
            this.name = name;
            this.address = address;
            this.occupancyPercent = occupancyPercent;
            this.occupiedSpots = occupiedSpots;
            this.totalSpots = totalSpots;
            this.revenue = revenue;
            this.sessions = sessions;
            this.avgTime = avgTime;
            this.peakHour = peakHour;
        }
    }
}