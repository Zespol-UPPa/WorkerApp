package parkflow.deskoptworker.utils;

import lombok.Getter;
import parkflow.deskoptworker.Controllers.Worker.CustomersController;
import parkflow.deskoptworker.Controllers.Worker.WorkerController;
import parkflow.deskoptworker.models.Customer;
import parkflow.deskoptworker.models.Parking;

/**
 * Singleton do nawigacji między modułami aplikacji.
 * Używany gdy komponent z jednego modułu (np. Parkings)
 * chce przejść do innego modułu (np. Customers/Reservations) z parametrami.
 */
@Getter
public class NavigationManager {

    private static NavigationManager instance;

    private WorkerController workerController;
    private CustomersController customersController;

    private NavigationManager() {}

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    // ==================== REJESTRACJA KONTROLERÓW ====================

    /**
     * Rejestruje WorkerController (wywoływane z WorkerController.initialize())
     */
    public void registerWorkerController(WorkerController controller) {
        this.workerController = controller;
        System.out.println("NavigationManager: WorkerController registered");
    }

    /**
     * Rejestruje CustomersController (wywoływane z CustomersController.initialize())
     */
    public void registerCustomersController(CustomersController controller) {
        this.customersController = controller;
        System.out.println("NavigationManager: CustomersController registered");
    }

    // ==================== NAWIGACJA ====================

    /**
     * Nawiguje do Customers → Reservations z filtrem na parking.
     * Wywoływane z ParkingItemController.
     */
    public void navigateToReservationsWithParkingFilter(Parking parking) {
        if (workerController == null) {
            System.err.println("NavigationManager: WorkerController not registered!");
            return;
        }

        System.out.println("NavigationManager: Navigating to reservations for parking: " + parking.getName());
        workerController.navigateToCustomersReservations(parking);
    }

    /**
     * Nawiguje do Customers → Reservations z filtrem na klienta.
     */
    public void navigateToReservationsWithCustomerFilter(Customer customer) {
        if (workerController == null) {
            System.err.println("NavigationManager: WorkerController not registered!");
            return;
        }

        System.out.println("NavigationManager: Navigating to reservations for customer: " + customer.getFullName());
        workerController.navigateToCustomersReservations(customer);
    }

    // ==================== GETTERY ====================

    /**
     * Czyści wszystkie referencje (wywoływane przy wylogowaniu)
     */
    public void clear() {
        workerController = null;
        customersController = null;
        System.out.println("NavigationManager: Cleared all references");
    }
}