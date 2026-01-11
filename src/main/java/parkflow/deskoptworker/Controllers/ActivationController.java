package parkflow.deskoptworker.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.api.ActivationService;
import parkflow.deskoptworker.dto.ActivationInfoResponse;
import parkflow.deskoptworker.dto.ActivationRequest;
import parkflow.deskoptworker.utils.AlertHelper;
import parkflow.deskoptworker.utils.FieldValidator;

public class ActivationController {

    // Step 1: Code Entry
    @FXML private VBox codeEntryPane;
    @FXML private TextField codeTextField;
    @FXML private Label codeErrorLabel;
    @FXML private Button verifyCodeButton;

    // Step 2: Data Entry & Password Setup
    @FXML private ScrollPane dataEntryPane;
    @FXML private TextField accountIdField;
    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField peselField;
    @FXML private TextField roleField;
    @FXML private VBox parkingPane;
    @FXML private TextField parkingField;
    @FXML private VBox companyPane;
    @FXML private TextField companyField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label dataErrorLabel;
    @FXML private Button backButton;
    @FXML private Button activateButton;

    private ViewFactory viewFactory;
    private final ActivationService activationService = new ActivationService();

    private String activationCode;
    private Long accountId;
    private String role;

    @FXML
    public void initialize() {
        viewFactory = new ViewFactory();
        hideError(codeErrorLabel);
        hideError(dataErrorLabel);

        // Setup input filters
        FieldValidator.addNameFilter(firstNameField);
        FieldValidator.addNameFilter(lastNameField);
        FieldValidator.addPhoneFilter(phoneField);
        FieldValidator.addPeselFilter(peselField);
    }

    @FXML
    private void handleVerifyCode() {
        hideError(codeErrorLabel);

        String code = codeTextField.getText().trim();

        if (code.isEmpty()) {
            showError(codeErrorLabel, "Please enter activation code");
            return;
        }

        verifyCodeButton.setDisable(true);

        try {
            ActivationInfoResponse info = activationService.getActivationInfo(code);

            if (info == null) {
                // Alert already shown by ApiClient
                verifyCodeButton.setDisable(false);
                return;
            }

            this.activationCode = code;
            this.accountId = info.getAccountId();
            this.role = info.getRole();

            // Populate fields with data from backend
            accountIdField.setText(String.valueOf(info.getAccountId()));
            emailField.setText(info.getEmail());
            firstNameField.setText(info.getFirstName() != null ? info.getFirstName() : "");
            lastNameField.setText(info.getLastName() != null ? info.getLastName() : "");
            phoneField.setText(info.getPhoneNumber() != null ? info.getPhoneNumber() : "");
            peselField.setText(info.getPeselNumber() != null ? info.getPeselNumber() : "");
            roleField.setText(info.getRole());

            // Show parking only for Worker
            if ("Worker".equalsIgnoreCase(info.getRole())) {
                if (info.getParkingName() != null) {
                    parkingField.setText(info.getParkingName());
                }
                parkingPane.setVisible(true);
                parkingPane.setManaged(true);
                companyPane.setVisible(false);
                companyPane.setManaged(false);
            }
            // Show company only for Admin
            else if ("Admin".equalsIgnoreCase(info.getRole())) {
                if (info.getCompanyName() != null) {
                    companyField.setText(info.getCompanyName());
                }
                companyPane.setVisible(true);
                companyPane.setManaged(true);
                parkingPane.setVisible(false);
                parkingPane.setManaged(false);
            }

            // Switch to data entry view
            codeEntryPane.setVisible(false);
            codeEntryPane.setManaged(false);
            dataEntryPane.setVisible(true);
            dataEntryPane.setManaged(true);

            firstNameField.requestFocus();

        } catch (Exception e) {
            // Alert already shown by ApiClient
            e.printStackTrace();
        } finally {
            verifyCodeButton.setDisable(false);
        }
    }

    @FXML
    private void handleActivate() {
        hideError(dataErrorLabel);

        // Clear previous error styling
        FieldValidator.clearErrors(firstNameField, lastNameField, phoneField, peselField,
                passwordField, confirmPasswordField);

        // Get data from fields
        String firstName = FieldValidator.getTrimmedText(firstNameField);
        String lastName = FieldValidator.getTrimmedText(lastNameField);
        String phone = FieldValidator.getTrimmedText(phoneField);
        String pesel = FieldValidator.getTrimmedText(peselField);
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation with styling
        boolean valid = true;

        if (!FieldValidator.validateName(firstNameField)) {
            showError(dataErrorLabel, "First name is required");
            valid = false;
        }

        if (!FieldValidator.validateName(lastNameField)) {
            showError(dataErrorLabel, "Last name is required");
            valid = false;
        }

        if (!FieldValidator.validatePhone(phoneField)) {
            showError(dataErrorLabel, "Invalid phone number (minimum 9 digits)");
            valid = false;
        }

        if (!FieldValidator.validatePesel(peselField)) {
            showError(dataErrorLabel, "PESEL must be exactly 11 digits");
            valid = false;
        }

        if (!FieldValidator.hasMinLength(passwordField, 8)) {
            FieldValidator.setFieldError(passwordField, true);
            showError(dataErrorLabel, "Password must be at least 8 characters");
            valid = false;
        }

        if (!FieldValidator.passwordsMatch(passwordField, confirmPasswordField)) {
            FieldValidator.setFieldError(confirmPasswordField, true);
            showError(dataErrorLabel, "Passwords do not match");
            valid = false;
        }

        if (!valid) {
            return;
        }

        activateButton.setDisable(true);

        try {
            ActivationRequest request = new ActivationRequest();
            request.setCode(activationCode);
            request.setFirstName(firstName);
            request.setLastName(lastName);
            request.setPhoneNumber(phone);
            request.setPeselNumber(pesel);
            request.setPassword(password);
            request.setConfirmPassword(confirmPassword);

            boolean success = activationService.activate(request, role);

            if (!success) {
                // Alert already shown by ApiClient
                activateButton.setDisable(false);
                return;
            }

            // Success - show custom alert with login credentials
            AlertHelper.showSuccess(
                    "Activation Successful",
                    "Your account has been activated!\n\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "LOGIN CREDENTIALS:\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                            "Your Login ID: " + accountId + "\n" +
                            "Password: (the password you just set)\n\n" +
                            "IMPORTANT: Save your Login ID!\n" +
                            "You will need it every time you log in."
            );

            handleBackToLogin();

        } catch (Exception e) {
            // Alert already shown by ApiClient
            e.printStackTrace();
            activateButton.setDisable(false);
        }
    }

    @FXML
    private void handleBack() {
        dataEntryPane.setVisible(false);
        dataEntryPane.setManaged(false);
        codeEntryPane.setVisible(true);
        codeEntryPane.setManaged(true);

        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        peselField.clear();
        passwordField.clear();
        confirmPasswordField.clear();

        FieldValidator.clearErrors(firstNameField, lastNameField, phoneField, peselField,
                passwordField, confirmPasswordField);

        hideError(dataErrorLabel);
    }

    @FXML
    private void handleBackToLogin() {
        Stage stage = (Stage) codeTextField.getScene().getWindow();
        stage.close();
        viewFactory.showLoginWindow();
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void hideError(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }
}