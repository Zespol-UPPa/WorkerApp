package parkflow.deskoptworker.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;
import parkflow.deskoptworker.utils.AlertHelper;
import parkflow.deskoptworker.utils.FieldValidator;

public class AddEmpController {

    @FXML public Button closeBtn;
    @FXML public Button saveBtn;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField peselField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ChoiceBox<UserRole> roleChoiceBox;
    @FXML private Label errorLabel;

    @Getter
    private User savedEmployee = null;
    private static int nextId = 10003;

    @FXML
    public void initialize() {
        roleChoiceBox.getItems().addAll(UserRole.WORKER, UserRole.ADMIN);
        roleChoiceBox.setValue(UserRole.WORKER);

        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }

        setupInputFilters();
    }

    /**
     * Setup real-time input filters using FieldValidator
     */
    private void setupInputFilters() {
        FieldValidator.addNameFilter(firstNameField);
        FieldValidator.addNameFilter(lastNameField);
        FieldValidator.addPeselFilter(peselField);
        FieldValidator.addPhoneFilter(phoneField);
    }

    /**
     * Sprawdza wszystkie fields, zwraca true tylko jesli wszystkie sa poprawne
     */
    private boolean validateForm() {

        FieldValidator.clearErrors(firstNameField, lastNameField, peselField, emailField, phoneField);

        boolean isValid = FieldValidator.validateName(firstNameField);

        if (!FieldValidator.validateName(lastNameField)) isValid = false;
        if (!FieldValidator.validatePesel(peselField)) isValid = false;
        if (!FieldValidator.validateEmail(emailField)) isValid = false;
        if (!FieldValidator.validatePhone(phoneField)) isValid = false;

        return isValid;
    }

    @FXML
    private void handleSave() {
        if (validateForm()) {
            savedEmployee = new User(
                    nextId++,
                    FieldValidator.getTrimmedText(firstNameField),
                    FieldValidator.getTrimmedText(lastNameField),
                    FieldValidator.getTrimmedText(phoneField),
                    FieldValidator.getTrimmedText(emailField),
                    FieldValidator.getTrimmedText(peselField),
                    roleChoiceBox.getValue(),
                    true
            );

            System.out.println("Zapisano nowego użytkownika: " + savedEmployee.getFullName());

            Stage stage = (Stage) saveBtn.getScene().getWindow();
            stage.close();
            AlertHelper.showSuccess("Success", "Employee added successfully!");

        } else {
            if (errorLabel != null) {
                errorLabel.setText("Please fill all fields correctly.");
                errorLabel.setVisible(true);
            }
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}