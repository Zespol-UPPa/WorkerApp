package parkflow.deskoptworker.Controllers.Worker;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import parkflow.deskoptworker.Controllers.Components.FilterBarPaymentsController;
import parkflow.deskoptworker.models.Customer;
import parkflow.deskoptworker.models.Transaction;
import parkflow.deskoptworker.models.Transaction.TransactionStatus;
import parkflow.deskoptworker.models.Transaction.TransactionType;
import parkflow.deskoptworker.Controllers.Components.MetricCardController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Kontroler widoku Payments/Transactions.
 * Wyświetla transakcje parkingowe (Parking Session i Reservation Fee)
 * związane z parkingami danej firmy.
 *
 * NIE wyświetla wpłat do portfela (deposits) - to jest relacja klient ↔ ParkFlow.
 */
public class PaymentsViewController {

    // ==================== FXML ELEMENTS ====================

    // Clear filter
    @FXML private HBox clearFilterBox;
    @FXML private Label clearFilterLabel;

    // Header
    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;

    // Metric cards (visible when filtered)
    @FXML private HBox metricsBox;

    @FXML private MetricCardController walletBalanceCardController;
    @FXML private MetricCardController totalSpentCardController;
    @FXML private MetricCardController sessionsCountCardController;
    @FXML private MetricCardController pendingCardController;

    // Search
    @FXML private TextField searchField;

    // Filter bar
    @FXML private FilterBarPaymentsController filterBarController;

    // Table
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> customerColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, String> parkingColumn;
    @FXML private TableColumn<Transaction, String> amountColumn;
    @FXML private TableColumn<Transaction, String> statusColumn;

    // ==================== DATA ====================

    private ObservableList<Transaction> allTransactions = FXCollections.observableArrayList();
    private ObservableList<Transaction> filteredTransactions = FXCollections.observableArrayList();

    @Setter
    private CustomersController parentController;

    @Getter
    private Customer filteredCustomer = null;

    // ==================== INITIALIZATION ====================

    @FXML
    public void initialize() {
        transactionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        System.out.println("PaymentsViewController initialized");

        setupFilterBarListener();
        setupTableColumns();
        setupSearchListener();

        loadMockData();
        applyFilters();
    }

    private void setupFilterBarListener() {
        if (filterBarController != null) {
            filterBarController.setFilterChangeListener((type, status) -> {
                System.out.println("Filters changed: " + type + " | " + status);
                applyFilters();
            });
            System.out.println("FilterBar listener connected!");
        } else {
            System.err.println("WARNING: filterBarController is null!");
        }
    }

