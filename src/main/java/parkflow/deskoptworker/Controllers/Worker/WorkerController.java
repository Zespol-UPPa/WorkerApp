package parkflow.deskoptworker.Controllers.Worker;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.api.ApiClient;
import parkflow.deskoptworker.api.AuthService;
import parkflow.deskoptworker.models.Customer;
import parkflow.deskoptworker.models.Parking;
import parkflow.deskoptworker.utils.NavigationManager;

public class WorkerController {

    @FXML private BorderPane contentArea;
    @FXML private WorkerMenuController workerMenuController;

    private final ViewFactory viewFactory;
    private final StringProperty selectedMenuItem;

    private final AuthService authService;

    // Konstruktor z ViewFactory (Dependency Injection)
    public WorkerController(ViewFactory viewFactory) {
        this.authService = new AuthService();
        this.viewFactory = viewFactory;
        this.selectedMenuItem = new SimpleStringProperty("");
    }

    @FXML
    public void initialize() {
        System.out.println("WorkerController initialized");

        NavigationManager.getInstance().registerWorkerController(this);

        // Listener na zmianę wybranego menu
        selectedMenuItem.addListener((_, _, newValue) -> {
            switch (newValue) {
                case "Dashboard":
                    contentArea.setCenter(viewFactory.getDashboardView());
                    break;
                case "Reports":
                    contentArea.setCenter(viewFactory.getReportsView());
                    break;
                case "Customers":
                    contentArea.setCenter(viewFactory.getCustomersView());
                    break;
                case "Settings":
                    contentArea.setCenter(viewFactory.getSettingsView());
                    break;
                default:
                    System.err.println("Unknown menu item: " + newValue);
            }
        });

        // Połącz z menu controllerem
        if (workerMenuController != null) {
            workerMenuController.setParentController(this);
            System.out.println("Parent controller SET successfully!");
        } else {
            System.err.println("ERROR: workerMenuController is NULL!");
        }

        // Załaduj domyślny widok
        selectedMenuItem.set("Dashboard");
        ApiClient.setSessionExpiredCallback(this::handleSessionExpired);
    }

    public void onMenuItemSelected(String menuItem) {
        selectedMenuItem.set(menuItem);
    }

    @FXML
    public void handleLogout() {
        // Manual logout - call backend to clear cookie
        authService.logout();

        // Close current window
        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.close();

        // Clear cache
        viewFactory.clearCache();

        // Show login
        viewFactory.showLoginWindow();
    }



    private void handleSessionExpired() {
        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.close();

        viewFactory.clearCache();
        viewFactory.showLoginWindow();
    }

    // =========== DODATKOWE NAWIGACJE ===========

    public void navigateToCustomersReservations(Parking parking) {
        System.out.println("WorkerController: Navigating to Customers/Reservations for parking: " + parking.getName());

        if (workerMenuController != null) {
            workerMenuController.setActiveMenuItem("Customers");
        }

        selectedMenuItem.set("Customers");

        javafx.application.Platform.runLater(() -> {
            javafx.application.Platform.runLater(() -> {
                CustomersController customersController = NavigationManager.getInstance().getCustomersController();
                if (customersController != null) {
                    customersController.showParkingReservations(parking);
                } else {
                    System.err.println("WorkerController: CustomersController not registered!");
                }
            });
        });
    }

    public void navigateToCustomersReservations(Customer customer) {
        System.out.println("WorkerController: Navigating to Customers/Reservations for customer: " + customer.getFullName());

        if (workerMenuController != null) {
            workerMenuController.setActiveMenuItem("Customers");
        }

        selectedMenuItem.set("Customers");

        javafx.application.Platform.runLater(() -> {
            javafx.application.Platform.runLater(() -> {
                CustomersController customersController = NavigationManager.getInstance().getCustomersController();
                if (customersController != null) {
                    customersController.showCustomerReservations(customer);
                } else {
                    System.err.println("WorkerController: CustomersController not registered!");
                }
            });
        });
    }
}