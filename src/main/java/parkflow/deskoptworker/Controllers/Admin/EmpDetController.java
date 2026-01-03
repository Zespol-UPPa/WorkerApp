package parkflow.deskoptworker.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Setter;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;

public class EmpDetController {
    @FXML
    public Label idLabel;
    @FXML
    public Label firstNameLabel;
    @FXML
    public Label lastNameLabel;
    @FXML
    public Label peselLabel;
    @FXML
    public Label emailLabel;
    @FXML
    public Label phoneLabel;
    @FXML
    public Label roleLabel;
    @FXML
    public Label statusLabel;
    @FXML
    public Button closeBtn;

    @Setter
    User employee;

    public void updateView(){
        if(employee != null){
        idLabel.setText(String.valueOf(employee.getId()));
        firstNameLabel.setText(employee.getFirstName());
        lastNameLabel.setText(employee.getLastName());
        peselLabel.setText(String.valueOf(employee.getPesel()));
        emailLabel.setText(employee.getEmail());
        phoneLabel.setText(employee.getPhoneNumber());
            // Ustawienie roli z kolorkiem
            updateRoleLabel();

            // Ustawienie statusu z kolorkiem
            updateStatusLabel();
        }
        else{
            System.err.println("Błąd wczytywania informacji o pracowniku.");
        }
    }

    private void updateRoleLabel() {
        // Czyszczenie poprzednich klas CSS
        roleLabel.getStyleClass().removeAll("coloredLabels", "blue");

        if(employee.getRole() == UserRole.ADMIN) {
            roleLabel.setText("Admin");
            roleLabel.getStyleClass().addAll("coloredLabels", "blue");
        } else {
            roleLabel.setText("Worker");
            roleLabel.getStyleClass().add("coloredLabels");
        }
    }

    private void updateStatusLabel() {
        // Czyszczenie poprzednich klas CSS
        statusLabel.getStyleClass().removeAll("coloredLabels", "green", "gray");

        if(employee.isActive()) {
            statusLabel.setText("Active");
            statusLabel.getStyleClass().addAll("coloredLabels", "green");
        } else {
            statusLabel.setText("Inactive");
            statusLabel.getStyleClass().addAll("coloredLabels", "gray");
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}

