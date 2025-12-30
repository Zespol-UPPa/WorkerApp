package parkflow.deskoptworker.Controllers.Admin;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import parkflow.deskoptworker.Views.ViewFactory;

public class AdminController {
    @FXML private BorderPane contentArea;

    @FXML private AdminMenuController adminMenuController;

    private final ViewFactory viewFactory;
    private final StringProperty selectedMenuItem;

    public AdminController() {
        this.viewFactory = new ViewFactory();
        this.selectedMenuItem = new SimpleStringProperty("");
    }

    @FXML
    public void initialize() {
        System.out.println("AdminController initialized");
        System.out.println("adminMenuController: "+ adminMenuController);
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
//                case "Settings":
//                    contentArea.setCenter(viewFactory.getSettingsView());
//                    break;
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


        // Załaduj domyślny widok
        selectedMenuItem.set("Dashboard");
    }

    public void onMenuItemSelected(String menuItem) {
        selectedMenuItem.set(menuItem);
    }
}