package parkflow.deskoptworker.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import parkflow.deskoptworker.Controllers.Admin.*;
import parkflow.deskoptworker.Controllers.Worker.PricingWController;
import parkflow.deskoptworker.Controllers.Worker.WorkerController;
import parkflow.deskoptworker.Controllers.sharedPanels.ParkingsController;
import parkflow.deskoptworker.models.Parking;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;
import parkflow.deskoptworker.utils.ModalHelper;

import java.io.IOException;
import java.util.Objects;

public class ViewFactory {

    // Cache widoków
    private VBox dashboardView;
    private VBox parkingsView;
    private VBox reportsView;
    private VBox personnelView;
    private VBox customersView;
    private VBox settingsView;

    public ViewFactory() {}

    // ============ SHARED VIEWS ============

    public VBox getDashboardView() {
        if (dashboardView == null) {
            try {
                dashboardView = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/shared/dashboard.fxml")
                ).load();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load dashboard.fxml");
            }
        }
        return dashboardView;
    }

    public VBox getParkingsView() {
        if (parkingsView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/shared/parkings.fxml")
                );
                parkingsView = loader.load();

                // Przekaż ViewFactory do ParkingsController
                ParkingsController controller = loader.getController();
                if (controller != null) {
                    controller.setViewFactory(this);
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load parkings.fxml");
            }
        }
        return parkingsView;
    }

    public VBox getReportsView() {
        if (reportsView == null) {
            try {
                reportsView = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/shared/reports/ReportsMain.fxml"))
                        .load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return reportsView;
    }

    public VBox getSettingsView() {
        if (settingsView == null) {
            try {
                settingsView = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/shared/Settings.fxml"))
                        .load();
            } catch (Exception e) {
                settingsView = createPlaceholder("Settings View - Coming Soon");
                e.printStackTrace();
            }
        }
        return settingsView;
    }

    // ============ ADMIN-ONLY VIEWS ============

    public VBox getPersonnelView() {
        if (personnelView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/admin/Personnel.fxml")
                );
                personnelView = loader.load();

                PersonnelController controller = loader.getController();
                if (controller != null) {
                    controller.setViewFactory(this);
                }

            } catch (Exception e) {
                System.err.println("Failed to load Personnel.fxml");
                e.printStackTrace();
            }
        }
        return personnelView;
    }

    // ============ WORKER-ONLY VIEWS ============

    public VBox getCustomersView() {
        if (customersView == null) {
            try {
                customersView = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/worker/Customers.fxml"))
                        .load();
            } catch (Exception e) {
                e.printStackTrace();
                customersView = createPlaceholder("Customers View - Coming Soon");
            }
        }
        return customersView;
    }

    // ============ HELPER ============

    private VBox createPlaceholder(String text) {
        VBox placeholder = new VBox();
        placeholder.setStyle("-fx-alignment: center; -fx-padding: 50; -fx-background-color: white;");
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #666666;");
        placeholder.getChildren().add(label);
        return placeholder;
    }

    // ============ WINDOW METHODS ============

    public void showAdminWindow() {
        System.out.println("Admin window opened");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/admin/Admin.fxml")
            );

            // Przekaż ViewFactory do AdminController
            AdminController controller = new AdminController(this);
            loader.setController(controller);

            BorderPane root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setMinWidth(1200);
            stage.setMinHeight(700);
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.setTitle("ParkFlow");
            stage.getIcons().add(
                    new Image(
                            Objects.requireNonNull(
                                    getClass().getResourceAsStream("/parkflow/deskoptworker/images/LogoIcon.png")
                            )
                    )
            );
            stage.show();

        } catch (Exception e) {
            System.err.println("Failed to load Admin window!");
            e.printStackTrace();
        }
    }

    public void showWorkerWindow() {
        System.out.println("Worker window opened");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/worker/Worker.fxml")
            );

            // Przekaż ViewFactory do WorkerController
            WorkerController controller = new WorkerController(this);
            loader.setController(controller);

            BorderPane root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setMinWidth(1200);
            stage.setMinHeight(700);
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.setTitle("ParkFlow - Worker");
            stage.getIcons().add(
                    new Image(
                            Objects.requireNonNull(
                                    getClass().getResourceAsStream("/parkflow/deskoptworker/images/LogoIcon.png")
                            )
                    )
            );
            stage.show();

        } catch (Exception e) {
            System.err.println("Failed to load Worker window!");
            e.printStackTrace();
        }
    }

    // ============ MODAL METHODS ============

    public User showAddEmployeeModal() {
        AddEmpController controller = ModalHelper.showModal(
                "/parkflow/deskoptworker/admin/AddNewEmployee.fxml",
                "Add Employee"
        );
        return controller != null ? controller.getSavedEmployee() : null;
    }

    public void showEmployeeDetailsModal(User employee) {
        ModalHelper.showModal(
                "/parkflow/deskoptworker/admin/EmployeeDetails.fxml",
                "Employee Details",
                (EmpDetController controller) -> {
                    controller.setEmployee(employee);
                    controller.updateView();
                }
        );
    }

    public boolean showDeactivateEmployeeModal(User employee) {
        DeactivateController controller = ModalHelper.showModal(
                "/parkflow/deskoptworker/admin/DeactivateEmployee.fxml",
                "Deactivate Employee",
                (DeactivateController c) -> c.setEmployee(employee)
        );
        return controller != null && controller.isConfirmed();
    }



    public void showPricingModal(Parking parking, UserRole userRole) {
        if (userRole == UserRole.ADMIN) {
            ModalHelper.showModal(
                    "/parkflow/deskoptworker/admin/pricingDetailsA.fxml",
                    "View Pricing",
                    (PricingControllerA c) -> c.setParkingData(parking)
            );
        } else {
            ModalHelper.showModal(
                    "/parkflow/deskoptworker/worker/pricingDetailsE.fxml",
                    "View Pricing",
                    (PricingWController c) -> c.setParkingData(parking)
            );
        }
    }

    public void showAddParkingModal() {
        ModalHelper.showModal(
                "/parkflow/deskoptworker/admin/addNewParking.fxml",
                "Add new parking"
        );
    }


    /**
     * Czyści cache widoków (np. przy wylogowaniu)
     */
    public void clearCache() {
        dashboardView = null;
        parkingsView = null;
        reportsView = null;
        personnelView = null;
        customersView = null;
        settingsView = null;
    }
}