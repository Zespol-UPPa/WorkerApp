package parkflow.deskoptworker.Controllers.Reports;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController;
import parkflow.deskoptworker.Controllers.Components.StatusCardController;


import java.util.*;
import java.util.stream.Collectors;

public class ReportFinancialController {

    // Top 3 STATUS CARDS (z ikonkami)
    @FXML private StatusCardController totalRevenueCardController;
    @FXML private StatusCardController parkingUsageCardController;
    @FXML private StatusCardController pendingPaymentsCardController;

    // Charts
    @FXML private BarChart<String, Number> revenueUsageChart;
    @FXML private PieChart revenueDistributionChart;

    // Key Metrics - 4 SIMPLE METRIC CARDS
    @FXML private SimpleMetricBoxController avgTransactionCardController;
    @FXML private SimpleMetricBoxController totalTransactionsCardController;
    @FXML private SimpleMetricBoxController reservationFeesCardController;
    @FXML private SimpleMetricBoxController revenueGrowthCardController;

    @FXML
    public void initialize() {
        setupTopStatusCards();
        setupRevenueUsageChart();
        setupRevenueDistributionChart();
        setupKeyMetrics();
    }

    /**
     * Ustawia górne 3 karty statusu z ikonkami
     */
    private void setupTopStatusCards() {

        totalRevenueCardController.setData(
                "Total Revenue",
                "(All payments)",
                "45780.50 $",
                "+12.5% from last month",
                "/parkflow/deskoptworker/images/dollarGreen.png",
                "#E8F5E9"  // Light green background
        );

        // Parking Usage (From Wallets) - z ikoną clockBlue.png
        parkingUsageCardController.setData(
                "Parking Usage",
                "(Finalized)",
                "28920.00 $",
                "1247 transactions",
                "/parkflow/deskoptworker/images/clockBlue.png",
                "#E3F2FD"  // Light blue background
        );

        // Pending Payments (Awaiting Customer) - z ikoną dollarOrange.png
        pendingPaymentsCardController.setData(
                "Pending Payments",
                "(Awaiting Customer)",
                "2340.00 $",
                "To be collected",
                "/parkflow/deskoptworker/images/dollarOrange.png",
                "#FFF3E0"  // Light orange background
        );
    }

    private void setupRevenueUsageChart() {
        // Sample data - Revenue (Deposits) vs Usage (Expenses)
        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Revenue");

        XYChart.Series<String, Number> usageSeries = new XYChart.Series<>();
        usageSeries.setName("Finalized payments");

        String[] months = {"Jun", "Jul", "Aug", "Sep", "Oct", "Nov"};
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
        // Sample parking revenue data (sorted by revenue descending)
        Map<String, Double> parkingRevenues = new LinkedHashMap<>();
        parkingRevenues.put("Galeria Krakowska", 18900.00);
        parkingRevenues.put("CH Bonarka", 13480.50);
        parkingRevenues.put("Podwawelski", 10200.00);
        parkingRevenues.put("Downtown Plaza", 3200.00);

        // Smart PieChart: max 4 kategorie + "Other"
        createSmartPieChart(parkingRevenues);
    }

    /**
     * Tworzy PieChart z max 4 kategoriami + "Other" jeśli jest więcej parkingów
     */
    private void createSmartPieChart(Map<String, Double> data) {
        // Sortuj po wartości malejąco
        List<Map.Entry<String, Double>> sortedEntries = data.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        // Oblicz total
        double total = sortedEntries.stream()
                .mapToDouble(Map.Entry::getValue)
                .sum();

        // Jeśli 4 lub mniej - pokaż wszystkie
        if (sortedEntries.size() <= 4) {
            for (Map.Entry<String, Double> entry : sortedEntries) {
                double percentage = (entry.getValue() / total) * 100;
                String label = String.format("%s: %.1f%%", entry.getKey(), percentage);
                revenueDistributionChart.getData().add(
                        new PieChart.Data(label, entry.getValue())
                );
            }
        } else {
            // Więcej niż 4 - weź top 3 + "Other"
            double otherTotal = 0;

            for (int i = 0; i < sortedEntries.size(); i++) {
                Map.Entry<String, Double> entry = sortedEntries.get(i);

                if (i < 3) {
                    // Top 3 - pokaż indywidualnie
                    double percentage = (entry.getValue() / total) * 100;
                    String label = String.format("%s: %.1f%%", entry.getKey(), percentage);
                    revenueDistributionChart.getData().add(
                            new PieChart.Data(label, entry.getValue())
                    );
                } else {
                    // Reszta idzie do "Other"
                    otherTotal += entry.getValue();
                }
            }

            // Dodaj "Other" jeśli jest coś
            if (otherTotal > 0) {
                double otherPercentage = (otherTotal / total) * 100;
                String otherLabel = String.format("Other: %.1f%%", otherPercentage);
                revenueDistributionChart.getData().add(
                        new PieChart.Data(otherLabel, otherTotal)
                );
            }
        }

        // Ustaw kolory dla segmentów
        styleChartSegments();
    }

    private void styleChartSegments() {
        // Kolory dla segmentów (blue, purple, pink, orange)
        String[] colors = {
                "#4D49E5", // blue
                "#A34DE9", // purple
                "#D5297E", // pink
                "#FA7017"  // orange (dla "Other")
        };

        int i = 0;
        for (PieChart.Data data : revenueDistributionChart.getData()) {
            String color = colors[Math.min(i, colors.length - 1)];
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            i++;
        }
    }

    /**
     * Ustawia dolne 4 karty metryk (Simple Metric Cards)
     */
    private void setupKeyMetrics() {
        // Avg Transaction Value - zielony
        avgTransactionCardController.setData("Avg Transaction Value", "36.70 $");
        avgTransactionCardController.setCardType("green");

        // Total Transactions - niebieski
        totalTransactionsCardController.setData("Total Transactions", "1247");
        totalTransactionsCardController.setCardType("blue");

        // Reservation Fees - fioletowy
        reservationFeesCardController.setData("Reservation Fees", "6235.00 $");
        reservationFeesCardController.setCardType("purple");

        // Revenue Growth - pomarańczowy (pozytywny wzrost)
        revenueGrowthCardController.setData("Revenue Growth", "+12.5 %");
        revenueGrowthCardController.setCardType("orange");
    }

    public void refreshData(String timePeriod, String parking) {
        System.out.println("Refreshing financial data for: " + timePeriod + " | " + parking);
        // Tutaj pobierz dane z bazy i wywołaj setup metody ponownie
    }
}