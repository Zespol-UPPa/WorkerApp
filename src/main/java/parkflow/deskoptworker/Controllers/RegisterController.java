package parkflow.deskoptworker.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {
    /* Activation window controls */
    @FXML private TextField codeTxtField;
    @FXML private Label errorActLabel;
    @FXML private Button activateBtn;

    /* Registration window controls */
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField peselField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorRegLabel;
    @FXML private Button registerBtn;
    @FXML private Button cancelBtn;

    private boolean isActivationWindow = true;

    @FXML
    public void initialize() {
        if (codeTxtField != null) {
            isActivationWindow = true;
            errorActLabel.setVisible(false);
            errorActLabel.setManaged(false);
        } else if (idField != null) {
            isActivationWindow = false;
            errorRegLabel.setVisible(false);
            errorRegLabel.setManaged(false);
        }
    }

    @FXML
    public void handleActivate() {
        String code = codeTxtField.getText().trim();

        if (code.isEmpty()) {
            showError("Enter code");
            return;
        }

        if (code.equals("wrong")) {
            showError("Wrong code");
            return;
        }

        // Kod prawidłowy - przejdź do rejestracji
        System.out.println("Going to registration");
        try {
            var resourceUrl = getClass().getResource("shared/register.fxml");


            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setTitle("Registration");
            registerStage.setScene(new Scene(root));

            // Zamknij okno activation
            Stage activationStage = (Stage) activateBtn.getScene().getWindow();
            activationStage.close();

            // Pokaż okno rejestracji
            registerStage.show();

            System.out.println("Otworzyłem rejestrację i zamknąłem activation");

        } catch (IOException e) {
            System.err.println("Błąd: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRegister() {
//        String id = idField.getText().trim();
//        String name = nameField.getText().trim();
//        String surname = surnameField.getText().trim();
//        String pesel = peselField.getText().trim();
//        String phone = phoneField.getText().trim();
//        String email = emailField.getText().trim();
//        String password = passwordField.getText();
//        String confirmPassword = confirmPasswordField.getText();
//
//        // Walidacja
//        if (id.isEmpty() || name.isEmpty() || surname.isEmpty() ||
//                pesel.isEmpty() || phone.isEmpty() || email.isEmpty() ||
//                password.isEmpty() || confirmPassword.isEmpty()) {
//            showError("Please fill all fields");
//            return;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            showError("Passwords do not match");
//            return;
//        }
//
//        // TODO: Logika rejestracji - wyślij dane do serwera
//        System.out.println("Registering user: " + name + " " + surname);
//        System.out.println("ID: " + id + ", PESEL: " + pesel);

        // Zawsze przechodzimy do logowania
        goToLogin();
    }
    private void goToLogin()
    {
        try {
            var resourceUrl = getClass().getResource("shared/login.fxml");

            if (resourceUrl == null) {
                System.err.println("ERROR: Nie można znaleźć login.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));

            // Zamknij okno rejestracji
            Stage registerStage = (Stage) registerBtn.getScene().getWindow();
            registerStage.close();

            // Pokaż okno logowania
            loginStage.show();

            System.out.println("Przeszedłem do logowania i zamknąłem rejestrację");

        } catch (IOException e) {
            System.err.println("Błąd: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Stage getCurrentStage() {
        if (isActivationWindow && activateBtn != null) {
            return (Stage) activateBtn.getScene().getWindow();
        } else if (!isActivationWindow && registerBtn != null) {
            return (Stage) registerBtn.getScene().getWindow();
        }
        return null;
    }

    private void showError(String message) {
        if (isActivationWindow && errorActLabel != null) {
            errorActLabel.setText(message);
            errorActLabel.setVisible(true);
            errorActLabel.setManaged(true);
        } else if (!isActivationWindow && errorRegLabel != null) {
            errorRegLabel.setText(message);
            errorRegLabel.setVisible(true);
            errorRegLabel.setManaged(true);
        }
    }

    @FXML
    public void handleCancel() {
        System.out.println("Canceling, going back to login...");

        goToLogin();
    }
}