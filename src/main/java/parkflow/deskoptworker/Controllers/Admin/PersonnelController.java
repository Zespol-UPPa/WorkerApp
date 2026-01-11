package parkflow.deskoptworker.Controllers.Admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Setter;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.api.PersonnelService;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;

import java.util.List;
import java.util.Objects;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public class PersonnelController {

    @FXML
    public Button addEmpBtn;
    @FXML
    public TextField searchField;
    @FXML
    public TableView<User> employeeTable;
    @FXML
    public TableColumn<User, Integer> idColumn;
    @FXML
    public TableColumn<User, String> nameColumn;
    @FXML
    public TableColumn<User, String> emailColumn;
    @FXML
    public TableColumn<User, String> phoneColumn;
    @FXML
    public TableColumn<User, UserRole> roleColumn;
    @FXML
    public TableColumn<User, Boolean> statusColumn;
    @FXML
    public TableColumn<User, Void> actionsColumn;

    private ObservableList<User> employeeList = FXCollections.observableArrayList();
    private ObservableList<User> filteredList = FXCollections.observableArrayList();

    @Setter
    private ViewFactory viewFactory = new ViewFactory();

    private final PersonnelService personnelService = new PersonnelService();

    @FXML
    public void initialize() {
        employeeTable.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        setupTableColumns();
        loadEmployees();
        setupSearch();
    }

    private void setupTableColumns() {
        // ID Column
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Name Column - combine firstName + lastName
        nameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName())
        );

        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // ===== ROLE COLUMN - with colored label =====
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleColumn.setCellFactory(column -> new TableCell<User, UserRole>() {
            @Override
            protected void updateItem(UserRole role, boolean empty) {
                super.updateItem(role, empty);

                if (empty || role == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label(role == UserRole.ADMIN ? "Admin" : "Worker");
                    label.getStyleClass().add("coloredLabels");
                    if (role == UserRole.ADMIN) {
                        label.getStyleClass().add("blue");
                    }
                    setGraphic(label);
                }
            }
        });

        // ===== STATUS COLUMN - with colored label =====
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        statusColumn.setCellFactory(column -> new TableCell<User, Boolean>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);

                if (empty || active == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label(active ? "Active" : "Inactive");
                    label.getStyleClass().add("coloredLabels");
                    label.getStyleClass().add(active ? "green" : "gray");
                    setGraphic(label);
                }
            }
        });

        // ===== ACTIONS COLUMN - with buttons =====
        actionsColumn.setCellFactory(column -> new TableCell<User, Void>() {
            private final Button viewButton;
            private final Button toggleStatusButton;
            private final javafx.scene.layout.HBox buttonsContainer;

            {
                viewButton = new Button();
                toggleStatusButton = new Button();

                viewButton.setStyle("-fx-background-color: transparent;");
                toggleStatusButton.setStyle("-fx-background-color: transparent;");

                // Eye icon - always the same
                try {
                    ImageView eyeIcon = new ImageView(new Image(
                            Objects.requireNonNull(getClass().getResourceAsStream("/parkflow/deskoptworker/images/eye.png"))
                    ));
                    eyeIcon.setFitWidth(20);
                    eyeIcon.setFitHeight(16);
                    viewButton.setGraphic(eyeIcon);
                } catch (Exception e) {
                    viewButton.setText(" Eye ");
                }

                viewButton.getStyleClass().add("action-button");
                toggleStatusButton.getStyleClass().add("action-button");

                buttonsContainer = new javafx.scene.layout.HBox(5, viewButton, toggleStatusButton);
                buttonsContainer.setAlignment(javafx.geometry.Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    viewButton.setOnAction(null);
                    toggleStatusButton.setOnAction(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());

                    // Show/hide toggle button based on role
                    // Only workers can be deactivated, not admins
                    if (user.getRole() == UserRole.ADMIN) {
                        // Hide toggle button for admins
                        toggleStatusButton.setVisible(false);
                        toggleStatusButton.setManaged(false);
                    } else {
                        // Show toggle button for workers
                        toggleStatusButton.setVisible(true);
                        toggleStatusButton.setManaged(true);

                        // Update icon based on user status
                        try {
                            ImageView statusIcon;
                            if (user.isActive()) {
                                // Active user -> X (deactivate)
                                statusIcon = new ImageView(new Image(
                                        Objects.requireNonNull(getClass().getResourceAsStream("/parkflow/deskoptworker/images/x.png"))
                                ));
                            } else {
                                // Inactive user -> check (activate)
                                statusIcon = new ImageView(new Image(
                                        Objects.requireNonNull(getClass().getResourceAsStream("/parkflow/deskoptworker/images/check.png"))
                                ));
                            }
                            statusIcon.setFitWidth(20);
                            statusIcon.setFitHeight(20);
                            toggleStatusButton.setGraphic(statusIcon);
                        } catch (Exception e) {
                            toggleStatusButton.setText(user.isActive() ? "X" : "V");
                        }

                        // Set handler for toggle button
                        toggleStatusButton.setOnAction(_ -> handleToggleStatus(user));
                    }

                    // Set handler for view button
                    viewButton.setOnAction(_ -> handleViewEmployee(user));

                    setGraphic(buttonsContainer);
                }
            }
        });

        // Connect list to table
        employeeTable.setItems(filteredList);
    }

    /**
     * Load employees from backend
     * Loads all personnel (admins + workers) from company
     */
    private void loadEmployees() {
        // Show loading indicator
        employeeTable.setPlaceholder(new Label("Loading personnel..."));

        // Load in background thread to avoid blocking UI
        new Thread(() -> {
            try {
                List<User> personnel = personnelService.getCompanyPersonnel();

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    employeeList.clear();
                    employeeList.addAll(personnel);
                    filteredList.setAll(employeeList);

                    if (personnel.isEmpty()) {
                        employeeTable.setPlaceholder(new Label("No personnel found"));
                    }

                    System.out.println("Loaded " + personnel.size() + " personnel members");
                });

            } catch (Exception e) {
                System.err.println("Error loading personnel: " + e.getMessage());
                e.printStackTrace();

                Platform.runLater(() -> {
                    employeeTable.setPlaceholder(new Label("Error loading personnel"));
                });
            }
        }).start();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((_, _, newValue) -> filterEmployees(newValue));
    }

    private void filterEmployees(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredList.setAll(employeeList);
            return;
        }

        String lowerCaseFilter = searchText.toLowerCase();

        filteredList.setAll(
                employeeList.stream()
                        .filter(user ->
                                user.getFullName().toLowerCase().contains(lowerCaseFilter) ||
                                        user.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                                        user.getPesel().contains(lowerCaseFilter) ||
                                        String.valueOf(user.getId()).contains(lowerCaseFilter) ||
                                        user.getRole().toString().toLowerCase().contains(lowerCaseFilter)
                        )
                        .toList()
        );
    }

    @FXML
    private void handleAddEmployee() {
        // TODO: Implement add employee modal
        // This would require creating a new account + admin/worker record
        System.out.println("Add employee - to be implemented");
    }

    private void handleViewEmployee(User user) {
        if (viewFactory != null) {
            viewFactory.showEmployeeDetailsModal(user);
        }
    }

    /**
     * Handle toggle status (activate/deactivate)
     * Only workers can be toggled, not admins
     */
    private void handleToggleStatus(User user) {
        if (viewFactory == null || user.getRole() == UserRole.ADMIN) {
            return; // Cannot toggle admin status
        }

        if (user.isActive()) {
            // Deactivate worker
            boolean confirmed = viewFactory.showDeactivateEmployeeModal(user);
            if (confirmed) {
                // Call backend to deactivate
                new Thread(() -> {
                    boolean success = personnelService.deactivateWorker(user.getId());

                    Platform.runLater(() -> {
                        if (success) {
                            user.setActive(false);
                            employeeTable.refresh();
                            System.out.println("Deactivated: " + user.getFullName());
                        } else {
                            // Show error alert
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Failed to deactivate worker");
                            alert.setContentText("An error occurred while trying to deactivate the worker.");
                            alert.showAndWait();
                        }
                    });
                }).start();
            }
        } else {
            // Activate worker
            boolean confirmed = viewFactory.showActivateEmployeeModal(user);
            if (confirmed) {
                // Call backend to activate
                new Thread(() -> {
                    boolean success = personnelService.activateWorker(user.getId());

                    Platform.runLater(() -> {
                        if (success) {
                            user.setActive(true);
                            employeeTable.refresh();
                            System.out.println("Activated: " + user.getFullName());
                        } else {
                            // Show error alert
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Failed to activate worker");
                            alert.setContentText("An error occurred while trying to activate the worker.");
                            alert.showAndWait();
                        }
                    });
                }).start();
            }
        }
    }

    /**
     * Refresh personnel list (can be called from outside)
     */
    public void refresh() {
        loadEmployees();
    }
}