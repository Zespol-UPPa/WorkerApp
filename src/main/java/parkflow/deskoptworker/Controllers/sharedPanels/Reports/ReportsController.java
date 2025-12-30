package parkflow.deskoptworker.Controllers.sharedPanels.Reports;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.Views.ReportsViewFactory;

public class ReportsController {
    @FXML private VBox contentArea;
    @FXML private ReportsTopMenuController topMenuController;

    private final ReportsViewFactory reportsViewFactory;
    private final StringProperty selectedMenuItem;

    public ReportsController() {
        this.reportsViewFactory = new ReportsViewFactory();
        this.selectedMenuItem = new SimpleStringProperty("");
    }

    @FXML
    public void initialize() {
        System.out.println("ReportsController initialized");
        System.out.println("topMenuController: " + topMenuController);

        // Listener na zmianę wybranego menu
        selectedMenuItem.addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Overview":
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(reportsViewFactory.getOverviewView());
                    break;
                case "Financial":
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(reportsViewFactory.getFinancialView());
                    break;
                case "Occupancy":
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(reportsViewFactory.getOccupancyView());
                    break;
                case "Session":
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(reportsViewFactory.getSessionView());
                    break;
                case "Customers":
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(reportsViewFactory.getCustomersView());
                    break;
                case "Reservations":
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(reportsViewFactory.getReservationsView());
                    break;
                default:
                    System.err.println("Unknown report menu item: " + newValue);
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
        selectedMenuItem.set("Overview");
    }

    public void onMenuItemSelected(String menuItem) {
        selectedMenuItem.set(menuItem);
    }
}
