package parkflow.deskoptworker.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.models.User;

public class DeactivateController {
    public Button cancelBtn;
    public Button deactivateBtn;
    public Text employeeNameText;

    private User employee;
    @Getter
    private boolean confirmed = false;

    private ViewFactory viewFactory;

    public void setEmployee(User employee) {
        this.employee = employee;
        if (employeeNameText != null) {
            employeeNameText.setText(employee.getFullName());
        }
    }

    @FXML
    private void handleCancel() {
        confirmed = false;
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleDeactivate() {
        confirmed = true;
        Stage stage = (Stage) deactivateBtn.getScene().getWindow();
        stage.close();
    }
}

