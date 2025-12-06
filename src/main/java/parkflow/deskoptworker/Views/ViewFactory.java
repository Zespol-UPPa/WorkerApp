package parkflow.deskoptworker.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import parkflow.deskoptworker.Controllers.Admin.AdminController;

import java.io.IOException;

public class ViewFactory {
    // Cache widoków
    private VBox dashboardView;
    private VBox parkingsView;
    private VBox reportsView;
    private VBox personnelView;
    private VBox customersView;
    private VBox settingsView;

    public ViewFactory() {}

    // Shared views
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
                parkingsView = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/shared/parkings.fxml")
                ).load();
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
                // Tymczasowo zwróć placeholder
                reportsView = createPlaceholder("Reports View - Coming Soon");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return reportsView;
    }

    public VBox getSettingsView() {
        if (settingsView == null) {
            try {
                // Tymczasowo zwróć placeholder
                settingsView = createPlaceholder("Settings View - Coming Soon");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return settingsView;
    }

    // Admin-only views
    public VBox getPersonnelView() {
        if (personnelView == null) {
            try {
                // Tymczasowo zwróć placeholder
                personnelView = createPlaceholder("Personnel View - Coming Soon");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return personnelView;
    }

    // Worker-only views
    public VBox getCustomersView() {
        if (customersView == null) {
            try {
                // Tymczasowo zwróć placeholder
                customersView = createPlaceholder("Customers View - Coming Soon");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return customersView;
    }

    private VBox createPlaceholder(String text) {
        VBox placeholder = new VBox();
        placeholder.setStyle("-fx-alignment: center; -fx-padding: 50; -fx-background-color: white;");
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #666666;");
        placeholder.getChildren().add(label);
        return placeholder;
    }

    // Window methods
    public void showAdminWindow() {
        System.out.println("Admin window opened");
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/admin/Admin.fxml")
            );

            // RĘCZNE PRZYPISANIE CONTROLLERA
            AdminController controller = new AdminController();
            loader.setController(controller);

            BorderPane root = loader.load();

            System.out.println("✓ Admin.fxml loaded");
            System.out.println("✓ Controller: " + controller);

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setMinWidth(1200);
            stage.setMinHeight(700);


            stage.setMaximized(true);
            stage.setScene(scene);
            stage.setTitle("ParkFlow - Admin Panel");
            stage.show();

        } catch (Exception e) {
            System.err.println("✗ Failed to load Admin window!");
            e.printStackTrace();
        }
    }

    public void showWorkerWindow() {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/parkflow/deskoptworker/employee/Worker.fxml")
        );
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("ParkFlow");
        stage.show();
    }

    public void closeStage(Stage stage) {
        stage.close();
    }
}