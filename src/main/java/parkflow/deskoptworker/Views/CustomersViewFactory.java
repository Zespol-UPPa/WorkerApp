package parkflow.deskoptworker.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.Controllers.Worker.AllActivityController;
import parkflow.deskoptworker.Controllers.Worker.CustomerProfileController;
import parkflow.deskoptworker.Controllers.Worker.CustomerReservationsController;
import parkflow.deskoptworker.Controllers.Worker.CustomersController;
import parkflow.deskoptworker.Controllers.Worker.CustomersViewController;
import parkflow.deskoptworker.Controllers.Worker.PaymentsViewController;

import parkflow.deskoptworker.models.Customer;
import parkflow.deskoptworker.models.Parking;
import parkflow.deskoptworker.models.Transaction;

import java.io.IOException;
import java.util.List;

public class CustomersViewFactory {
    // Cache widoków
    private VBox customersView;
    private CustomersViewController customersViewController;
    private VBox paymentsView;
    private PaymentsViewController paymentsViewController;
    private VBox reservationsView;
    private CustomerReservationsController reservationsViewController;

    private CustomerProfileController currentProfileController;
    private CustomersController parentController;

    public CustomersViewFactory() {}

    /**
     * Ustawia parent controller - wywoływane z CustomersController
     */
    public void setParentController(CustomersController controller) {
        this.parentController = controller;
        System.out.println("Factory: Parent controller set!");
    }

    // ==================== CUSTOMERS VIEW ====================

