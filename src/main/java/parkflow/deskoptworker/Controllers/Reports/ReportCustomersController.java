package parkflow.deskoptworker.Controllers.Reports;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import parkflow.deskoptworker.Controllers.Components.FilterBarController;
import parkflow.deskoptworker.Controllers.Components.MetricCardController;

import java.io.IOException;

public class ReportCustomersController {

    @FXML private GridPane metricsGrid;
    @FXML private TableView<CustomerData> topCustomersTable;
    @FXML private TableColumn<CustomerData, String> rankColumn;
    @FXML private TableColumn<CustomerData, String> customerColumn;
    @FXML private TableColumn<CustomerData, String> sessionsColumn;
    @FXML private TableColumn<CustomerData, String> totalSpentColumn;
    @FXML private TableColumn<CustomerData, String> avgDurationColumn;
    @FXML private VBox behaviorMetricsContainer;
    @FXML private VBox growthMetricsContainer;
    @FXML private FilterBarController filterBarController;

    @FXML
    public void initialize() {
        System.out.println("======================================");
        System.out.println("ReportCustomersController.initialize() CALLED");
        System.out.println("======================================");

        try {
            setupTopMetrics();
            System.out.println("✓ Top metrics setup complete");
        } catch (Exception e) {
            System.err.println("✗ Error in setupTopMetrics: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            setupCustomersTable();
            System.out.println("✓ Customers table setup complete");
        } catch (Exception e) {
            System.err.println("✗ Error in setupCustomersTable: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            setupBehaviorMetrics();
            System.out.println("✓ Behavior metrics setup complete");
        } catch (Exception e) {
            System.err.println("✗ Error in setupBehaviorMetrics: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            setupGrowthMetrics();
            System.out.println("✓ Growth metrics setup complete");
        } catch (Exception e) {
            System.err.println("✗ Error in setupGrowthMetrics: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("======================================");
        System.out.println("INITIALIZATION COMPLETE");
        System.out.println("======================================");
    }

    private void setupTopMetrics() {
        if (metricsGrid == null) {
            System.err.println("metricsGrid is NULL, skipping top metrics");
            return;
        }

        MetricCardController totalCard = loadMetricCard(
                "Total Customers", "4521", "",
                "/parkflow/deskoptworker/images/customersWhite.png", "card-purple"
        );
        if (totalCard != null) {
            metricsGrid.add(totalCard.getRoot(), 0, 0);
        }

        MetricCardController activeCard = loadMetricCard(
                "Active This Month", "3102", "",
                "/parkflow/deskoptworker/images/beatWhite.png", "card-green"
        );
        if (activeCard != null) {
            metricsGrid.add(activeCard.getRoot(), 1, 0);
        }

        MetricCardController newCard = loadMetricCard(
                "New This Month", "234", "",
                "/parkflow/deskoptworker/images/clockWhite.png", "card-blue"
        );
        if (newCard != null) {
            metricsGrid.add(newCard.getRoot(), 2, 0);
        }

        MetricCardController retentionCard = loadMetricCard(
                "Retention Rate", "78.5 %", "",
                "/parkflow/deskoptworker/images/chartWhite.png", "card-orange"
        );
        if (retentionCard != null) {
            metricsGrid.add(retentionCard.getRoot(), 3, 0);
        }
    }

    private void setupCustomersTable() {
        System.out.println("--- setupCustomersTable START ---");

        if (topCustomersTable == null) {
            System.err.println("ERROR: topCustomersTable is NULL!");
            return;
        }

        // Custom cell factory for rank column with colored circles
        rankColumn.setCellFactory(column -> new TableCell<CustomerData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Create circle with rank number
                    Circle circle = new Circle(18);

                    // Set color based on rank
                    String[] colors = {
                            "#D5297E",  // Rank 1 - Pink/Magenta
                            "#2A26BF",  // Rank 2 - Blue
                            "#A34DE9",  // Rank 3 - Purple
                            "#3E81F6",  // Rank 4 - Light Blue
                            "#039668"   // Rank 5 - Green
                    };

                    int rankNum = Integer.parseInt(item) - 1;
                    circle.setFill(javafx.scene.paint.Color.web(
                            rankNum < colors.length ? colors[rankNum] : "#6b7280"
                    ));

                    // Create label with rank number
                    Label rankLabel = new Label(item);
                    rankLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: white;");

                    // Stack them
                    StackPane stack = new StackPane(circle, rankLabel);
                    stack.setAlignment(Pos.CENTER);

                    setGraphic(stack);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Other columns - simple text
        customerColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().customer));
        sessionsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().sessions));
        totalSpentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().totalSpent));
        avgDurationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().avgDuration));

        // Create data
        ObservableList<CustomerData> customers = FXCollections.observableArrayList(
                new CustomerData("1", "Jan Kowalski", "45", "1240.50 $", "2.1 h"),
                new CustomerData("2", "Anna Nowak", "38", "1155.80 $", "1.8 h"),
                new CustomerData("3", "Sebastian Kania", "42", "1089.20 $", "3.5 h"),
                new CustomerData("4", "Marek Wiśniewski", "36", "985.40 $", "2.8 h"),
                new CustomerData("5", "Katarzyna Zielińska", "33", "892.10 $", "2.4 h")
        );

        topCustomersTable.setItems(customers);
        topCustomersTable.setPlaceholder(new Label("No data available"));

        System.out.println("--- setupCustomersTable END ---");
    }

    private void setupBehaviorMetrics() {
        if (behaviorMetricsContainer == null) {
            System.err.println("behaviorMetricsContainer is NULL, skipping");
            return;
        }

        System.out.println("Loading behavior metrics...");

        // Avg Sessions per Customer - PURPLE
        try {
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/SimpleMetricBox.fxml"));
            VBox box1 = loader1.load();
            parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController controller1 = loader1.getController();
            controller1.setData("Avg Sessions per Customer", "2.8");
            controller1.setCardType("purple");  // ✓ POPRAWIONE - używamy setCardType
            behaviorMetricsContainer.getChildren().add(box1);
            System.out.println("  Added Avg Sessions metric");
        } catch (IOException e) {
            System.err.println("  Error loading Avg Sessions: " + e.getMessage());
        }

        // Retention Rate - GREEN
        try {
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/SimpleMetricBox.fxml"));
            VBox box2 = loader2.load();
            parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController controller2 = loader2.getController();
            controller2.setData("Retention Rate", "78.5%");
            controller2.setCardType("green");  // ✓ POPRAWIONE - używamy setCardType
            behaviorMetricsContainer.getChildren().add(box2);
            System.out.println("  Added Retention Rate metric");
        } catch (IOException e) {
            System.err.println("  Error loading Retention Rate: " + e.getMessage());
        }

        // Churn Rate - RED
        try {
            FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/SimpleMetricBox.fxml"));
            VBox box3 = loader3.load();
            parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController controller3 = loader3.getController();
            controller3.setData("Churn Rate", "5.2%");
            controller3.setCardType("red");  // ✓ POPRAWIONE - używamy setCardType zamiast setCustomColors
            behaviorMetricsContainer.getChildren().add(box3);
            System.out.println("  Added Churn Rate metric");
        } catch (IOException e) {
            System.err.println("  Error loading Churn Rate: " + e.getMessage());
        }
    }

    private void setupGrowthMetrics() {
        if (growthMetricsContainer == null) {
            System.err.println("growthMetricsContainer is NULL, skipping");
            return;
        }

        System.out.println("Loading growth metrics...");

        // New Customers - PINK
        try {
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/SimpleMetricBox.fxml"));
            VBox box1 = loader1.load();
            parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController controller1 = loader1.getController();
            controller1.setData("New Customers", "234", "This Month");
            controller1.setCardType("pink");  // ✓ POPRAWIONE - używamy setCardType
            growthMetricsContainer.getChildren().add(box1);
            System.out.println("  Added New Customers metric");
        } catch (IOException e) {
            System.err.println("  Error loading New Customers: " + e.getMessage());
        }

        // Active Customers - PURPLE
        try {
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/SimpleMetricBox.fxml"));
            VBox box2 = loader2.load();
            parkflow.deskoptworker.Controllers.Components.SimpleMetricBoxController controller2 = loader2.getController();
            controller2.setData("Active Customers", "3102", "66.6% of total");
            controller2.setCardType("purple");
            growthMetricsContainer.getChildren().add(box2);

            System.out.println("  Added Active Customers metric");
        } catch (IOException e) {
            System.err.println("  Error loading Active Customers: " + e.getMessage());
        }
    }

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
            return null;
        }
    }

    public static class CustomerData {
        private final String rank;
        private final String customer;
        private final String sessions;
        private final String totalSpent;
        private final String avgDuration;

        public CustomerData(String rank, String customer, String sessions, String totalSpent, String avgDuration) {
            this.rank = rank;
            this.customer = customer;
            this.sessions = sessions;
            this.totalSpent = totalSpent;
            this.avgDuration = avgDuration;
        }
    }
}