package parkflow.deskoptworker.Controllers.Worker;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.Views.CustomersViewFactory;
import parkflow.deskoptworker.models.Customer;
import parkflow.deskoptworker.models.Parking;

public class CustomersController {
    @FXML private VBox contentArea;
    @FXML private CustomersTopMenuController topMenuController;

    private final CustomersViewFactory customersViewFactory;
    private final StringProperty selectedMenuItem;

    public CustomersController() {
        this.customersViewFactory = new CustomersViewFactory();
        this.selectedMenuItem = new SimpleStringProperty("");
    }

    @FXML
    public void initialize() {
        System.out.println("CustomersController initialized");
        System.out.println("topMenuController: " + topMenuController);

        parkflow.deskoptworker.Navigation.NavigationManager.getInstance().registerCustomersController(this);

        // KRYTYCZNE: Przekaż siebie do Factory PRZED załadowaniem widoków
        customersViewFactory.setParentController(this);
        System.out.println("CustomersController: setParentController() called on factory");

        // Listener na zmianę wybranego menu
        selectedMenuItem.addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Customers":
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(customersViewFactory.getCustomersView());
                    // Ukryj filtered gdy wracamy do listy
                    if (topMenuController != null) {
                        topMenuController.hideCustomersFilter();
                    }
                    break;
                case "Payments":
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(customersViewFactory.getPaymentsView());
                    break;
                case "Reservations":
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(customersViewFactory.getReservationsView());
                    break;
                default:
                    System.err.println("Unknown customers menu item: " + newValue);
            }
        });

        // Połącz z menu controllerem
        if (topMenuController != null) {
            topMenuController.setParentController(this);
            System.out.println("Parent controller SET successfully!");
        } else {
            System.err.println("ERROR: topMenuController is NULL!");
        }

        // Załaduj domyślny widok
        selectedMenuItem.set("Customers");
    }

    public void showCustomerProfile(Customer customer) {
        System.out.println("Showing profile for: " + customer.getFullName());

        contentArea.getChildren().clear();
        VBox profileView = customersViewFactory.getCustomerProfileView(customer);

        // Setup listener w profile controller przez Factory
        customersViewFactory.setupProfileListener(this, customer);

        contentArea.getChildren().add(profileView);
        VBox.setVgrow(profileView, javafx.scene.layout.Priority.ALWAYS);

        // Pokaż "filtered" w top menu
        if (topMenuController != null) {
            topMenuController.showCustomersFilter();
        }
    }

    /**
     * Przejdź do Payments z filtrem na klienta
     */
    public void showCustomerPayments(Customer customer) {
        System.out.println("Show payments for: " + customer.getFullName());

        // Załaduj widok Payments
        contentArea.getChildren().clear();
        VBox paymentsView = customersViewFactory.getPaymentsView();
        contentArea.getChildren().add(paymentsView);

        // TODO: Ustaw filtr na klienta w PaymentsController
        // customersViewFactory.getPaymentsViewController().setCustomerFilter(customer);

        // Ustaw menu na Payments i pokaż filtered
        if (topMenuController != null) {
            topMenuController.setActiveTab("Payments");
            topMenuController.showPaymentsFilter();
        }
    }

    /**
     * Przejdź do Reservations z filtrem na klienta
     */
    public void showCustomerReservations(Customer customer) {
        System.out.println("Show reservations for: " + customer.getFullName());

        // Załaduj widok Reservations z filtrem klienta
        contentArea.getChildren().clear();
        VBox reservationsView = customersViewFactory.getReservationsViewForCustomer(customer);
        contentArea.getChildren().add(reservationsView);

        // Ustaw menu na Reservations i pokaż filtered
        if (topMenuController != null) {
            topMenuController.setActiveTab("Reservations");
            topMenuController.showReservationsFilter();
        }
    }

    // ==================== PARKING NAVIGATION ====================

    /**
     * Przejdź do Reservations z filtrem na parking.
     * Wywoływane z WorkerController po nawigacji z Parkings.
     */
    public void showParkingReservations(Parking parking) {
        System.out.println("Show reservations for parking: " + parking.getName());

        // Załaduj widok Reservations z filtrem parkingu
        contentArea.getChildren().clear();
        VBox reservationsView = customersViewFactory.getReservationsViewForParking(parking);
        contentArea.getChildren().add(reservationsView);

        // Ustaw menu na Reservations i pokaż filtered
        if (topMenuController != null) {
            topMenuController.setActiveTab("Reservations");
            topMenuController.showReservationsFilter();
        }
    }

    // ==================== CLEAR FILTERS ====================

    /**
     * Wraca do listy wszystkich klientów
     */
    public void showCustomersList() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(customersViewFactory.getCustomersView());

        // Hide all filters
        if (topMenuController != null) {
            topMenuController.setActiveTab("Customers");
            topMenuController.hideCustomersFilter();
            topMenuController.hidePaymentsFilter();
            topMenuController.hideReservationsFilter();
        }
    }

    /**
     * Wraca do wszystkich rezerwacji (bez filtra)
     */
    public void showAllReservations() {
        System.out.println("Showing all reservations (no filter)");

        // Wyczyść filtr w kontrolerze rezerwacji
        customersViewFactory.clearReservationsFilter();

        // Załaduj widok rezerwacji
        contentArea.getChildren().clear();
        contentArea.getChildren().add(customersViewFactory.getReservationsView());

        // Ukryj filtered label ale zostaw na zakładce Reservations
        if (topMenuController != null) {
            topMenuController.setActiveTab("Reservations");
            topMenuController.hideReservationsFilter();
        }
    }

    public void onMenuItemSelected(String menuItem) {
        // Przy ręcznym wyborze z menu - wyczyść filtry
        if (menuItem.equals("Reservations")) {
            customersViewFactory.clearReservationsFilter();
            if (topMenuController != null) {
                topMenuController.hideReservationsFilter();
            }
        }
        selectedMenuItem.set(menuItem);
    }
}