    public VBox getCustomersView() {
        if (customersView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/worker/CustomersView.fxml")
                );
                customersView = loader.load();
                customersViewController = loader.getController();

                if (parentController != null) {
                    customersViewController.setParentController(parentController);
                    System.out.println("Factory: Parent controller passed to CustomersViewController!");
                } else {
                    System.err.println("Factory: WARNING - parentController is null in getCustomersView()");
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load CustomersView.fxml");
                customersView = createPlaceholder("Customers View - Coming Soon");
            }
        }
        return customersView;
    }

    // ==================== PAYMENTS VIEW ====================

    public VBox getPaymentsView() {
        if (paymentsView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/worker/PaymentsView.fxml")
                );
                paymentsView = loader.load();
                paymentsViewController = loader.getController();

                if (parentController != null) {
                    paymentsViewController.setParentController(parentController);
                    System.out.println("Factory: Parent controller passed to PaymentsViewController!");
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load PaymentsView.fxml");
                paymentsView = createPlaceholder("Payments View - Coming Soon");
            }
        }
        return paymentsView;
    }

    /**
     * Gets payments view with customer filter applied
     */
    public VBox getPaymentsViewForCustomer(Customer customer) {
        VBox view = getPaymentsView();

        if (paymentsViewController != null && customer != null) {
            paymentsViewController.setCustomerFilter(customer);
            System.out.println("Factory: Customer filter set for payments: " + customer.getFullName());
        }

        return view;
    }

    /**
     * Clears customer filter on payments view
     */
    public void clearPaymentsFilter() {
        if (paymentsViewController != null) {
            paymentsViewController.clearCustomerFilter();
        }
    }

    /**
     * Gets the payments view controller for direct manipulation
     */
    public PaymentsViewController getPaymentsViewController() {
        getPaymentsView(); // Ensure view is loaded
        return paymentsViewController;
    }

    // ==================== RESERVATIONS VIEW ====================

    public VBox getReservationsView() {
        if (reservationsView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/worker/CustomerReservation.fxml")
                );
                reservationsView = loader.load();
                reservationsViewController = loader.getController();

                if (parentController != null) {
                    reservationsViewController.setParentController(parentController);
                    System.out.println("Factory: Parent controller passed to ReservationsViewController!");
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load ReservationsView.fxml");
                reservationsView = createPlaceholder("Reservations View - Coming Soon");
            }
        }
        return reservationsView;
    }

    /**
     * Gets reservations view with customer filter applied
     */
    public VBox getReservationsViewForCustomer(Customer customer) {
        VBox view = getReservationsView();

        if (reservationsViewController != null && customer != null) {
            reservationsViewController.setCustomerFilter(customer);
            System.out.println("Factory: Customer filter set for " + customer.getFullName());
        }

        return view;
    }

    /**
     * Gets reservations view with parking filter applied
     */
    public VBox getReservationsViewForParking(Parking parking) {
        VBox view = getReservationsView();

        if (reservationsViewController != null && parking != null) {
            reservationsViewController.setParkingFilter(parking);
            System.out.println("Factory: Parking filter set for " + parking.getName());
        }

        return view;
    }

    /**
     * Clears any entity filter on reservations view
     */
    public void clearReservationsFilter() {
        if (reservationsViewController != null) {
            reservationsViewController.clearEntityFilter();
        }
    }

    /**
     * Gets the reservations view controller for direct manipulation
     */
    public CustomerReservationsController getReservationsViewController() {
        getReservationsView();
        return reservationsViewController;
    }

    // ==================== CUSTOMER PROFILE ====================

    /**
     * Tworzy widok profilu klienta z już skonfigurowanym listenerem
     */
    public VBox getCustomerProfileView(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/worker/CustomerProfile.fxml")
            );

            VBox customerProfileView = loader.load();
            currentProfileController = loader.getController();
            currentProfileController.setCustomer(customer);

            // Setup listener BEZPOŚREDNIO tutaj
            setupProfileListener(customer);

            return customerProfileView;

        } catch (IOException e) {
            e.printStackTrace();
            return createPlaceholder("Failed to load customer profile");
        }
    }

    /**
     * PRIVATE - Setup listener wewnętrznie
     */
    private void setupProfileListener(Customer customer) {
        if (currentProfileController == null || parentController == null) {
            System.err.println("Cannot setup listener - controller or parent is null");
            return;
        }

        currentProfileController.setListener(new CustomerProfileController.CustomerProfileListener() {
            @Override
            public void onClearFilter() {
                parentController.showCustomersList();
            }

            @Override
            public void onViewPendingPayments(Customer customer) {
                parentController.showCustomerPayments(customer);
            }

            @Override
            public void onViewAllReservations(Customer customer) {
                parentController.showCustomerReservations(customer);
            }

            @Override
            public void onViewAllPayments(Customer customer) {
                parentController.showCustomerPayments(customer);
            }

            @Override
            public void onViewAllActivity(Customer customer, List<Transaction> transactions) {
                // TERAZ Factory tylko TWORZY widok i zwraca go do CustomersController
                // CustomersController sam zdecyduje co z nim zrobić
                VBox allActivityView = createAllActivityView(customer, transactions);
                if (allActivityView != null) {
                    parentController.showCustomView(allActivityView);
                }
            }
        });
    }

    /**
     * Tworzy widok wszystkich aktywności (bez samodzielnego wyświetlania)
     * @return VBox z AllActivityView lub null jeśli błąd
     */
    private VBox createAllActivityView(Customer customer, List<Transaction> transactions) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/worker/AllActivityView.fxml")
            );
            VBox allActivityView = loader.load();

            AllActivityController controller = loader.getController();
            controller.setCustomer(customer, transactions);

            // Setup listener do powrotu
            controller.setListener(() -> {
                if (parentController != null) {
                    parentController.showCustomerProfile(customer);
                }
            });

            return allActivityView;

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load AllActivityView.fxml");
            return null;
        }
    }

    /**
     * Resets all cached views (useful for refresh)
     */
    public void resetViews() {
        customersView = null;
        customersViewController = null;
        paymentsView = null;
        paymentsViewController = null;
        reservationsView = null;
        reservationsViewController = null;
        currentProfileController = null;
    }

    private VBox createPlaceholder(String text) {
        VBox placeholder = new VBox();
        placeholder.setStyle("-fx-alignment: center; -fx-padding: 50; -fx-background-color: #F2F3FE;");
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #666666;");
        placeholder.getChildren().add(label);
        return placeholder;
    }
}