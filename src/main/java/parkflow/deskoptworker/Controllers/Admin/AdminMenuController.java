package parkflow.deskoptworker.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminMenuController {
    @FXML private Button dashboardBtn;
    @FXML private Button parkingBtn;
    @FXML private Button reportsBtn;
    @FXML private Button personnelBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;

    private Button currentButton;
    private AdminController parentController;

    public void initialize() {
        setActiveButton(dashboardBtn);

        dashboardBtn.setOnAction(e -> handleMenuClick(dashboardBtn, "Dashboard"));
        parkingBtn.setOnAction(e -> handleMenuClick(parkingBtn, "Parkings"));
        reportsBtn.setOnAction(e -> handleMenuClick(reportsBtn, "Reports"));
        personnelBtn.setOnAction(e -> handleMenuClick(personnelBtn, "Personnel"));
        settingsBtn.setOnAction(e -> handleMenuClick(settingsBtn, "Settings"));
        logoutBtn.setOnAction(e -> handleLogout());
    }

    public void setParentController(AdminController controller) {
        this.parentController = controller;
    }

    private void handleMenuClick(Button clickedButton, String viewName) {
        setActiveButton(clickedButton);

        if (parentController != null) {
            parentController.onMenuItemSelected(viewName);
        } else {
            System.err.println("Parent controller is null!");
        }
    }

    private void handleLogout() {
        System.out.println("Logging out...");
        // TODO: Implement logout
    }

    private void setActiveButton(Button button) {
        if (currentButton != null) {
            currentButton.getStyleClass().remove("current");
        }
        button.getStyleClass().add("current");
        currentButton = button;
    }
}