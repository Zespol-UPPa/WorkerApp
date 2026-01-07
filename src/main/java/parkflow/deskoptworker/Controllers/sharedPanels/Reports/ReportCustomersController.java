package parkflow.deskoptworker.Controllers.sharedPanels.Reports;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import parkflow.deskoptworker.Controllers.Components.FilterBarController;
import parkflow.deskoptworker.Controllers.Components.MetricCardController;
import parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController;

public class ReportCustomersController {

    // === TOP 4 METRIC CARDS (kolorowe z ikonkami) ===
    @FXML private MetricCardController totalCustomersCardController;
    @FXML private MetricCardController activeCardController;
    @FXML private MetricCardController newCardController;

    // === FILTER BAR ===
    @FXML private FilterBarController filterBarController;

    // === TABLE ===
    @FXML private TableView<CustomerData> topCustomersTable;
    @FXML private TableColumn<CustomerData, String> rankColumn;
    @FXML private TableColumn<CustomerData, String> customerColumn;
    @FXML private TableColumn<CustomerData, String> sessionsColumn;
    @FXML private TableColumn<CustomerData, String> totalSpentColumn;
    @FXML private TableColumn<CustomerData, String> avgDurationColumn;

    // === GROWTH METRICS (SimpleMetricBox) - 3 cards ===
    @FXML private SimpleMetricBoxController newCustomersCardController;
    @FXML private SimpleMetricBoxController activeCustomersCardController;
    @FXML private SimpleMetricBoxController avgSessionsCardController;

    @FXML
    public void initialize() {
        System.out.println("ReportCustomersController initialized");
        setupTopMetricCards();
        setupCustomersTable();
        setupGrowthMetrics();
    }

    /**
     * Setup top 4 kolorowych MetricCards
     */
    private void setupTopMetricCards() {
        // Total Customers - Purple
        totalCustomersCardController.setData(
                "Total Customers",
                "4521",
                null,
                "/parkflow/deskoptworker/images/customersWhite.png",
                "card-purple"
        );

        // Active This Month - Green
        activeCardController.setData(
                "Active This Month",
                "3102",
                null,
                "/parkflow/deskoptworker/images/beatWhite.png",
                "card-green"
        );

        // New This Month - Blue
        newCardController.setData(
                "New This Month",
                "234",
                null,
                "/parkflow/deskoptworker/images/clockWhite.png",
                "card-blue"
        );


    }

    /**
     * Setup tabeli z klientami
     */
    private void setupCustomersTable() {
        if (topCustomersTable == null) {
            System.err.println("ERROR: topCustomersTable is NULL!");
            return;
        }

        // RANK COLUMN - gradient badge
        rankColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().rank));
        rankColumn.setCellFactory(column -> new TableCell<CustomerData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label(item);
                    label.getStyleClass().add("rank-badge");
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // CUSTOMER COLUMN - zwykły tekst
        customerColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().customer));

        // SESSIONS COLUMN - niebieski badge
        sessionsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().sessions));
        sessionsColumn.setCellFactory(column -> new TableCell<CustomerData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label(item);
                    label.getStyleClass().add("sessions-badge");
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // TOTAL SPENT COLUMN - zielony tekst
        totalSpentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().totalSpent));
        totalSpentColumn.setCellFactory(column -> new TableCell<CustomerData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                }
            }
        });

        // AVG DURATION COLUMN - zwykły tekst
        avgDurationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().avgDuration));

        // Dane testowe
        loadSampleCustomerData();
    }

    /**
     * Ładuje przykładowe dane klientów
     */
    private void loadSampleCustomerData() {
        ObservableList<CustomerData> customers = FXCollections.observableArrayList(
                new CustomerData("1", "Jan Kowalski", "45", "1240.50 $", "2.1 h"),
                new CustomerData("2", "Anna Nowak", "38", "1155.80 $", "1.8 h"),
                new CustomerData("3", "Sebastian Kania", "42", "1089.20 $", "3.5 h"),
                new CustomerData("4", "Marek Wiśniewski", "36", "985.40 $", "2.8 h"),
                new CustomerData("5", "Katarzyna Zielińska", "33", "892.10 $", "2.4 h")
        );

        topCustomersTable.setItems(customers);
        topCustomersTable.setPlaceholder(new Label("No data available"));
    }

    /**
     * Setup Growth Metrics (SimpleMetricBox) - 3 cards
     */
    private void setupGrowthMetrics() {
        // New Customers - Pink
        newCustomersCardController.setData("New Customers", "234", "This Month");
        newCustomersCardController.setCardType("pink");

        // Active Customers - Purple
        activeCustomersCardController.setData("Active Customers", "3102", "66.6% of total");
        activeCustomersCardController.setCardType("purple");

        // Avg Sessions per Customer - Blue
        avgSessionsCardController.setData("Avg Sessions per Customer", "2.8", "Per customer");
        avgSessionsCardController.setCardType("blue");
    }

    // ==================== PUBLIC UPDATE METHODS ====================

    /**
     * Aktualizuje wszystkie dane z API
     */
    public void updateAllData(CustomerMetricsData data) {
        updateTopMetrics(data);
        updateGrowthMetrics(data);
        updateCustomersTable(data.topCustomers);
    }

    /**
     * Aktualizuje top 4 MetricCards
     */
    public void updateTopMetrics(CustomerMetricsData data) {
        totalCustomersCardController.setValue(String.valueOf(data.totalCustomers));
        activeCardController.setValue(String.valueOf(data.activeThisMonth));
        newCardController.setValue(String.valueOf(data.newThisMonth));
    }

    /**
     * Aktualizuje Growth Metrics
     */
    public void updateGrowthMetrics(CustomerMetricsData data) {
        newCustomersCardController.setValue(String.valueOf(data.newThisMonth));
        activeCustomersCardController.setValue(String.valueOf(data.activeThisMonth));
        activeCustomersCardController.setSubtitle(String.format("%.1f%% of total", data.activePercent));
        avgSessionsCardController.setValue(String.format("%.1f", data.avgSessionsPerCustomer));
    }

    /**
     * Aktualizuje tabelę klientów
     */
    public void updateCustomersTable(ObservableList<CustomerData> customers) {
        if (customers != null) {
            topCustomersTable.setItems(customers);
        }
    }

    /**
     * Refresh data based on filter selection
     */
    public void refreshData(String timePeriod, String parking) {
        System.out.println("Refreshing customer data for: " + timePeriod + " | " + parking);
        // TODO: Pobierz dane z API i wywołaj updateAllData()
    }

    // ==================== DATA CLASSES ====================

    /**
     * Klasa pomocnicza do przekazywania danych z API
     */
    public static class CustomerMetricsData {
        // Top metrics
        public int totalCustomers;
        public int activeThisMonth;
        public int newThisMonth;
        public double retentionRate;

        // Growth metrics
        public double avgSessionsPerCustomer;
        public double activePercent;

        // Table data
        public ObservableList<CustomerData> topCustomers;
    }

    /**
     * Dane pojedynczego klienta w tabeli
     */
    public static class CustomerData {
        public final String rank;
        public final String customer;
        public final String sessions;
        public final String totalSpent;
        public final String avgDuration;

        public CustomerData(String rank, String customer, String sessions, String totalSpent, String avgDuration) {
            this.rank = rank;
            this.customer = customer;
            this.sessions = sessions;
            this.totalSpent = totalSpent;
            this.avgDuration = avgDuration;
        }
    }
}