package parkflow.deskoptworker.Controllers.sharedPanels;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.utils.FieldValidator;
import parkflow.deskoptworker.utils.SessionManager;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    // Personal Info
    @FXML private Button editBtn, saveChangesBtn, cancelInfoBtn;
    @FXML private TextField idTextField, peselTextField;
    @FXML private TextField firstNameTxtField, lastNameTxtField, phoneTxtField, emailTxtField;
    @FXML private Label infoErrorLabel;
    @FXML private HBox saveChangesBtnBox;

    // Password
    @FXML private Button changePassEditBtn;
    @FXML private Button changePassBtn, cancelPassBtn;
    @FXML private VBox passwordHiddenVBox, changePasswordVbox;
    @FXML private PasswordField currentPassTxtField, newPassTxtField, confirmNewPassTxtField;
    @FXML private Label passwordErrorLabel;
    @FXML private HBox changePassBtnBox;

    // Przechowuj oryginalne wartości do resetu przy Cancel
    private String originalFirstName, originalLastName, originalPhone, originalEmail;

    private static final int MIN_PASSWORD_LENGTH = 8;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup input filters FIRST - przed załadowaniem danych
        setupInputFilters();

        // Załaduj dane użytkownika
        loadUserData();

        // Ustaw tryb view na start
        setPersonalInfoEditMode(false);
        setPasswordEditMode(false);

        // Event handlery - Personal Info
        editBtn.setOnAction(_ -> {
            saveOriginalValues();
            setPersonalInfoEditMode(true);
        });

        saveChangesBtn.setOnAction(_ -> handleSavePersonalInfo());

        cancelInfoBtn.setOnAction(_ -> {
            restoreOriginalValues();
            setPersonalInfoEditMode(false);
        });

        // Event handlery - Password
        changePassEditBtn.setOnAction(_ -> setPasswordEditMode(true));
        changePassBtn.setOnAction(_ -> handleChangePassword());
        cancelPassBtn.setOnAction(_ -> setPasswordEditMode(false));

        // Listenery do włączania przycisku Save changes gdy coś się zmieni
        addChangeListeners();

        // Listener do włączania przycisku Change password gdy wszystkie pola wypełnione
        addPasswordFieldListeners();
    }

    private void loadUserData() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            idTextField.setText(String.valueOf(user.getId()));
            peselTextField.setText(user.getPesel());
            firstNameTxtField.setText(user.getFirstName());
            lastNameTxtField.setText(user.getLastName());
            phoneTxtField.setText(user.getPhoneNumber());
            emailTxtField.setText(user.getEmail());
        }
    }

    private void setupInputFilters() {
        FieldValidator.addNameFilter(firstNameTxtField);
        FieldValidator.addNameFilter(lastNameTxtField);
        FieldValidator.addPhoneFilter(phoneTxtField);

    }

    private void saveOriginalValues() {
        originalFirstName = firstNameTxtField.getText();
        originalLastName = lastNameTxtField.getText();
        originalPhone = phoneTxtField.getText();
        originalEmail = emailTxtField.getText();
    }

    private void restoreOriginalValues() {
        firstNameTxtField.setText(originalFirstName);
        lastNameTxtField.setText(originalLastName);
        phoneTxtField.setText(originalPhone);
        emailTxtField.setText(originalEmail);
    }

    private void addChangeListeners() {
        Runnable checkChanges = () -> {
            boolean hasChanges =
                    !firstNameTxtField.getText().equals(originalFirstName) ||
                            !lastNameTxtField.getText().equals(originalLastName) ||
                            !phoneTxtField.getText().equals(originalPhone) ||
                            !emailTxtField.getText().equals(originalEmail);
            saveChangesBtn.setDisable(!hasChanges);
        };

        firstNameTxtField.textProperty().addListener((_, _, _) -> checkChanges.run());
        lastNameTxtField.textProperty().addListener((_, _, _) -> checkChanges.run());
        phoneTxtField.textProperty().addListener((_, _, _) -> checkChanges.run());
        emailTxtField.textProperty().addListener((_, _, _) -> checkChanges.run());
    }

    private void addPasswordFieldListeners() {
        Runnable checkPasswordFields = () -> {
            boolean allFilled =
                    FieldValidator.isNotEmpty(currentPassTxtField) &&
                            FieldValidator.isNotEmpty(newPassTxtField) &&
                            FieldValidator.isNotEmpty(confirmNewPassTxtField);
            changePassBtn.setDisable(!allFilled);
        };

        currentPassTxtField.textProperty().addListener((_, _, _) -> checkPasswordFields.run());
        newPassTxtField.textProperty().addListener((_, _, _) -> checkPasswordFields.run());
        confirmNewPassTxtField.textProperty().addListener((_, _, _) -> checkPasswordFields.run());
    }


    private void handleSavePersonalInfo() {
        FieldValidator.clearErrors(firstNameTxtField, lastNameTxtField, phoneTxtField, emailTxtField);
        hideError(infoErrorLabel);

        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        if (!FieldValidator.validateName(firstNameTxtField)) {
            isValid = false;
            errorMessage.append("Invalid first name. ");
        }

        if (!FieldValidator.validateName(lastNameTxtField)) {
            isValid = false;
            errorMessage.append("Invalid last name. ");
        }

        if (!FieldValidator.validatePhone(phoneTxtField)) {
            isValid = false;
            errorMessage.append("Invalid phone number (min 9 digits). ");
        }

        if (!FieldValidator.validateEmail(emailTxtField)) {
            isValid = false;
            errorMessage.append("Invalid email format. ");
        }

        if (isValid) {
            // Aktualizuj User w SessionManager
            User user = SessionManager.getCurrentUser();
            if (user != null) {
                user.setFirstName(FieldValidator.getTrimmedText(firstNameTxtField));
                user.setLastName(FieldValidator.getTrimmedText(lastNameTxtField));
                user.setPhoneNumber(FieldValidator.getTrimmedText(phoneTxtField));
                user.setEmail(FieldValidator.getTrimmedText(emailTxtField));
            }

            // TODO: Save to database/API

            saveOriginalValues();
            setPersonalInfoEditMode(false);
        } else {
            showError(infoErrorLabel, errorMessage.toString().trim());
        }
    }


    private void handleChangePassword() {
        // Clear previous errors
        FieldValidator.clearErrors(currentPassTxtField, newPassTxtField, confirmNewPassTxtField);
        hideError(passwordErrorLabel);

        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        if (FieldValidator.isEmpty(currentPassTxtField)) {
            FieldValidator.setFieldError(currentPassTxtField, true);
            isValid = false;
            errorMessage.append("Current password is required. ");
        }

        if (!FieldValidator.hasMinLength(newPassTxtField, MIN_PASSWORD_LENGTH)) {
            FieldValidator.setFieldError(newPassTxtField, true);
            isValid = false;
            errorMessage.append("Password must be at least ").append(MIN_PASSWORD_LENGTH).append(" characters. ");
        }


        if (!FieldValidator.passwordsMatch(newPassTxtField, confirmNewPassTxtField)) {
            FieldValidator.setFieldError(confirmNewPassTxtField, true);
            isValid = false;
            errorMessage.append("Passwords do not match. ");
        }

        // Czy nowe haslo sie rozni od starego
        if (isValid && currentPassTxtField.getText().equals(newPassTxtField.getText())) {
            FieldValidator.setFieldError(newPassTxtField, true);
            isValid = false;
            errorMessage.append("New password must be different from current. ");
        }

        if (isValid) {
            // TODO: Verify current password with API and save new password
            System.out.println("Password change requested");
            System.out.println("  Current: " + currentPassTxtField.getText());
            System.out.println("  New: " + newPassTxtField.getText());

            // Exit edit mode
            setPasswordEditMode(false);

            // TODO: Show success message
        } else {
            showError(passwordErrorLabel, errorMessage.toString().trim());
        }
    }

    private void setPersonalInfoEditMode(boolean editing) {
        // Przełącz edytowalność pól
        firstNameTxtField.setEditable(editing);
        lastNameTxtField.setEditable(editing);
        phoneTxtField.setEditable(editing);
        emailTxtField.setEditable(editing);

        // Zmień styl pól
        String styleClass = editing ? "text-field_basic" : "text-field_disabled";
        updateFieldStyle(firstNameTxtField, styleClass);
        updateFieldStyle(lastNameTxtField, styleClass);
        updateFieldStyle(phoneTxtField, styleClass);
        updateFieldStyle(emailTxtField, styleClass);

        // Pokaż/ukryj przyciski
        setNodeVisibility(editBtn, !editing);
        setNodeVisibility(saveChangesBtnBox, editing);

        if (editing) {
            firstNameTxtField.requestFocus();
        }

        FieldValidator.clearErrors(firstNameTxtField, lastNameTxtField, phoneTxtField, emailTxtField);
        hideError(infoErrorLabel);
    }

    private void setPasswordEditMode(boolean editing) {
        // Pokaż/ukryj sekcje
        setNodeVisibility(passwordHiddenVBox, !editing);
        setNodeVisibility(changePasswordVbox, editing);

        // Pokaż/ukryj duży przycisk w headerze
        setNodeVisibility(changePassEditBtn, !editing);

        // Pokaż/ukryj przyciski na dole
        setNodeVisibility(changePassBtnBox, editing);

        if (editing) {
            currentPassTxtField.clear();
            newPassTxtField.clear();
            confirmNewPassTxtField.clear();
            changePassBtn.setDisable(true);
        }

        FieldValidator.clearErrors(currentPassTxtField, newPassTxtField, confirmNewPassTxtField);
        hideError(passwordErrorLabel);
    }

    private void setNodeVisibility(javafx.scene.Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    private void updateFieldStyle(TextField field, String newStyle) {
        field.getStyleClass().removeAll("text-field_basic", "text-field_disabled");
        field.getStyleClass().add(newStyle);
    }

    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        setNodeVisibility(errorLabel, true);
    }

    private void hideError(Label errorLabel) {
        setNodeVisibility(errorLabel, false);
    }
}