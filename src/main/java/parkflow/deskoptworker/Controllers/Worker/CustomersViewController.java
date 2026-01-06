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
import parkflow.deskoptworker.models.Vehicle;

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
        Customer customer1 = new Customer(1, "Jan", "Kowalski", "jan.kowalski@gmail.com", 125.50, 1250.00);
        customer1.addVehicle(new Vehicle(1, "KR 12345", 1));

        Customer customer2 = new Customer(2, "Anna", "Nowak", "anna.nowak@gmail.com", 125.50, 890.50);
        customer2.addVehicle(new Vehicle(2, "WA 54321", 2));
        customer2.addVehicle(new Vehicle(3, "WA 98765", 2));

        Customer customer3 = new Customer(3, "Piotr", "Wiśniewski", "piotr.wisniewski@gmail.com", 5.20, 2340.00);
        customer3.addVehicle(new Vehicle(4, "GD 11111", 3));

        Customer customer4 = new Customer(4, "Maria", "Kamińska", "maria.kaminska@gmail.com", 250.00, 3500.50);
        customer4.addVehicle(new Vehicle(5, "WA 11111", 4));
        customer4.addVehicle(new Vehicle(6, "WA 22222", 4));
        customer4.addVehicle(new Vehicle(7, "KR 33333", 4));

        Customer customer5 = new Customer(5, "Tomasz", "Lewandowski", "tomasz.lewandowski@gmail.com", 0.00, 156.00);
        customer5.addVehicle(new Vehicle(8, "GD 55555", 5));

        Customer customer6 = new Customer(6, "Katarzyna", "Zielińska", "k.zielinska@gmail.com", 89.30, 2100.00);
        customer6.addVehicle(new Vehicle(9, "KR 77777", 6));
        customer6.addVehicle(new Vehicle(10, "KR 88888", 6));

        Customer customer7 = new Customer(7, "Andrzej", "Szymański", "andrzej.szymanski@gmail.com", 15.75, 450.25);
        customer7.addVehicle(new Vehicle(11, "WA 99999", 7));

        Customer customer8 = new Customer(8, "Magdalena", "Woźniak", "m.wozniak@gmail.com", 320.00, 4200.00);
        customer8.addVehicle(new Vehicle(12, "GD 12121", 8));

        Customer customer9 = new Customer(9, "Krzysztof", "Dąbrowski", "krzysztof.dabrowski@gmail.com", 8.50, 890.00);
        customer9.addVehicle(new Vehicle(13, "KR 45678", 9));
        customer9.addVehicle(new Vehicle(14, "WA 13579", 9));
        customer9.addVehicle(new Vehicle(15, "GD 24680", 9));

        Customer customer10 = new Customer(10, "Agnieszka", "Kozłowska", "agnieszka.kozlowska@gmail.com", 175.20, 1850.50);
        customer10.addVehicle(new Vehicle(16, "WA 11122", 10));

        Customer customer11 = new Customer(11, "Marek", "Jankowski", "marek.jankowski@gmail.com", 2.30, 125.00);
        customer11.addVehicle(new Vehicle(17, "KR 99911", 11));

        Customer customer12 = new Customer(12, "Joanna", "Mazur", "joanna.mazur@gmail.com", 450.00, 5600.00);
        customer12.addVehicle(new Vehicle(18, "GD 77788", 12));
        customer12.addVehicle(new Vehicle(19, "GD 66655", 12));

        Customer customer13 = new Customer(13, "Paweł", "Krawczyk", "pawel.krawczyk@gmail.com", 95.80, 780.00);
        customer13.addVehicle(new Vehicle(20, "WA 33344", 13));

        Customer customer14 = new Customer(14, "Ewa", "Piotrowska", "ewa.piotrowska@gmail.com", 0.50, 2340.00);
        customer14.addVehicle(new Vehicle(21, "KR 55566", 14));
        customer14.addVehicle(new Vehicle(22, "WA 77788", 14));

        Customer customer15 = new Customer(15, "Rafał", "Grabowski", "rafal.grabowski@gmail.com", 210.00, 3100.00);
        customer15.addVehicle(new Vehicle(23, "GD 99900", 15));

        Customer customer16 = new Customer(16, "Monika", "Pawlak", "monika.pawlak@gmail.com", 67.40, 1200.00);
        customer16.addVehicle(new Vehicle(24, "KR 11223", 16));

        Customer customer17 = new Customer(17, "Marcin", "Michalski", "marcin.michalski@gmail.com", 5.00, 450.00);
        customer17.addVehicle(new Vehicle(25, "WA 44556", 17));
        customer17.addVehicle(new Vehicle(26, "GD 88990", 17));
        customer17.addVehicle(new Vehicle(27, "KR 22334", 17));
        customer17.addVehicle(new Vehicle(28, "WA 55667", 17));

        Customer customer18 = new Customer(18, "Barbara", "Król", "barbara.krol@gmail.com", 340.00, 4800.00);
        customer18.addVehicle(new Vehicle(29, "GD 33445", 18));

        Customer customer19 = new Customer(19, "Grzegorz", "Wróbel", "grzegorz.wrobel@gmail.com", 12.90, 670.00);
        customer19.addVehicle(new Vehicle(30, "KR 66778", 19));

        Customer customer20 = new Customer(20, "Aleksandra", "Adamczyk", "aleksandra.adamczyk@gmail.com", 188.60, 2950.00);
        customer20.addVehicle(new Vehicle(31, "WA 99001", 20));
        customer20.addVehicle(new Vehicle(32, "KR 11223", 20));

        customersList.addAll(
                customer1, customer2, customer3, customer4, customer5,
                customer6, customer7, customer8, customer9, customer10,
                customer11, customer12, customer13, customer14, customer15,
                customer16, customer17, customer18, customer19, customer20
        );
        filteredList.setAll(customersList);
    }
}