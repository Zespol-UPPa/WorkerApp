package parkflow.deskoptworker.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;

public class AddEmpController {

    @FXML
    public Button closeBtn;
    @FXML
    public Button saveBtn;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField peselField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private ChoiceBox<UserRole> roleChoiceBox;

    @FXML
    private Label errorLabel;

    private User savedEmployee = null; // Nowy użytkownik
    private static int nextId = 10003;


    @FXML
    public void initialize() {
        roleChoiceBox.getItems().addAll(UserRole.WORKER, UserRole.ADMIN);
        roleChoiceBox.setValue(UserRole.WORKER);

        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
        setupValidation();
    }
    private void setupValidation() {
        // Imię - tylko litery (i spacje, myślniki dla dwuczłonowych imion)
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\s-]*")) {
                firstNameField.setText(oldValue);
            }
        });

        // Nazwisko - tylko litery (i spacje, myślniki)
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\s-]*")) {
                lastNameField.setText(oldValue);
            }
        });

        // PESEL - tylko cyfry, max 11
        peselField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                peselField.setText(oldValue);
            }
            if (newValue.length() > 11) {
                peselField.setText(oldValue);
            }
        });

        // Telefon - tylko + i cyfry
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[+\\d]*")) {
                phoneField.setText(oldValue);
            }
        });

        // Email - podstawowa walidacja (znak @ wymagany)
        // Pełna walidacja przy zapisie
    }

    private boolean validateForm() {
        // Wyczyść poprzednie błędy
        clearAllErrors();

        boolean isValid = true;

        // Sprawdzenie czy pola nie są puste
        if (firstNameField.getText().trim().isEmpty()) {
            setFieldError(firstNameField, true);
            isValid = false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            setFieldError(lastNameField, true);
            isValid = false;
        }

        if (peselField.getText().trim().isEmpty()) {
            setFieldError(peselField, true);
            isValid = false;
        }

        if (emailField.getText().trim().isEmpty()) {
            setFieldError(emailField, true);
            isValid = false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            setFieldError(phoneField, true);
            isValid = false;
        }

        // Walidacja PESEL (dokładnie 11 cyfr)
        if (!peselField.getText().isEmpty() && !peselField.getText().matches("\\d{11}")) {
            setFieldError(peselField, true); // <-- TUTAJ BYŁ BŁĄD! Było firstNameField
            isValid = false;
        }

        // Walidacja email (podstawowa)
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!emailField.getText().isEmpty() && !emailField.getText().matches(emailRegex)) {
            setFieldError(emailField, true);
            isValid = false;
        }

        // Walidacja telefonu (+ i minimum 9 cyfr)
        if (!phoneField.getText().isEmpty() && !phoneField.getText().matches("\\+?\\d{9,}")) {
            setFieldError(phoneField, true);
            isValid = false;
        }

        return isValid;
    }

    private void setFieldError(TextField field, boolean hasError) {
        if (hasError) {
            if (!field.getStyleClass().contains("error-field")) {
                field.getStyleClass().add("error-field");
            }
        } else {
            field.getStyleClass().remove("error-field");
        }
    }

    private void clearAllErrors() {
        setFieldError(firstNameField, false);
        setFieldError(lastNameField, false);
        setFieldError(peselField, false);
        setFieldError(emailField, false);
        setFieldError(phoneField, false);
    }

    public User getSavedEmployee() {
        return savedEmployee;
    }

    @FXML
    private void handleSave() {
        if (validateForm()) {
            // Stwórz nowego użytkownika
            savedEmployee = new User(
                    nextId++, // Tymczasowe ID (później z bazy danych)
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim(),
                    peselField.getText().trim(),
                    roleChoiceBox.getValue(),
                    true // Domyślnie aktywny
            );

            System.out.println("Zapisano nowego użytkownika: " + savedEmployee.getFullName());

            // Zamknij okno
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            stage.close();

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
