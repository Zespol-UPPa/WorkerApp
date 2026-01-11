package parkflow.deskoptworker.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.Setter;
import parkflow.deskoptworker.Views.ViewFactory;

public class AdminMenuController {
    @FXML private Button dashboardBtn;
    @FXML private Button parkingBtn;
    @FXML private Button reportsBtn;
    @FXML private Button personnelBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;

    private Button currentButton;
    @Setter
    private AdminController parentController;

    ViewFactory viewFactory;


    public void initialize() {
        setActiveButton(dashboardBtn);

        dashboardBtn.setOnAction(_ -> handleMenuClick(dashboardBtn, "Dashboard"));
        parkingBtn.setOnAction(_ -> handleMenuClick(parkingBtn, "Parkings"));
        reportsBtn.setOnAction(_ -> handleMenuClick(reportsBtn, "Reports"));
        personnelBtn.setOnAction(_ -> handleMenuClick(personnelBtn, "Personnel"));
        settingsBtn.setOnAction(_ -> handleMenuClick(settingsBtn, "Settings"));
        logoutBtn.setOnAction(_ -> handleLogout());

        viewFactory = new ViewFactory();
    }

    private void handleMenuClick(Button clickedButton, String viewName) {
        setActiveButton(clickedButton);

        if (parentController != null) {
            parentController.onMenuItemSelected(viewName);
        } else {
            System.err.println("Parent controller is null!");
        }
    }

    @FXML
    private void handleLogout() {
        // Show logout confirmation modal

        if (viewFactory != null) {
            boolean confirmed = viewFactory.showLogoutModal();

            if (confirmed && parentController != null) {
                parentController.handleLogout();
            } else {
                System.out.println("Logout cancelled by user");
            }
        } else {
            System.err.println("ViewFactory is null!");
        }
    }

    private void setActiveButton(Button button) {
        if (currentButton != null) {
            currentButton.getStyleClass().remove("current");
        }
        button.getStyleClass().add("current");
        currentButton = button;
    }
}