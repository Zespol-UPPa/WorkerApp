package parkflow.deskoptworker.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class LogoutController {
    public Button cancelBtn;
    public Button logoutBtn;


    @FXML
    private void handleCancel() {

        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
        System.out.println("Cancelled");

    }
}
