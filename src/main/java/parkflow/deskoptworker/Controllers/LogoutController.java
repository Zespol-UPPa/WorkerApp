package parkflow.deskoptworker.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.Getter;

public class LogoutController {

    @FXML public Button cancelBtn;
    @FXML public Button logoutBtn;

    @Getter
    private boolean confirmed = false;

    @FXML
    public void initialize() {
        // Setup button handlers
        if (cancelBtn != null) {
            cancelBtn.setOnAction(event -> handleCancel());
        }
        if (logoutBtn != null) {
            logoutBtn.setOnAction(event -> handleLogout());
        }
    }

    @FXML
    private void handleCancel() {
        confirmed = false;
        closeWindow();
        System.out.println("Logout cancelled");
    }

    @FXML
    private void handleLogout() {
        confirmed = true;
        closeWindow();
        System.out.println("Logout confirmed");
    }

    private void closeWindow() {
        Stage stage = getStage();
        if (stage != null) {
            stage.close();
        }
    }

    private Stage getStage() {
        if (cancelBtn != null && cancelBtn.getScene() != null) {
            return (Stage) cancelBtn.getScene().getWindow();
        }
        if (logoutBtn != null && logoutBtn.getScene() != null) {
            return (Stage) logoutBtn.getScene().getWindow();
        }
        return null;
    }
}