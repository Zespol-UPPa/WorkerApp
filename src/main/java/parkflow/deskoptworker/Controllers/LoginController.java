package parkflow.deskoptworker.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.api.AuthService;
import parkflow.deskoptworker.utils.SessionManager;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    @FXML
    private TextField idTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label activationLabel;
    @FXML
    private ImageView passIcon;
    @FXML
    private Label errorLabel;

    private boolean passwordVisible = false;


    ViewFactory viewFactory;
    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        viewFactory = new ViewFactory();
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        //polaczenie TextField z PasswordField
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (passwordField.isVisible() && passwordTextField != null) {
                passwordTextField.setText(newVal);
            }
        });

        if (passwordTextField != null) {
            passwordTextField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (passwordTextField.isVisible()) {
                    passwordField.setText(newVal);
                }
            });
        }
    }

    @FXML
    private void handleLogin() {

        hideError();

        String id = idTextField.getText().trim();
        String password = passwordVisible
                ? passwordTextField.getText()
                : passwordField.getText();

        if (id.isEmpty() || password.isEmpty()) {
            showError("Please fill all fields");
            return;
        }

        try {
            boolean success = authService.login(id, password);

            if (!success) {
                showError("Invalid ID or password");
                return;
            }

            SessionManager session = SessionManager.getInstance();


            if (session.isAdmin()) {
                viewFactory.showAdminWindow();
            } else {
                viewFactory.showWorkerWindow();
            }

            // zamknij login
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();

        } catch (RuntimeException e) {

            if (e.getMessage().contains("Forbidden")) {
                showError("Account not activated");
                return;
            }

            showError("Login failed. Try again.");
            e.printStackTrace();
        }
    }

    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            // Pokaż hasło
            if (passwordTextField != null) {
                passwordTextField.setText(passwordField.getText());
                passwordTextField.setVisible(true);
                passwordTextField.setManaged(true);
            }
            passwordField.setVisible(false);
            passwordField.setManaged(false);

            // Zmień ikonę na "hide"
            passIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/parkflow/deskoptworker/images/hide.png"))));
            System.out.println("Password visible");
        } else {
            // Ukryj hasło
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            if (passwordTextField != null) {
                passwordTextField.setVisible(false);
                passwordTextField.setManaged(false);
            }

            // Zmień ikonę na "see"
            passIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/parkflow/deskoptworker/images/see.png"))));
            System.out.println("Password hidden");
        }
    }

    @FXML
    private void handleActivation() {
        System.out.println("Go to activation...");

        // Zamknij okno logowania
        Stage loginStage = (Stage) activationLabel.getScene().getWindow();
        loginStage.close();

        // Otwórz okno aktywacji przez ViewFactory
        viewFactory.showActivationWindow();
    }



    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        System.out.println("Błąd: " + message);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }




}