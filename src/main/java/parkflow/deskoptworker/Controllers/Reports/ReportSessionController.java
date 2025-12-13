package parkflow.deskoptworker.Controllers.Reports;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import parkflow.deskoptworker.Controllers.Components.FilterBarController;
import parkflow.deskoptworker.Controllers.Components.MetricCardController;

public class ReportSessionController {

    @FXML private FilterBarController filterBarController;
    @FXML private BarChart<String, Number> durationDistributionChart;
    @FXML private AreaChart<String, Number> hourlyActivityChart;

    @FXML private MetricCardController avgDurationCardController;
    @FXML private MetricCardController totalSessionsCardController;
    @FXML private MetricCardController activeNowCardController;
    @FXML private MetricCardController completedCardController;

    // Duration Breakdown - Item 1
    @FXML private Label count1Label;
    @FXML private Label percent1Label;
    @FXML private Region track1;
    @FXML private StackPane bar1;

    // Duration Breakdown - Item 2
    @FXML private Label count2Label;
    @FXML private Label percent2Label;
    @FXML private Region track2;
    @FXML private StackPane bar2;

    // Duration Breakdown - Item 3
    @FXML private Label count3Label;
    @FXML private Label percent3Label;
    @FXML private Region track3;
    @FXML private StackPane bar3;

    // Duration Breakdown - Item 4
    @FXML private Label count4Label;
    @FXML private Label percent4Label;
    @FXML private Region track4;
    @FXML private StackPane bar4;

    // Duration Breakdown - Item 5
    @FXML private Label count5Label;
    @FXML private Label percent5Label;
    @FXML private Region track5;
    @FXML private StackPane bar5;

    @FXML
    public void initialize() {
        setupMetricCards();
        setupDurationDistributionChart();
        setupDurationBreakdown();
        setupHourlyActivityChart();
        setupFilterListener();
    }

    private void setupFilterListener() {
        if (filterBarController != null) {
            filterBarController.setFilterChangeListener((timePeriod, parking) -> {
                System.out.println("Filters changed: " + timePeriod + " | " + parking);
                refreshData(timePeriod, parking);
            });
        }
    }

    private void setupMetricCards() {
        // Avg Duration - Blue
        avgDurationCardController.setData(
                "Avg Duration",
                "1.6h",
                "",
                null, // no icon
                "card-blue"
        );

        // Total Sessions - Green
        totalSessionsCardController.setData(
                "Total Sessions",
                "1200",
                "",
                null,
                "card-green"
        );

        // Active Now - Purple
        activeNowCardController.setData(
                "Active Now",
                "124",
                "",
                null,
                "card-purple"
        );

        // Completed - Orange
        completedCardController.setData(
                "Completed",
                "1076",
                "",
                null,
                "card-orange"
        );
    }

    private void setupDurationDistributionChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.getData().add(new XYChart.Data<>("0-30min", 1200));
        series.getData().add(new XYChart.Data<>("30min-1h", 1800));
        series.getData().add(new XYChart.Data<>("1-2h", 2400));
        series.getData().add(new XYChart.Data<>("2-4h", 2100));
        series.getData().add(new XYChart.Data<>("4h+", 1000));

        durationDistributionChart.getData().add(series);
    }

    private void setupDurationBreakdown() {
        // Sample data - update all 5 bars
        updateDurationBar(1, 14, 1245, count1Label, percent1Label, track1, bar1);
        updateDurationBar(2, 20, 1784, count2Label, percent2Label, track2, bar2);
        updateDurationBar(3, 30, 2676, count3Label, percent3Label, track3, bar3);
        updateDurationBar(4, 24, 2141, count4Label, percent4Label, track4, bar4);
        updateDurationBar(5, 12, 1077, count5Label, percent5Label, track5, bar5);
    }

    private void updateDurationBar(int index, int percentage, int count,
                                   Label countLabel, Label percentLabel,
                                   Region track, StackPane bar) {
        if (countLabel == null || percentLabel == null || track == null || bar == null) {
            System.err.println("ERROR: Null elements in bar " + index);
            return;
        }

        countLabel.setText(String.valueOf(count));
        percentLabel.setText(percentage + "%");


        bar.prefWidthProperty().unbind();
        bar.maxWidthProperty().unbind();

        bar.prefWidthProperty().bind(
                track.widthProperty().multiply(percentage / 100.0)
        );
        bar.maxWidthProperty().bind(
                track.widthProperty().multiply(percentage / 100.0)
        );
    }

    private void setupHourlyActivityChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Sample data - hourly pattern
        String[] hours = {"00:00", "02:00", "04:00", "06:00", "08:00", "10:00",
                "12:00", "14:00", "16:00", "18:00", "20:00", "22:00"};
        int[] values = {50, 30, 20, 40, 90, 140, 180, 200, 190, 160, 110, 70};

        for (int i = 0; i < hours.length; i++) {
            series.getData().add(new XYChart.Data<>(hours[i], values[i]));
        }

        hourlyActivityChart.getData().add(series);
    }

    private void refreshData(String timePeriod, String parking) {
        // TODO: Implement data refresh based on filters
        System.out.println("Refreshing data for: " + timePeriod + " | " + parking);
        //aktualizacja metryk i wykresow
    }
}