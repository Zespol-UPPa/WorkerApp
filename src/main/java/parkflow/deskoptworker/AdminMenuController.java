package parkflow.deskoptworker;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminMenuController {

    @FXML
    private Button dashboardBtn;
    @FXML
    private Button parkingBtn;
    @FXML
    private Button reportsBtn;
    @FXML
    private Button personnelBtn;
    @FXML
    private Button settingsBtn;
    @FXML
    private Button logoutBtn;

    private Button currentButton;

    public void initialize() {

        setActiveButton(dashboardBtn);

        dashboardBtn.setOnAction(e -> handleMenuClick(dashboardBtn));
        parkingBtn.setOnAction(e -> handleMenuClick(parkingBtn));
        reportsBtn.setOnAction(e -> handleMenuClick(reportsBtn));
        personnelBtn.setOnAction(e -> handleMenuClick(personnelBtn));
        settingsBtn.setOnAction(e -> handleMenuClick(settingsBtn));


    }

    private void handleMenuClick(Button clickedButton) {
        setActiveButton(clickedButton);

        // Tutaj dodasz logikę do zmiany widoku
        if (clickedButton == dashboardBtn) {
            System.out.println("Showing Dashboard");
            // TODO: loadDashboardView();
        } else if (clickedButton == parkingBtn) {
            System.out.println("Showing Parkings");
            // TODO: loadParkingsView();
        } else if (clickedButton == reportsBtn) {
            System.out.println("Showing Reports");
            // TODO: loadReportsView();
        } else if (clickedButton == personnelBtn) {
            System.out.println("Showing Personnel");
            // TODO: loadPersonnelView();
        } else if (clickedButton == settingsBtn) {
            System.out.println("Showing Settings");
            // TODO: loadSettingsView();
        }
    }
    private void setActiveButton(Button button) {
        //usun klase poprzedniego
        if (currentButton != null) {
            currentButton.getStyleClass().remove("current");
        }

        // Dodaj klasę dla nowego
        button.getStyleClass().add("current");

        currentButton = button;
    }
}
