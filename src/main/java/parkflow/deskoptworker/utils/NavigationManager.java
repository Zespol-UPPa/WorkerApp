package parkflow.deskoptworker.utils;

import parkflow.deskoptworker.Controllers.Admin.AdminController;
import parkflow.deskoptworker.Controllers.Worker.CustomersController;
import parkflow.deskoptworker.Controllers.Worker.WorkerController;

/**
 * NavigationManager - Singleton for cross-controller navigation
 * Allows Dashboard and other controllers to trigger menu navigation
 *
 * UPDATED: Added debug logging
 */
public class NavigationManager {

    private static NavigationManager instance;

    private AdminController adminController;
    private WorkerController workerController;
    private CustomersController customersController;

    private NavigationManager() {
        System.out.println("NavigationManager: Instance created");
    }

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    // ==================== REGISTRATION ====================

    public void registerAdminController(AdminController controller) {
        this.adminController = controller;
        System.out.println("✅ NavigationManager: AdminController registered");
    }

    public void registerWorkerController(WorkerController controller) {
        this.workerController = controller;
        System.out.println("✅ NavigationManager: WorkerController registered");
    }

    public void registerCustomersController(CustomersController controller) {
        this.customersController = controller;
        System.out.println("✅ NavigationManager: CustomersController registered");
    }

    // ==================== GETTERS ====================

    public CustomersController getCustomersController() {
        return customersController;
    }

    // ==================== NAVIGATION METHODS ====================

    /**
     * Navigate to Reports from anywhere
     */
    public void navigateToReports() {
        System.out.println("🔷 NavigationManager.navigateToReports() called");
        System.out.println("   - adminController: " + (adminController != null ? "✅" : "❌"));
        System.out.println("   - workerController: " + (workerController != null ? "✅" : "❌"));

        if (adminController != null) {
            System.out.println("→ Navigating to Reports (Admin)");
            adminController.onMenuItemSelected("Reports");
        } else if (workerController != null) {
            System.out.println("→ Navigating to Reports (Worker)");
            workerController.onMenuItemSelected("Reports");
        } else {
            System.err.println("❌ NavigationManager: No controller registered!");
        }
    }

    /**
     * Navigate to Parkings (Admin only)
     */
    public void navigateToParkings() {
        System.out.println("🔷 NavigationManager.navigateToParkings() called");
        System.out.println("   - adminController: " + (adminController != null ? "✅" : "❌"));

        if (adminController != null) {
            System.out.println("→ Navigating to Parkings");
            adminController.onMenuItemSelected("Parkings");
        } else {
            System.err.println("❌ NavigationManager: AdminController not registered!");
        }
    }

    /**
     * Navigate to Settings from anywhere
     */
    public void navigateToSettings() {
        System.out.println(" NavigationManager.navigateToSettings() called");
        System.out.println("   - adminController: " + (adminController != null ? "" : "❌"));
        System.out.println("   - workerController: " + (workerController != null ? "✅" : "❌"));

        if (adminController != null) {
            System.out.println("→ Navigating to Settings (Admin)");
            adminController.onMenuItemSelected("Settings");
        } else if (workerController != null) {
            System.out.println("→ Navigating to Settings (Worker)");
            workerController.onMenuItemSelected("Settings");
        } else {
            System.err.println(" NavigationManager: No controller registered!");
        }
    }

    /**
     * Navigate to Dashboard from anywhere
     */
    public void navigateToDashboard() {
        System.out.println("NavigationManager.navigateToDashboard() called");

        if (adminController != null) {
            System.out.println("→ Navigating to Dashboard (Admin)");
            adminController.onMenuItemSelected("Dashboard");
        } else if (workerController != null) {
            System.out.println("→ Navigating to Dashboard (Worker)");
            workerController.onMenuItemSelected("Dashboard");
        } else {
            System.err.println("NavigationManager: No controller registered!");
        }
    }

    /**
     * Navigate to Personnel (Admin only)
     */
    public void navigateToPersonnel() {
        System.out.println("NavigationManager.navigateToPersonnel() called");

        if (adminController != null) {
            System.out.println("→ Navigating to Personnel");
            adminController.onMenuItemSelected("Personnel");
        } else {
            System.err.println("NavigationManager: AdminController not registered!");
        }
    }

    /**
     * Navigate to Customers (Worker only)
     */
    public void navigateToCustomers() {
        System.out.println("NavigationManager.navigateToCustomers() called");

        if (workerController != null) {
            System.out.println("→ Navigating to Customers");
            workerController.onMenuItemSelected("Customers");
        } else {
            System.err.println("NavigationManager: WorkerController not registered!");
        }
    }

    /**
     * Clear all registered controllers (on logout)
     */
    public void clear() {
        adminController = null;
        workerController = null;
        customersController = null;
        System.out.println("NavigationManager: All controllers cleared");
    }
}