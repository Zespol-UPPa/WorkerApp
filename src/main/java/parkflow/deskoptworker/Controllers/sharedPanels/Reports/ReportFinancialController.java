package parkflow.deskoptworker.Controllers.sharedPanels.Reports;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController;
import parkflow.deskoptworker.Controllers.Components.StatusCardController;
import parkflow.deskoptworker.api.PaymentService;
import parkflow.deskoptworker.models.UserRole;
import parkflow.deskoptworker.utils.SessionManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Financial Report Controller - LAST 6 MONTHS
 * Shows company-wide financial data for the last 6 months
 * No filters - fixed period and all parkings
 */
public class ReportFinancialController {

    @FXML private StatusCardController totalRevenueCardController;
    @FXML private StatusCardController parkingUsageCardController;
    @FXML private StatusCardController pendingPaymentsCardController;
    @FXML private StatusCardController reservationFeesStatusCardController;

    @FXML private BarChart<String, Number> revenueUsageChart;
    @FXML private PieChart revenueDistributionChart;
    @FXML private VBox pieChartContainer;

    @FXML private SimpleMetricBoxController avgTransactionCardController;
    @FXML private SimpleMetricBoxController totalTransactionsCardController;
    @FXML private SimpleMetricBoxController reservationFeesCardController;
    @FXML private SimpleMetricBoxController revenueGrowthCardController;

    private final PaymentService paymentService = new PaymentService();
    private UserRole currentUserRole;

    // FIXED: Last 6 months, all parkings
    private static final String BACKEND_PERIOD = "semester";
    private static final String ALL_PARKINGS = null;

    @FXML
    public void initialize() {
        currentUserRole = SessionManager.getInstance().getCurrentUser().getRole();
        setupUIForRole();
        loadInitialData();
    }

    private void setupUIForRole() {
        if (currentUserRole == UserRole.WORKER && pieChartContainer != null) {
            pieChartContainer.setVisible(false);
            pieChartContainer.setManaged(false);
        }
    }

    private void loadInitialData() {
        System.out.println("Loading financial data: Last 6 Months | All Parkings");
        refreshData();
    }

    public void refreshData() {
        try {
            Map<String, Object> summary = paymentService.getFinancialSummary(BACKEND_PERIOD, ALL_PARKINGS);

            setupTopStatusCards(summary);
            setupRevenueUsageChart();

            if (currentUserRole == UserRole.ADMIN) {
                setupRevenueDistributionChart();
            }

            setupKeyMetrics(summary);
            System.out.println("Financial data loaded successfully");

        } catch (Exception e) {
            System.err.println("Error loading financial data: " + e.getMessage());
            e.printStackTrace();
            setupTopStatusCards(new HashMap<>());
            setupRevenueUsageChart();

            if (currentUserRole == UserRole.ADMIN) {
                setupRevenueDistributionChart();
            }

            setupKeyMetrics(new HashMap<>());
        }
    }

    private void setupTopStatusCards(Map<String, Object> summary) {
        double totalRevenue = PaymentService.getDoubleValue(summary, "totalRevenue");
        double parkingUsage = PaymentService.getDoubleValue(summary, "parkingUsage");
        double pendingPayments = PaymentService.getDoubleValue(summary, "pendingPayments");
        double reservationFees = PaymentService.getDoubleValue(summary, "reservationFees");
        double revenueGrowth = PaymentService.getDoubleValue(summary, "revenueGrowth");
        int totalTransactions = PaymentService.getIntValue(summary, "totalTransactions");

        totalRevenueCardController.setData(
                "Total Revenue", "(Last 6 months)",
                String.format("%.2f PLN", totalRevenue),
                String.format("%+.1f%% vs previous", revenueGrowth),
                "/parkflow/deskoptworker/images/dollarGreen.png", "#E8F5E9"
        );

        parkingUsageCardController.setData(
                "Parking Usage", "(Finalized)",
                String.format("%.2f PLN", parkingUsage),
                totalTransactions + " transactions",
                "/parkflow/deskoptworker/images/clockBlue.png", "#E3F2FD"
        );

        pendingPaymentsCardController.setData(
                "Pending Payments", "(Awaiting)",
                String.format("%.2f PLN", pendingPayments),
                "To be collected",
                "/parkflow/deskoptworker/images/dollarOrange.png", "#FFF3E0"
        );

        reservationFeesStatusCardController.setData(
                "Reservation Fees", "(From reservations)",
                String.format("%.2f PLN", reservationFees),
                "Revenue from fees",
                "/parkflow/deskoptworker/images/calendarPurple.png", "#F3E5F5"
        );
    }

    private void setupRevenueUsageChart() {
        try {
            List<Map<String, Object>> revenueData = paymentService.getRevenueOverTime(BACKEND_PERIOD, ALL_PARKINGS);

            if (revenueData == null || revenueData.isEmpty()) {
                System.out.println("No revenue data - using mock");
                setupMockRevenueChart();
                return;
            }

            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            revenueSeries.setName("Total Revenue");

            XYChart.Series<String, Number> usageSeries = new XYChart.Series<>();
            usageSeries.setName("Parking Usage");

            for (Map<String, Object> dataPoint : revenueData) {
                String period = PaymentService.getStringValue(dataPoint, "period");
                double revenue = PaymentService.getDoubleValue(dataPoint, "totalRevenue");
                double usage = PaymentService.getDoubleValue(dataPoint, "parkingUsage");

                revenueSeries.getData().add(new XYChart.Data<>(period, revenue));
                usageSeries.getData().add(new XYChart.Data<>(period, usage));
            }

            revenueUsageChart.getData().clear();
            revenueUsageChart.getData().addAll(revenueSeries, usageSeries);
            revenueUsageChart.setLegendVisible(true);

        } catch (Exception e) {
            System.err.println("Error setting up revenue chart: " + e.getMessage());
            setupMockRevenueChart();
        }
    }

