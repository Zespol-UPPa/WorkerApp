package parkflow.deskoptworker.Controllers.Worker;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.models.Customer;
import parkflow.deskoptworker.models.Parking;

public class WorkerController {

    @FXML
    private BorderPane contentArea;

    // WAŻNE: Nazwa musi być: fx:id + "Controller"
    // fx:id="workerMenu" → pole workerMenuController
    @FXML
    private WorkerMenuController workerMenuController;

    private final ViewFactory viewFactory;
    private final StringProperty selectedMenuItem;

    public WorkerController() {
        this.viewFactory = new ViewFactory();
        this.selectedMenuItem = new SimpleStringProperty("");
    }

    @FXML
    public void initialize() {
        System.out.println("WorkerController initialized");
        System.out.println("workerMenuController: " + workerMenuController);

        parkflow.deskoptworker.Navigation.NavigationManager.getInstance().registerWorkerController(this);


        // Listener na zmianę wybranego menu
        selectedMenuItem.addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Dashboard":
                    contentArea.setCenter(viewFactory.getDashboardView());
                    break;
                case "Parkings":
                    contentArea.setCenter(viewFactory.getParkingsView());
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
    }

    public void onMenuItemSelected(String menuItem) {
        selectedMenuItem.set(menuItem);
    }

    /*
    =========DODATKOWE NAWIGACJE================
     */
    /**
     * Nawiguje do Customers → Reservations z filtrem na parking.
     * Wywoływane z NavigationManager.
     */
    /**
     * Nawiguje do Customers → Reservations z filtrem na parking.
     * Wywoływane z NavigationManager.
     */
    public void navigateToCustomersReservations(Parking parking) {
        System.out.println("WorkerController: Navigating to Customers/Reservations for parking: " + parking.getName());

        // Podświetl "Customers" w menu
        if (workerMenuController != null) {
            workerMenuController.setActiveMenuItem("Customers");
        }

        // Przełącz na widok Customers (to załaduje CustomersController jeśli jeszcze nie był)
        selectedMenuItem.set("Customers");

        // Poczekaj na załadowanie widoku i przekaż filtr
        javafx.application.Platform.runLater(() -> {
            // Drugie runLater żeby dać czas na initialize() CustomersController
            javafx.application.Platform.runLater(() -> {
                CustomersController customersController = parkflow.deskoptworker.Navigation.NavigationManager.getInstance().getCustomersController();
                if (customersController != null) {
                    customersController.showParkingReservations(parking);
                } else {
                    System.err.println("WorkerController: CustomersController not registered in NavigationManager!");
                }
            });
        });
    }

    /**
     * Nawiguje do Customers → Reservations z filtrem na klienta.
     * Wywoływane z NavigationManager.
     */
    public void navigateToCustomersReservations(Customer customer) {
        System.out.println("WorkerController: Navigating to Customers/Reservations for customer: " + customer.getFullName());

        // Podświetl "Customers" w menu
        if (workerMenuController != null) {
            workerMenuController.setActiveMenuItem("Customers");
        }

        // Przełącz na widok Customers (to załaduje CustomersController jeśli jeszcze nie był)
        selectedMenuItem.set("Customers");

        // Poczekaj na załadowanie widoku i przekaż filtr
        javafx.application.Platform.runLater(() -> {
            // Drugie runLater żeby dać czas na initialize() CustomersController
            javafx.application.Platform.runLater(() -> {
                CustomersController customersController = parkflow.deskoptworker.Navigation.NavigationManager.getInstance().getCustomersController();
                if (customersController != null) {
                    customersController.showCustomerReservations(customer);
                } else {
                    System.err.println("WorkerController: CustomersController not registered in NavigationManager!");
                }
            });
        });
    }
}