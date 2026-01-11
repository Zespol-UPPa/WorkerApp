package parkflow.deskoptworker.Controllers.Admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.api.ApiClient;
import parkflow.deskoptworker.api.AuthService;

public class AdminController {
    @FXML private BorderPane contentArea;
    @FXML private AdminMenuController adminMenuController;

    private final ViewFactory viewFactory;
    private final StringProperty selectedMenuItem;

    private final AuthService authService;

    // Konstruktor z ViewFactory (Dependency Injection)
    public AdminController(ViewFactory viewFactory) {
        this.authService = new AuthService();
        this.viewFactory = viewFactory;
        this.selectedMenuItem = new SimpleStringProperty("");
    }

    @FXML
    public void initialize() {
        System.out.println("AdminController initialized");

        // Listener na zmianę wybranego menu
        selectedMenuItem.addListener((_, _, newValue) -> {
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
                case "Personnel":
                    contentArea.setCenter(viewFactory.getPersonnelView());
                    break;
                case "Settings":
                        contentArea.setCenter(viewFactory.getSettingsView());
                        break;
                default:
                    System.err.println("Unknown menu item: " + newValue);
            }
        });

        // Połącz z menu controllerem
        if (adminMenuController != null) {
            adminMenuController.setParentController(this);
            System.out.println("Parent controller SET successfully!");
        } else {
            System.err.println("ERROR: adminMenuController is NULL!");
        }

        ApiClient.setSessionExpiredCallback(this::handleSessionExpired);

        // Załaduj domyślny widok
        selectedMenuItem.set("Dashboard");
    }
    private void handleSessionExpired() {

        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.close();

        viewFactory.clearCache();
        viewFactory.showLoginWindow();
    }

    public void handleLogout() {
        authService.logout();

        // Close current window
        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.close();

        viewFactory.clearCache();
        viewFactory.showLoginWindow();
    }



    public void onMenuItemSelected(String menuItem) {
        selectedMenuItem.set(menuItem);
    }
}