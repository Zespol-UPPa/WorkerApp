package parkflow.deskoptworker.Views;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ReportsViewFactory {
    // Cache widoków
    private VBox overviewView;
    private VBox financialView;
    private VBox occupancyView;
    private VBox sessionView;
    private VBox customersView;
    private VBox reservationsView;

    public ReportsViewFactory() {}

    public VBox getOverviewView() {
        if (overviewView == null) {
            try {
                System.out.println("Loading ReportOverview.fxml...");
                overviewView = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/shared/reports/ReportOverview.fxml")
                ).load();
                System.out.println("ReportOverview.fxml loaded successfully");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load ReportOverview.fxml");
                overviewView = createPlaceholder("Overview View");
            }
        }
        return overviewView;
    }

    public VBox getFinancialView() {
        if (financialView == null) {
            try {
                System.out.println("Loading Financial report view...");
                 financialView = new FXMLLoader(
                         getClass().getResource("/parkflow/deskoptworker/shared/reports/ReportFinancial.fxml")
                 ).load();
            } catch (Exception e) {
                e.printStackTrace();
                financialView = createPlaceholder("Financial View");
            }
        }
        return financialView;
    }

    public VBox getOccupancyView() {
        if (occupancyView == null) {
            try {
                System.out.println("Loading Occupancy report view...");
                 occupancyView = new FXMLLoader(
                         getClass().getResource("/parkflow/deskoptworker/shared/reports/ReportOccupancy.fxml")
                 ).load();
            } catch (Exception e) {
                e.printStackTrace();
                occupancyView = createPlaceholder("Occupancy View");
            }
        }
        return occupancyView;
    }

    public VBox getSessionView() {
        if (sessionView == null) {
            try {
                System.out.println("Loading ReportSession" +
                        ".fxml...");
                sessionView = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/shared/reports/ReportSession.fxml")
                ).load();
                System.out.println("ReportOverview.fxml loaded successfully");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load ReportSession.fxml");
                sessionView = createPlaceholder("Session View");
            }
        }
        return sessionView;
    }

    // Worker-only views
    public VBox getCustomersView() {
        if (customersView == null) {
            try {
                customersView = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/shared/reports/ReportCustomers.fxml")
                ).load();
            } catch (Exception e) {
                e.printStackTrace();
                customersView= createPlaceholder("Customers View");
            }
        }
        return customersView;
    }


    public VBox getReservationsView() {
        if (reservationsView == null) {
            try {
                System.out.println("Loading Reservations report view...");
                reservationsView = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/shared/reports/ReportReservations.fxml")
                ).load();
            } catch (Exception e) {
                e.printStackTrace();
                reservationsView = createPlaceholder("Reservation View");
            }
        }
        return reservationsView;
    }

    private VBox createPlaceholder(String text) {
        VBox placeholder = new VBox();
        placeholder.setStyle("-fx-alignment: center; -fx-padding: 50; -fx-background-color: white;");
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #666666;");
        placeholder.getChildren().add(label);
        return placeholder;
    }

    // Metoda do czyszczenia cache jeśli potrzeba
    public void clearCache() {
        overviewView = null;
        financialView = null;
        occupancyView = null;
        sessionView = null;
        customersView = null;
    }
}
