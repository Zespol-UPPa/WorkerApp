package parkflow.deskoptworker.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import parkflow.deskoptworker.Controllers.Admin.*;
import parkflow.deskoptworker.models.User;

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

    // Shared views
    public VBox getDashboardView() {

        System.out.println("Showing dashboard view");
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

    // Admin-only views
    public VBox getPersonnelView() {
        System.out.println("=== getPersonnelView() called ===");

        if (personnelView == null) {
            try {
                System.out.println("Loading PersonnelManagement.fxml...");

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/admin/Personnel.fxml")
                );

                System.out.println("FXML URL: " + loader.getLocation());

                personnelView = loader.load();
                System.out.println("FXML loaded successfully");
                System.out.println("PersonnelView: " + personnelView);

                // Przekaż ViewFactory do controllera
                PersonnelController controller = loader.getController();
                System.out.println("Controller: " + controller);

                if (controller != null) {
                    controller.setViewFactory(this);
                    System.out.println("ViewFactory przekazany do PersonnelController");
                } else {
                    System.err.println("Controller is NULL!");
                }

            } catch (Exception e) {
                System.err.println(" Failed to load Personnel.fxml");
                e.printStackTrace();
            }
        } else {
            System.out.println("Returning cached personnelView");
        }

        System.out.println("=== Returning personnelView: " + personnelView + " ===");
        return personnelView;
    }

    // Worker-only views
    public VBox getCustomersView() {
        if (customersView == null) {
            try {
             customersView= new FXMLLoader(
                     getClass().getResource("/parkflow/deskoptworker/worker/Customers.fxml"))
                     .load();
            } catch (Exception e) {
                e.printStackTrace();
                // Tymczasowo zwróć placeholder
                customersView = createPlaceholder("Customers View - Coming Soon");
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

            AdminController controller = new AdminController();
            loader.setController(controller);

            BorderPane root = loader.load();

            System.out.println("Admin.fxml loaded");
            System.out.println("Controller: " + controller);

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

            BorderPane root = loader.load();
            System.out.println("Worker.fxml loaded");

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

    /**
     * Otwiera okno dodawania pracownika (modal, bez paska)
     */
    public User showAddEmployeeModal() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/admin/AddNewEmployee.fxml")
            );

            Parent root = loader.load();

            // Dodaj cień
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(50);
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(20);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));
            root.setEffect(dropShadow);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setResizable(false);

            stage.showAndWait();

            // Pobierz controller i sprawdź czy zapisano
            AddEmpController controller = loader.getController();
            return controller.getSavedEmployee(); // Zwróć nowego użytkownika lub null

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load AddNewEmployee.fxml");
            return null;
        }}

    /**
     * Otwiera okno szczegółów pracownika (modal, bez paska)
     */
    public void showEmployeeDetailsModal(User employee) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/admin/EmployeeDetails.fxml")
            );

            Parent root = loader.load();

            // TERAZ możesz pobrać controller i wywołać metody
            EmpDetController controller = loader.getController();
            controller.setEmployee(employee);
            controller.updateView();


            // Dodaj cień do root
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(50);
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(20);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.1));
            root.setEffect(dropShadow);

            // Użyj już załadowanego root
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);


            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT); // Tylko TRANSPARENT, nie UNDECORATED osobno
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setResizable(false);

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load EmployeeDetails.fxml");
        }
    }

    /**
     * Otwiera okno dezaktywacji pracownika (modal, bez paska)
     * Zwraca true jeśli użytkownik potwierdził dezaktywację
     */
    public boolean showDeactivateEmployeeModal(User employee) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/admin/DeactivateEmployee.fxml")
            );

            Parent root = loader.load(); // Załaduj TYLKO RAZ

            // Dodaj cień do root
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(50);
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(20);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.1));
            root.setEffect(dropShadow);

            // Użyj już załadowanego root
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            DeactivateController controller = loader.getController();
            controller.setEmployee(employee);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT); // Tylko TRANSPARENT, nie UNDECORATED osobno
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setResizable(false);

            stage.showAndWait();

            return controller.isConfirmed();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load DeactivateEmployee.fxml");
            return false;
        }
    }

    /**
     * Otwiera okno aktywacji pracownika (modal, bez paska)
     * Zwraca true jeśli użytkownik potwierdził aktywację
     */
    public boolean showActivateEmployeeModal(User employee) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/admin/ActivateEmployee.fxml")
            );

            Scene scene = new Scene(loader.load());
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            // Tutaj też przekażesz dane gdy będziesz mieć controller

            Stage stage = new Stage();
            stage.setTitle("Activate Employee");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setResizable(false);

            stage.showAndWait();

            return true; // Zwróć rezultat z controllera

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load ActivateEmployee.fxml");
            return false;
        }
    }




    public void closeStage(Stage stage) {
        stage.close();
    }
}