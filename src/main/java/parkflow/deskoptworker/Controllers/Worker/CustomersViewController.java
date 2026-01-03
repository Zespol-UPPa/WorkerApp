package parkflow.deskoptworker.Controllers.Worker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import parkflow.deskoptworker.models.Customer;

import java.util.Objects;

public class CustomersViewController {

    @FXML private TextField searchField;
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, String> customerColumn;
    @FXML private TableColumn<Customer, String> vehiclesColumn;
    @FXML private TableColumn<Customer, String> walletColumn;
    @FXML private TableColumn<Customer, String> totalSpentColumn;
    @FXML private TableColumn<Customer, Void> actionColumn;

    private ObservableList<Customer> customersList;
    private ObservableList<Customer> filteredList;

    @Setter
    private CustomersController parentController;

    @FXML
    public void initialize() {
        System.out.println("CustomersViewController initialized");

        customersList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();

        loadSampleData();
        setupTableColumns();
        customersTable.setItems(filteredList);
        setupSearchFilter();
    }

    private void setupTableColumns() {
        setupCustomerColumn();
        setupVehiclesColumn();
        setupWalletColumn();
        setupTotalSpentColumn();
        setupActionColumn();
    }

    private void setupCustomerColumn() {
        customerColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName())
        );

        customerColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Customer customer = getTableRow().getItem();

                    Label nameLabel = new Label(customer.getFullName());
                    nameLabel.getStyleClass().add("customer-name-label");

                    Label emailLabel = new Label(customer.getEmail());
                    emailLabel.getStyleClass().add("customer-email-label");

                    VBox vbox = new VBox(2);
                    vbox.getChildren().addAll(nameLabel, emailLabel);
                    vbox.setAlignment(Pos.CENTER_LEFT);

                    setGraphic(vbox);
                }
            }
        });
    }

    private void setupVehiclesColumn() {
        vehiclesColumn.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    customer.getVehiclesCount() > 0 ? customer.getVehicles().getFirst().getRegistrationNumber() : ""
            );
        });

        vehiclesColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Customer customer = getTableRow().getItem();
                    HBox container = new HBox(8);
                    container.setAlignment(Pos.CENTER_LEFT);

                    if (customer.getVehiclesCount() > 0) {
                        Label vehicleBadge = new Label(customer.getVehicles().getFirst().getRegistrationNumber());
                        vehicleBadge.getStyleClass().add("vehicle-badge");
                        container.getChildren().add(vehicleBadge);

                        if (customer.getVehiclesCount() > 1) {
                            Label moreBadge = new Label("+" + (customer.getVehiclesCount() - 1));
                            moreBadge.getStyleClass().add("vehicle-more-badge");
                            container.getChildren().add(moreBadge);
                        }
                    }

                    setGraphic(container);
                }
            }
        });
    }

    private void setupWalletColumn() {
        walletColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("%.2f $", cellData.getValue().getWalletBalance())
                )
        );

        walletColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Customer customer = getTableRow().getItem();
                    double balance = customer.getWalletBalance();

                    Label balanceLabel = new Label(String.format("%.2f $", balance));
                    balanceLabel.getStyleClass().add("wallet-balance-label");
                    balanceLabel.getStyleClass().add(balance < 10 ? "wallet-balance-low" : "wallet-balance-ok");

                    setGraphic(balanceLabel);
                }
            }
        });
    }

    private void setupTotalSpentColumn() {
        totalSpentColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.format("%.2f $", cellData.getValue().getTotalSpent())
                )
        );

        totalSpentColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label label = new Label(item);
                    label.getStyleClass().add("total-spent-label");
                    setGraphic(label);
                }
            }
        });
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button arrowButton = new Button();

            {
                arrowButton.getStyleClass().add("action-arrow-button");

                try {
                    ImageView arrowIcon = new ImageView(
                            new Image(Objects.requireNonNull(
                                    getClass().getResourceAsStream("/parkflow/deskoptworker/images/arrowRight.png")
                            ))
                    );
                    arrowIcon.setFitWidth(20);
                    arrowIcon.setFitHeight(20);
                    arrowIcon.setPreserveRatio(true);
                    arrowButton.setGraphic(arrowIcon);
                } catch (Exception e) {
                    System.err.println("Failed to load arrow icon");
                    arrowButton.setText("→");
                }

                arrowButton.setOnAction(_ -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    handleViewCustomerDetails(customer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : arrowButton);
            }
        });
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((_, _, newValue) -> filterCustomers(newValue));
    }

    private void filterCustomers(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredList.setAll(customersList);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();

            filteredList.setAll(
                    customersList.stream()
                            .filter(customer ->
                                    customer.getFullName().toLowerCase().contains(lowerCaseFilter) ||
                                            customer.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                                            String.valueOf(customer.getCustomerId()).contains(lowerCaseFilter) ||
                                            customer.getVehicles().stream()
                                                    .anyMatch(v -> v.getRegistrationNumber().toLowerCase().contains(lowerCaseFilter))
                            )
                            .toList()
            );
        }
    }

    private void handleViewCustomerDetails(Customer customer) {
        System.out.println("View details for customer: " + customer.getFullName());

        if (parentController != null) {
            parentController.showCustomerProfile(customer);
        } else {
            System.err.println("Parent controller is null!");
        }
    }

    private void loadSampleData() {
        // ... (bez zmian - ta sama zawartość co wcześniej)
    }
}