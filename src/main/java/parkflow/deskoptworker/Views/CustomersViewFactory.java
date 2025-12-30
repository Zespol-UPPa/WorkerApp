package parkflow.deskoptworker.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.Controllers.Worker.CustomerProfileController;
import parkflow.deskoptworker.Controllers.Worker.CustomerReservationsController;
import parkflow.deskoptworker.Controllers.Worker.CustomersController;
import parkflow.deskoptworker.Controllers.Worker.CustomersViewController;

import parkflow.deskoptworker.models.Customer;
import parkflow.deskoptworker.models.Parking;

import java.io.IOException;

public class CustomersViewFactory {
    // Cache widoków
    private VBox customersView;
    private CustomersViewController customersViewController;
    private VBox paymentsView;
    private VBox reservationsView;
    private CustomerReservationsController reservationsViewController;
    private VBox customerProfileView;

    private CustomerProfileController currentProfileController;
    private Customer currentProfileCustomer;
    private CustomersController parentController;

    public CustomersViewFactory() {}

    /**
     * Ustawia parent controller - wywoływane z CustomersController
     */
    public void setParentController(CustomersController controller) {
        this.parentController = controller;
        System.out.println("Factory: Parent controller set!");
    }

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

    public VBox getPaymentsView() {
        if (paymentsView == null) {
            try {
                paymentsView = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/worker/PaymentsView.fxml")
                ).load();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load PaymentsView.fxml");
                paymentsView = createPlaceholder("Payments View - Coming Soon");
            }
        }
        return paymentsView;
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
        // Ensure view is loaded
        getReservationsView();
        return reservationsViewController;
    }

    // ==================== CUSTOMER PROFILE ====================

    /**
     * Tworzy widok profilu klienta
     */
    public VBox getCustomerProfileView(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/worker/CustomerProfile.fxml")
            );

            customerProfileView = loader.load();
            currentProfileController = loader.getController();
            currentProfileController.setCustomer(customer);

            currentProfileCustomer = customer;
            return customerProfileView;

        } catch (IOException e) {
            e.printStackTrace();
            return createPlaceholder("Failed to load customer profile");
        }
    }

    /**
     * Ustawia listener dla profilu klienta
     * Musi być wywołane ZARAZ PO getCustomerProfileView()
     */
    public void setupProfileListener(CustomersController parentController, Customer customer) {
        if (currentProfileController == null) {
            System.err.println("Profile controller is null! Call getCustomerProfileView() first.");
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
        });
    }


    /**
     * Resets all cached views (useful for refresh)
     */
    public void resetViews() {
        customersView = null;
        customersViewController = null;
        paymentsView = null;
        reservationsView = null;
        reservationsViewController = null;
        customerProfileView = null;
        currentProfileController = null;
        currentProfileCustomer = null;
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