package parkflow.deskoptworker.Controllers.sharedPanels;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.api.AccountService;
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

    // API Service
    private final AccountService accountService = new AccountService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // 🔒 SESSION GUARD
        if (SessionManager.getInstance().getCurrentUser() == null) {
            System.out.println("No active session – redirecting to login");
            new ViewFactory().showLoginWindow();
            return;
        }

        setupInputFilters();
        loadUserDataFromAPI(); // Load fresh data from API

        setPersonalInfoEditMode(false);
        setPasswordEditMode(false);

        editBtn.setOnAction(_ -> {
            saveOriginalValues();
            setPersonalInfoEditMode(true);
        });

        saveChangesBtn.setOnAction(_ -> handleSavePersonalInfo());

        cancelInfoBtn.setOnAction(_ -> {
            restoreOriginalValues();
            setPersonalInfoEditMode(false);
        });

        changePassEditBtn.setOnAction(_ -> setPasswordEditMode(true));
        changePassBtn.setOnAction(_ -> handleChangePassword());
        cancelPassBtn.setOnAction(_ -> setPasswordEditMode(false));

        addChangeListeners();
        addPasswordFieldListeners();
    }

    /**
     * Load user data from API (fresh data)
     */
    private void loadUserDataFromAPI() {
        // Show loading indicator (optional)
        setFieldsEnabled(false);

        // Load in background thread to avoid blocking UI
        new Thread(() -> {
            try {
                User freshUser = accountService.getCurrentUserProfile();

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    if (freshUser != null) {
                        // Update SessionManager with fresh data
                        SessionManager.getInstance().setCurrentUser(freshUser);

                        // Update UI fields
                        idTextField.setText(String.valueOf(freshUser.getId()));
                        peselTextField.setText(freshUser.getPesel());
                        firstNameTxtField.setText(freshUser.getFirstName());
                        lastNameTxtField.setText(freshUser.getLastName());
                        phoneTxtField.setText(freshUser.getPhoneNumber());
                        emailTxtField.setText(freshUser.getEmail());

                        saveOriginalValues();
                    } else {
                        showError(infoErrorLabel, "Failed to load profile data");
                    }
                    setFieldsEnabled(true);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError(infoErrorLabel, "Error loading profile: " + e.getMessage());
                    setFieldsEnabled(true);
                });
            }
        }).start();
    }

    private void setFieldsEnabled(boolean enabled) {
        idTextField.setDisable(!enabled);
        peselTextField.setDisable(!enabled);
        firstNameTxtField.setDisable(!enabled);
        lastNameTxtField.setDisable(!enabled);
        phoneTxtField.setDisable(!enabled);
        emailTxtField.setDisable(!enabled);
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
                            !phoneTxtField.getText().equals(originalPhone);
            saveChangesBtn.setDisable(!hasChanges);
        };

        firstNameTxtField.textProperty().addListener((_, _, _) -> checkChanges.run());
        lastNameTxtField.textProperty().addListener((_, _, _) -> checkChanges.run());
        phoneTxtField.textProperty().addListener((_, _, _) -> checkChanges.run());
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

    /**
     * Handle saving personal info via API
     */
    private void handleSavePersonalInfo() {
        FieldValidator.clearErrors(firstNameTxtField, lastNameTxtField, phoneTxtField);
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

        if (isValid) {
            // Disable button during save
            saveChangesBtn.setDisable(true);
            saveChangesBtn.setText("Saving...");

            String firstName = FieldValidator.getTrimmedText(firstNameTxtField);
            String lastName = FieldValidator.getTrimmedText(lastNameTxtField);
            String phone = FieldValidator.getTrimmedText(phoneTxtField);

            // Save in background thread
            new Thread(() -> {
                boolean success = accountService.updatePersonalInfo(firstName, lastName, phone);

                Platform.runLater(() -> {
                    if (success) {
                        // Update SessionManager with new data
                        User user = SessionManager.getInstance().getCurrentUser();
                        if (user != null) {
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setPhoneNumber(phone);
                        }

                        saveOriginalValues();
                        setPersonalInfoEditMode(false);

                        // Optional: Show success message
                        System.out.println("Profile updated successfully");
                    } else {
                        showError(infoErrorLabel, "Failed to save changes. Please try again.");
                    }

                    saveChangesBtn.setDisable(false);
                    saveChangesBtn.setText("Save Changes");
                });
            }).start();
        } else {
            showError(infoErrorLabel, errorMessage.toString().trim());
        }
    }

    /**
     * Handle changing password via API
     */
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

        // Czy nowe hasło się różni od starego
        if (isValid && currentPassTxtField.getText().equals(newPassTxtField.getText())) {
            FieldValidator.setFieldError(newPassTxtField, true);
            isValid = false;
            errorMessage.append("New password must be different from current. ");
        }

        if (isValid) {
            // Disable button during save
            changePassBtn.setDisable(true);
            changePassBtn.setText("Changing...");

            String currentPass = currentPassTxtField.getText();
            String newPass = newPassTxtField.getText();

            // Change password in background thread
            new Thread(() -> {
                boolean success = accountService.changePassword(currentPass, newPass);

                Platform.runLater(() -> {
                    if (success) {
                        // Exit edit mode and clear fields
                        setPasswordEditMode(false);

                        // Optional: Show success message
                        System.out.println("Password changed successfully");
                    } else {
                        showError(passwordErrorLabel, "Failed to change password. Current password may be incorrect.");
                    }

                    changePassBtn.setDisable(false);
                    changePassBtn.setText("Change Password");
                });
            }).start();
        } else {
            showError(passwordErrorLabel, errorMessage.toString().trim());
        }
    }

    private void setPersonalInfoEditMode(boolean editing) {
        // Przełącz edytowalność pól
        firstNameTxtField.setEditable(editing);
        lastNameTxtField.setEditable(editing);
        phoneTxtField.setEditable(editing);
        emailTxtField.setEditable(false); // Email is always read-only

        // Zmień styl pól
        String styleClass = editing ? "text-field_basic" : "text-field_disabled";
        updateFieldStyle(firstNameTxtField, styleClass);
        updateFieldStyle(lastNameTxtField, styleClass);
        updateFieldStyle(phoneTxtField, styleClass);

        // Pokaż/ukryj przyciski
        setNodeVisibility(editBtn, !editing);
        setNodeVisibility(saveChangesBtnBox, editing);

        if (editing) {
            firstNameTxtField.requestFocus();
        }

        FieldValidator.clearErrors(firstNameTxtField, lastNameTxtField, phoneTxtField);
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