    private void setupTableColumns() {
        // Date column
        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFormattedDate())
        );

        // Customer column - with name and license plate
        customerColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCustomerName())
        );
        customerColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Transaction tx = getTableRow().getItem();

                    VBox vbox = new VBox(2);
                    vbox.setAlignment(Pos.CENTER_LEFT);

                    Label nameLabel = new Label(tx.getCustomerName());
                    nameLabel.getStyleClass().add("transaction-customer-name");

                    Label plateLabel = new Label(tx.getLicensePlate());
                    plateLabel.getStyleClass().add("transaction-license-plate");

                    vbox.getChildren().addAll(nameLabel, plateLabel);
                    setGraphic(vbox);
                }
            }
        });

        // Type column - with colored badge
        typeColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTypeDisplayName())
        );
        typeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Transaction tx = getTableRow().getItem();
                    if (tx == null) {
                        setGraphic(null);
                        return;
                    }

                    HBox badge = new HBox(10);
                    badge.setAlignment(Pos.CENTER_LEFT);
                    ImageView iconView = new ImageView();
                    iconView.setFitWidth(24);
                    iconView.setFitHeight(24);
                    iconView.setPreserveRatio(true);

                    Label textLabel = new Label(item);

                    if (tx.getType() == TransactionType.PARKING_SESSION) {
                        badge.getStyleClass().add("type-badge-parking");
                        try {
                            iconView.setImage(new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream("/parkflow/deskoptworker/images/CarBlue.png"))
                            ));
                        } catch (Exception e) {
                            System.err.println("Failed to load parking icon");
                        }
                    } else {
                        badge.getStyleClass().add("type-badge-reservation");
                        try {
                            iconView.setImage(new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream("/parkflow/deskoptworker/images/calendar.png"))
                            ));
                        } catch (Exception e) {
                            System.err.println("Failed to load calendar icon");
                        }
                    }

                    badge.getChildren().addAll(iconView, textLabel);
                    setGraphic(badge);
                }
            }
        });

        parkingColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getParkingName())
        );

        // Amount column - styled
        amountColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFormattedAmount())
        );
        amountColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().remove("transaction-amount-label");
                } else {
                    setText(item);
                    if (!getStyleClass().contains("transaction-amount-label")) {
                        getStyleClass().add("transaction-amount-label");
                    }
                }
            }
        });

        // Status column - with colored badge
        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatusDisplayName())
        );
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("transaction-status-badge");

                    Transaction tx = getTableRow() != null ? getTableRow().getItem() : null;
                    if (tx != null) {
                        if (tx.getStatus() == TransactionStatus.COMPLETED) {
                            badge.getStyleClass().add("transaction-status-completed");
                        } else if (tx.getStatus() == TransactionStatus.PENDING) {
                            badge.getStyleClass().add("transaction-status-pending");
                        } else {
                            badge.getStyleClass().add("transaction-status-default");
                        }
                    }

                    setGraphic(badge);
                }
            }
        });

        transactionsTable.setItems(filteredTransactions);
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((_, _, _) -> applyFilters());
    }

    // ==================== FILTERING ====================

    /**
     * Ustawia filtr na klienta - pokazuje tylko jego transakcje
     */
    public void setCustomerFilter(Customer customer) {
        this.filteredCustomer = customer;

        showClearFilterBox(true);
        titleLabel.setText(customer.getFullName() + "'s Transactions");
        subtitleLabel.setText("Parking history at your locations");

        // Pokaż metryki
        metricsBox.setVisible(true);
        metricsBox.setManaged(true);

        // Reset filter bar
        if (filterBarController != null) {
            filterBarController.resetFilters();
        }

        // Oblicz metryki dla klienta
        updateCustomerMetrics(customer);

        applyFilters();
    }

    /**
     * Czyści filtr klienta
     */
    public void clearCustomerFilter() {
        this.filteredCustomer = null;

        showClearFilterBox(false);
        titleLabel.setText("Parking Transactions");
        subtitleLabel.setText("Revenue from parking sessions and reservations at your locations");

        // Ukryj metryki
        metricsBox.setVisible(false);
        metricsBox.setManaged(false);

        applyFilters();
    }

    private void showClearFilterBox(boolean show) {
        clearFilterBox.setVisible(show);
        clearFilterBox.setManaged(show);
    }

    private void applyFilters() {
        String searchText = searchField.getText();
        String typeFilter = filterBarController != null ? filterBarController.getSelectedType() : "All Types";
        String statusFilter = filterBarController != null ? filterBarController.getSelectedStatus() : "All Statuses";

        filteredTransactions.setAll(
                allTransactions.stream()
                        .filter(tx -> matchesCustomerFilter(tx))
                        .filter(tx -> matchesSearchText(tx, searchText))
                        .filter(tx -> matchesTypeFilter(tx, typeFilter))
                        .filter(tx -> matchesStatusFilter(tx, statusFilter))
                        .collect(Collectors.toList())
        );
    }

    private boolean matchesCustomerFilter(Transaction tx) {
        if (filteredCustomer == null) return true;
        return tx.getCustomerId() == filteredCustomer.getCustomerId();
    }

    private boolean matchesSearchText(Transaction tx, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) return true;

        String lowerSearch = searchText.toLowerCase().trim();

        return tx.getCustomerName().toLowerCase().contains(lowerSearch) ||
                tx.getParkingName().toLowerCase().contains(lowerSearch) ||
                tx.getLicensePlate().trim().toLowerCase().contains(lowerSearch)||
                tx.getDescription().toLowerCase().contains(lowerSearch);
    }

    private boolean matchesTypeFilter(Transaction tx, String typeFilter) {
        if (typeFilter == null || "All Types".equals(typeFilter)) return true;

        return switch (typeFilter) {
            case "Parking Session" -> tx.getType() == TransactionType.PARKING_SESSION;
            case "Reservation Fee" -> tx.getType() == TransactionType.RESERVATION_FEE;
            default -> true;
        };
    }

    private boolean matchesStatusFilter(Transaction tx, String statusFilter) {
        if (statusFilter == null || "All Statuses".equals(statusFilter)) return true;

        return switch (statusFilter) {
            case "Completed" -> tx.getStatus() == TransactionStatus.COMPLETED;
            case "Pending" -> tx.getStatus() == TransactionStatus.PENDING;
            default -> true;
        };
    }

    private void updateCustomerMetrics(Customer customer) {
        if (customer == null) return;

        // Filtruj transakcje dla tego klienta
        List<Transaction> customerTx = allTransactions.stream()
                .filter(tx -> tx.getCustomerId() == customer.getCustomerId())
                .collect(Collectors.toList());

        // === CARD 1: Wallet Balance ===
        if (walletBalanceCardController != null) {
            double balance = customer.getWalletBalance();
            walletBalanceCardController.setTitle("Wallet Balance");
            walletBalanceCardController.setValue(String.format("%.2f $", balance));
            walletBalanceCardController.setIcon("/parkflow/deskoptworker/images/wallet.png");

            // Kolor zależny od salda
            if (balance < 10) {
                walletBalanceCardController.setValueColor("value-red");
            } else {
                walletBalanceCardController.setValueColor("value-green");
            }
        }

        // === CARD 2: Total Spent (completed only) ===
        if (totalSpentCardController != null) {
            double totalSpent = customerTx.stream()
                    .filter(tx -> tx.getStatus() == TransactionStatus.COMPLETED)
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            totalSpentCardController.setTitle("Total Spent");
            totalSpentCardController.setValue(String.format("%.2f $", totalSpent));
            totalSpentCardController.setIcon("/parkflow/deskoptworker/images/dollar.png");
        }

        // === CARD 3: Sessions Count ===
        if (sessionsCountCardController != null) {
            long sessionsCount = customerTx.stream()
                    .filter(tx -> tx.getType() == TransactionType.PARKING_SESSION)
                    .count();

            sessionsCountCardController.setTitle("Parking Sessions");
            sessionsCountCardController.setValue(String.valueOf(sessionsCount));
            sessionsCountCardController.setSubtitle("completed sessions");
            sessionsCountCardController.setIcon("/parkflow/deskoptworker/images/parking.png");
        }

        // === CARD 4: Pending Payments ===
        if (pendingCardController != null) {
            List<Transaction> pendingTx = customerTx.stream()
                    .filter(tx -> tx.getStatus() == TransactionStatus.PENDING)
                    .collect(Collectors.toList());

            double pendingAmount = pendingTx.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            pendingCardController.setTitle("Pending Payments");
            pendingCardController.setValue(String.format("%.2f $", pendingAmount));
            pendingCardController.setSubtitle(pendingTx.size() + " unpaid session" + (pendingTx.size() != 1 ? "s" : ""));
            pendingCardController.setIcon("/parkflow/deskoptworker/images/warning.png");

            if (pendingTx.size() > 0) {
                pendingCardController.setValueColor("value-orange");
            }
        }
    }

    // ==================== EVENT HANDLERS ====================

    @FXML
    private void onClearFilter() {
        clearCustomerFilter();

        if (parentController != null) {
            parentController.showCustomersList();
        }
    }

    // ==================== DATA LOADING ====================

    /**
     * Ustawia transakcje z zewnętrznego źródła
     */
    public void setTransactions(List<Transaction> transactions) {
        this.allTransactions.setAll(transactions != null ? transactions : new ArrayList<>());
        applyFilters();
    }

    /**
     * Odświeża dane - TODO: implementacja pobierania z API
     */
    public void refreshTransactions() {
        // TODO: Load from API/database
        loadMockData();
        applyFilters();
    }

    private void loadMockData() {
        allTransactions.clear();

        // Parking Sessions
        allTransactions.add(new Transaction(
                1,
                LocalDateTime.of(2025, 11, 18, 14, 30),
                TransactionType.PARKING_SESSION,
                TransactionStatus.COMPLETED,
                12.00,
                "Parking session (2h 15min)",
                1, "Jan Kowalski", "KR 12345",
                98, "Galeria Krakowska"
        ));

        allTransactions.add(new Transaction(
                2,
                LocalDateTime.of(2025, 11, 18, 10, 15),
                TransactionType.PARKING_SESSION,
                TransactionStatus.PENDING,
                8.50,
                "Parking session (1h 30min)",
                2, "Anna Nowak", "WA 98765",
                98, "Galeria Krakowska"
        ));

        allTransactions.add(new Transaction(
                3,
                LocalDateTime.of(2025, 11, 17, 16, 45),
                TransactionType.RESERVATION_FEE,
                TransactionStatus.COMPLETED,
                5.00,
                "Spot reservation R-23",
                1, "Jan Kowalski", "KR 12345",
                99, "Downtown Plaza"
        ));

        allTransactions.add(new Transaction(
                4,
                LocalDateTime.of(2025, 11, 17, 12, 00),
                TransactionType.PARKING_SESSION,
                TransactionStatus.COMPLETED,
                18.00,
                "Parking session (3h 45min)",
                3, "Piotr Wiśniewski", "GD 55555",
                98, "Galeria Krakowska"
        ));

        allTransactions.add(new Transaction(
                5,
                LocalDateTime.of(2025, 11, 16, 9, 30),
                TransactionType.RESERVATION_FEE,
                TransactionStatus.COMPLETED,
                5.00,
                "Spot reservation A-12",
                2, "Anna Nowak", "WA 98765",
                98, "Galeria Krakowska"
        ));

        allTransactions.add(new Transaction(
                6,
                LocalDateTime.of(2025, 11, 16, 15, 20),
                TransactionType.PARKING_SESSION,
                TransactionStatus.COMPLETED,
                6.50,
                "Parking session (1h 5min)",
                1, "Jan Kowalski", "KR 12345",
                99, "Downtown Plaza"
        ));

        allTransactions.add(new Transaction(
                7,
                LocalDateTime.of(2025, 11, 15, 11, 00),
                TransactionType.PARKING_SESSION,
                TransactionStatus.PENDING,
                22.00,
                "Parking session (4h 30min)",
                4, "Maria Kowalczyk", "PO 11111",
                100, "CH Bonarka"
        ));

        System.out.println("Loaded " + allTransactions.size() + " mock transactions");
    }
}