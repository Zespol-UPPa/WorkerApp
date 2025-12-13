package parkflow.deskoptworker.Controllers.Reports;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.Controllers.Components.FilterBarController;
import parkflow.deskoptworker.Controllers.Components.MetricCardController;

import java.io.IOException;

public class ReportReservationsController {

    @FXML private GridPane metricsGrid;
    @FXML private GridPane breakdownGrid;
    @FXML private FilterBarController filterBarController;

    @FXML
    public void initialize() {
        System.out.println("ReportReservationsController initialized");
        setupTopMetrics();
        setupBreakdownMetrics();
    }

    /**
     * Setup top 4 metric cards
     */
    private void setupTopMetrics() {
        // Card 1: Total Reservations (Blue)
        MetricCardController totalCard = loadMetricCard(
                "Total Reservations",
                "234",
                "",
                "/parkflow/deskoptworker/images/calendarWhite.png",
                "card-blue"
        );
        if (totalCard != null) {
            metricsGrid.add(totalCard.getRoot(), 0, 0);
        }

        // Card 2: Used (Green)
        MetricCardController usedCard = loadMetricCard(
                "Used",
                "3102",
                "",
                "/parkflow/deskoptworker/images/checkWhite.png",
                "card-green"
        );
        if (usedCard != null) {
            metricsGrid.add(usedCard.getRoot(), 1, 0);
        }

        // Card 3: Utilization Rate (Orange)
        MetricCardController utilizationCard = loadMetricCard(
                "Utilization Rate",
                "78.5 %",
                "",
                "/parkflow/deskoptworker/images/targetWhite.png",
                "card-orange"
        );
        if (utilizationCard != null) {
            metricsGrid.add(utilizationCard.getRoot(), 2, 0);
        }

        // Card 4: Paid (Active) (Purple)
        MetricCardController paidCard = loadMetricCard(
                "Paid (Active)",
                "4521",
                "",
                "/parkflow/deskoptworker/images/clockWhite.png",
                "card-purple"
        );
        if (paidCard != null) {
            metricsGrid.add(paidCard.getRoot(), 3, 0);
        }
    }

    /**
     * Setup breakdown metrics using SimpleMetricBox with setCardType
     */
    private void setupBreakdownMetrics() {
        // Used - Green
        try {
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/SimpleMetricBox.fxml"));
            VBox box1 = loader1.load();
            parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController controller1 = loader1.getController();
            controller1.setData("Used", "1024","83.7 % of total");
            controller1.setCardType("green");
            breakdownGrid.add(box1, 0, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/SimpleMetricBox.fxml"));
            VBox box2 = loader2.load();
            parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController controller2 = loader2.getController();
            controller2.setData("Paid(active)", "1145","91.8% of total");
            controller2.setCardType("blue");
            breakdownGrid.add(box2, 1, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/SimpleMetricBox.fxml"));
            VBox box3 = loader3.load();
            parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController controller3 = loader3.getController();
            controller3.setData("Expired(No-Show)", "158","12.7%");
            controller3.setCardType("orange");
            breakdownGrid.add(box3, 2, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Success Rate - Purple
        try {
            FXMLLoader loader4 = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/SimpleMetricBox.fxml"));
            VBox box4 = loader4.load();
            parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController controller4 = loader4.getController();
            controller4.setData("Success Rate", "87.3%","Utilization efficiency");
            controller4.setCardType("purple");
            breakdownGrid.add(box4, 3, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load a metric card component
     */
    private MetricCardController loadMetricCard(String title, String value, String subtitle,
                                                String iconPath, String colorClass) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/components/MetricCard.fxml")
            );
            VBox card = loader.load();
            MetricCardController controller = loader.getController();
            controller.setData(title, value, subtitle, iconPath, colorClass);
            return controller;
        } catch (IOException e) {
            System.err.println("Error loading MetricCard '" + title + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}