package parkflow.deskoptworker.Controllers.sharedPanels.Reports;

import javafx.fxml.FXML;
import parkflow.deskoptworker.Controllers.Components.FilterBarController;
import parkflow.deskoptworker.Controllers.Components.MetricCardController;
import parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController;
import parkflow.deskoptworker.Controllers.Components.StatusCardController;

public class ReportReservationsController {

    // === TOP 4 METRIC CARDS (kolorowe z ikonkami) ===
    @FXML private MetricCardController totalReservationsCardController;
    @FXML private MetricCardController usedCardController;
    @FXML private MetricCardController utilizationCardController;
    @FXML private MetricCardController paidCardController;

    // === FILTER BAR ===
    @FXML private FilterBarController filterBarController;

    // === 3 STATUS CARDS (białe z ikonkami) ===
    @FXML private StatusCardController revenueCardController;
    @FXML private StatusCardController leadTimeCardController;
    @FXML private StatusCardController noShowCardController;

    // === 4 BREAKDOWN CARDS (SimpleMetricBox) ===
    @FXML private SimpleMetricBoxController usedBreakdownController;
    @FXML private SimpleMetricBoxController paidBreakdownController;
    @FXML private SimpleMetricBoxController expiredBreakdownController;
    @FXML private SimpleMetricBoxController successBreakdownController;

    @FXML
    public void initialize() {
        System.out.println("ReportReservationsController initialized");
        setupTopMetricCards();
        setupStatusCards();
        setupBreakdownCards();
    }

    /**
     * Setup top 4 kolorowych MetricCards
     */
    private void setupTopMetricCards() {
        // Total Reservations - Blue
        totalReservationsCardController.setData(
                "Total Reservations",
                "234",
                null,
                "/parkflow/deskoptworker/images/calendarWhite.png",
                "card-blue"
        );

        // Used - Green
        usedCardController.setData(
                "Used",
                "3102",
                null,
                "/parkflow/deskoptworker/images/checkWhite.png",
                "card-green"
        );

        // Utilization Rate - Orange
        utilizationCardController.setData(
                "Utilization Rate",
                "78.5 %",
                null,
                "/parkflow/deskoptworker/images/targetWhite.png",
                "card-orange"
        );

        // Paid (Active) - Purple
        paidCardController.setData(
                "Paid (Active)",
                "4521",
                null,
                "/parkflow/deskoptworker/images/clockWhite.png",
                "card-purple"
        );
    }

    /**
     * Setup 3 białych StatusCards z ikonkami
     */
    private void setupStatusCards() {
        // Reservation Revenue
        revenueCardController.setData(
                "Reservation Revenue",
                null,
                "6235.00 $",
                "From reservation fees",
                "/parkflow/deskoptworker/images/dollarGreen.png",
                "#E8F5E9"
        );

        // Avg Lead Time
        leadTimeCardController.setData(
                "Avg Lead Time",
                null,
                "18.5h",
                "Before parking starts",
                "/parkflow/deskoptworker/images/clockBlue.png",
                "#E3F2FD"
        );

        // No-Show Rate
        noShowCardController.setData(
                "No-Show Rate",
                null,
                "12.7%",
                "Expired without use",
                "/parkflow/deskoptworker/images/dollarOrange.png",
                "#FFF3E0"
        );
        noShowCardController.setValueColor("#EF4444"); // czerwony kolor wartości
    }

    /**
     * Setup 4 Breakdown cards (SimpleMetricBox)
     */
    private void setupBreakdownCards() {
        // Used - Green
        usedBreakdownController.setData("Used", "1024", "83.7% of total");
        usedBreakdownController.setCardType("green");

        // Paid (active) - Blue
        paidBreakdownController.setData("Paid (active)", "1145", "91.8% of total");
        paidBreakdownController.setCardType("blue");

        // Expired (No-Show) - Orange
        expiredBreakdownController.setData("Expired (No-Show)", "158", "12.7%");
        expiredBreakdownController.setCardType("orange");

        // Success Rate - Purple
        successBreakdownController.setData("Success Rate", "87.3%", "Utilization efficiency");
        successBreakdownController.setCardType("purple");
    }

    // ==================== PUBLIC UPDATE METHODS ====================
    // do aktualizacji z API

    /**
     * Aktualizuje wszystkie dane na stronie
     */
    public void updateAllData(ReservationMetricsData data) {
        updateTopMetrics(data);
        updateStatusCards(data);
        updateBreakdown(data);
    }

    /**
     * Aktualizuje top 4 MetricCards
     */
    public void updateTopMetrics(ReservationMetricsData data) {
        totalReservationsCardController.setValue(String.valueOf(data.totalReservations));
        usedCardController.setValue(String.valueOf(data.usedCount));
        utilizationCardController.setValue(String.format("%.1f %%", data.utilizationRate));
        paidCardController.setValue(String.valueOf(data.paidActiveCount));
    }

    /**
     * Aktualizuje 3 StatusCards
     */
    public void updateStatusCards(ReservationMetricsData data) {
        revenueCardController.setValue(String.format("%.2f $", data.reservationRevenue));
        revenueCardController.setChange(data.revenueChangeText);

        leadTimeCardController.setValue(String.format("%.1fh", data.avgLeadTimeHours));

        noShowCardController.setValue(String.format("%.1f%%", data.noShowRate));
    }

    /**
     * Aktualizuje 4 Breakdown cards
     */
    public void updateBreakdown(ReservationMetricsData data) {
        usedBreakdownController.setValue(String.valueOf(data.breakdownUsed));
        usedBreakdownController.setSubtitle(String.format("%.1f%% of total", data.breakdownUsedPercent));

        paidBreakdownController.setValue(String.valueOf(data.breakdownPaid));
        paidBreakdownController.setSubtitle(String.format("%.1f%% of total", data.breakdownPaidPercent));

        expiredBreakdownController.setValue(String.valueOf(data.breakdownExpired));
        expiredBreakdownController.setSubtitle(String.format("%.1f%%", data.breakdownExpiredPercent));

        successBreakdownController.setValue(String.format("%.1f%%", data.successRate));
    }

    /**
     * Refresh data based on filter selection
     */
    public void refreshData(String timePeriod, String parking) {
        System.out.println("Refreshing reservation data for: " + timePeriod + " | " + parking);
        // TODO: Pobierz dane z API i wywołaj updateAllData()
    }

    // ==================== DATA CLASS ====================

    /**
     * Klasa pomocnicza do przekazywania danych z API
     */
    public static class ReservationMetricsData {
        // Top metrics
        public int totalReservations;
        public int usedCount;
        public double utilizationRate;
        public int paidActiveCount;

        // Status cards
        public double reservationRevenue;
        public String revenueChangeText;
        public double avgLeadTimeHours;
        public double noShowRate;

        // Breakdown
        public int breakdownUsed;
        public double breakdownUsedPercent;
        public int breakdownPaid;
        public double breakdownPaidPercent;
        public int breakdownExpired;
        public double breakdownExpiredPercent;
        public double successRate;
    }
}