    private void setupMockRevenueChart() {
        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Total Revenue");
        XYChart.Series<String, Number> usageSeries = new XYChart.Series<>();
        usageSeries.setName("Parking Usage");

        String[] months = {"Aug", "Sep", "Oct", "Nov", "Dec", "Jan"};
        double[] revenues = {35000, 42000, 40000, 43000, 45000, 47000};
        double[] usages = {31000, 35000, 32000, 36000, 38000, 40000};

        for (int i = 0; i < months.length; i++) {
            revenueSeries.getData().add(new XYChart.Data<>(months[i], revenues[i]));
            usageSeries.getData().add(new XYChart.Data<>(months[i], usages[i]));
        }

        revenueUsageChart.getData().clear();
        revenueUsageChart.getData().addAll(revenueSeries, usageSeries);
        revenueUsageChart.setLegendVisible(true);
    }

    private void setupRevenueDistributionChart() {
        try {
            List<Map<String, Object>> distribution = paymentService.getRevenueDistribution(BACKEND_PERIOD);

            if (distribution == null || distribution.isEmpty()) {
                System.out.println("No distribution data - using mock");
                setupMockPieChart();
                return;
            }

            Map<String, Double> parkingRevenues = new LinkedHashMap<>();

            for (Map<String, Object> item : distribution) {
                String parkingName = PaymentService.getStringValue(item, "parkingName");
                double revenue = PaymentService.getDoubleValue(item, "revenue");

                if (!parkingName.isEmpty() && revenue > 0) {
                    parkingRevenues.put(parkingName, revenue);
                }
            }

            if (parkingRevenues.isEmpty()) {
                setupMockPieChart();
                return;
            }

            createSmartPieChart(parkingRevenues);

        } catch (Exception e) {
            System.err.println("Error setting up pie chart: " + e.getMessage());
            setupMockPieChart();
        }
    }

    private void setupMockPieChart() {
        Map<String, Double> parkingRevenues = new LinkedHashMap<>();
        parkingRevenues.put("Parking A", 95000.0);
        parkingRevenues.put("Parking B", 78000.0);
        parkingRevenues.put("Parking C", 65000.0);
        parkingRevenues.put("Parking D", 34000.0);

        createSmartPieChart(parkingRevenues);
    }

    private void createSmartPieChart(Map<String, Double> data) {
        List<Map.Entry<String, Double>> sortedEntries = data.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        double total = sortedEntries.stream().mapToDouble(Map.Entry::getValue).sum();

        revenueDistributionChart.getData().clear();

        if (sortedEntries.size() <= 4) {
            for (Map.Entry<String, Double> entry : sortedEntries) {
                double percentage = (entry.getValue() / total) * 100;
                String label = String.format("%s: %.1f%%", entry.getKey(), percentage);
                revenueDistributionChart.getData().add(new PieChart.Data(label, entry.getValue()));
            }
        } else {
            double otherTotal = 0;
            for (int i = 0; i < sortedEntries.size(); i++) {
                Map.Entry<String, Double> entry = sortedEntries.get(i);
                if (i < 3) {
                    double percentage = (entry.getValue() / total) * 100;
                    String label = String.format("%s: %.1f%%", entry.getKey(), percentage);
                    revenueDistributionChart.getData().add(new PieChart.Data(label, entry.getValue()));
                } else {
                    otherTotal += entry.getValue();
                }
            }
            if (otherTotal > 0) {
                double otherPercentage = (otherTotal / total) * 100;
                revenueDistributionChart.getData().add(
                        new PieChart.Data(String.format("Other: %.1f%%", otherPercentage), otherTotal)
                );
            }
        }

        styleChartSegments();
    }

    private void styleChartSegments() {
        Platform.runLater(() -> {
            String[] colors = {"#4D49E5", "#A34DE9", "#D5297E", "#FA7017"};
            int i = 0;

            for (PieChart.Data data : revenueDistributionChart.getData()) {
                if (data.getNode() != null) {
                    String color = colors[Math.min(i, colors.length - 1)];

                    data.getNode().setStyle("-fx-pie-color: " + color);

                    i++;
                }
            }
        });
    }

    private void setupKeyMetrics(Map<String, Object> summary) {
        double avgTransaction = PaymentService.getDoubleValue(summary, "avgTransactionValue");
        int totalTransactions = PaymentService.getIntValue(summary, "totalTransactions");
        double reservationFees = PaymentService.getDoubleValue(summary, "reservationFees");
        double revenueGrowth = PaymentService.getDoubleValue(summary, "revenueGrowth");

        avgTransactionCardController.setData("Avg Transaction", String.format("%.2f PLN", avgTransaction));
        avgTransactionCardController.setCardType("green");

        totalTransactionsCardController.setData("Total Transactions", String.valueOf(totalTransactions));
        totalTransactionsCardController.setCardType("blue");

        reservationFeesCardController.setData("Reservation Fees", String.format("%.2f PLN", reservationFees));
        reservationFeesCardController.setCardType("purple");

        revenueGrowthCardController.setData("Revenue Growth", String.format("%+.1f %%", revenueGrowth));
        revenueGrowthCardController.setCardType("orange");
    }
}