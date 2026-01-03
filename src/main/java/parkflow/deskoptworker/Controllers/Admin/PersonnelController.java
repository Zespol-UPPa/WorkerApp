package parkflow.deskoptworker.Controllers.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Setter;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;

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

    @FXML
    public void initialize() {
        employeeTable.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        setupTableColumns();
        loadEmployees();
        setupSearch();
    }

    private void setupTableColumns() {
        // PropertyValueFactory - automatycznie pobiera wartość z gettera
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Dla pełnego imienia łączymy firstName + lastName
        nameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName())
        );

        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // ===== KOLUMNA ROLE - z kolorowym labelem =====
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

        // ===== KOLUMNA STATUS - z kolorowym labelem =====
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

        // ===== KOLUMNA ACTIONS - z przyciskami =====
        actionsColumn.setCellFactory(column -> new TableCell<User, Void>() {
            private final Button viewButton;
            private final Button toggleStatusButton;
            private final javafx.scene.layout.HBox buttonsContainer;

            {
                viewButton = new Button();
                toggleStatusButton = new Button();

                viewButton.setStyle("-fx-background-color: transparent;");
                toggleStatusButton.setStyle("-fx-background-color: transparent;");

                // Ikona oka - zawsze taka sama
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

                    // Zaktualizuj ikonę w zależności od statusu użytkownika
                    try {
                        ImageView statusIcon;
                        if (user.isActive()) {
                            // Użytkownik aktywny -> X (dezaktywuj)
                            statusIcon = new ImageView(new Image(
                                    Objects.requireNonNull(getClass().getResourceAsStream("/parkflow/deskoptworker/images/x.png"))
                            ));
                        } else {
                            // Użytkownik nieaktywny -> check (aktywuj)
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

                    // Ustaw handlery
                    viewButton.setOnAction(_ -> handleViewEmployee(user));
                    toggleStatusButton.setOnAction(_ -> handleToggleStatus(user));

                    setGraphic(buttonsContainer);
                }
            }
        });

        // Podłącz listę do tabeli
        employeeTable.setItems(filteredList);
    }

    private void loadEmployees() {
        // Na razie przykładowe dane:
        employeeList.add(new User(10001, "Anna", "Nowak", "+48 502 897 314",
                "anna.nowak@parkflow.com", "12345678901", UserRole.ADMIN, true));
        employeeList.add(new User(10002, "Piotr", "Kowalczyk", "+48 666 769 980",
                "piotr.kowalczyk@parkflow.com", "98765432109", UserRole.WORKER, true));

        // Skopiuj wszystko do filteredList
        filteredList.setAll(employeeList);
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
                                        String.valueOf(user.getId()).contains(lowerCaseFilter)
                        )
                        .toList()
        );
    }


    @FXML
    private void handleAddEmployee() {
        if (viewFactory != null) {
            User newEmployee = viewFactory.showAddEmployeeModal();

            if (newEmployee != null) {
                // Dodaj nowego pracownika do listy
                employeeList.add(newEmployee);
                filteredList.setAll(employeeList);

                System.out.println("Dodano pracownika do tabeli: " + newEmployee.getFullName());
            } else {
                System.out.println("Anulowano dodawanie pracownika");
            }
        }
    }

    private void handleViewEmployee(User user) {
        if (viewFactory != null) {
            viewFactory.showEmployeeDetailsModal(user);
        }
    }

    private void handleToggleStatus(User user) {
        if (viewFactory == null) return;

        if (user.isActive()) {
            boolean confirmed = viewFactory.showDeactivateEmployeeModal(user);
            if (confirmed) {
                user.setActive(false);
                employeeTable.refresh();
                System.out.println("Dezaktywowano: " + user.getFullName());
            }
        } else {
//            boolean confirmed = viewFactory.showActivateEmployeeModal(user);
//            if (confirmed) {
//                user.setActive(true);
//                employeeTable.refresh();
//                System.out.println("Aktywowano: " + user.getFullName());
//            }
        }
    }
}