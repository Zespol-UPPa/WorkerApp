package parkflow.deskoptworker.utils;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class FieldValidator {

    // ==================== REGEX PATTERNS ====================

    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\s-]*");
    private static final Pattern DIGITS_ONLY_PATTERN = Pattern.compile("\\d*");
    private static final Pattern PHONE_INPUT_PATTERN = Pattern.compile("\\+?\\d*");

    // Walidacja końcowa
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PESEL_PATTERN = Pattern.compile("\\d{11}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+?\\d{9,}");

    // ==================== INPUT FILTERS (TextFormatter) ====================

    /**
     * Tworzy UnaryOperator dla TextFormatter z podanym wzorem
     */
    private static UnaryOperator<TextFormatter.Change> createFilter(Pattern pattern) {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || pattern.matcher(newText).matches()) {
                return change;
            }
            return null;
        };
    }

    /**
     * Tworzy UnaryOperator dla TextFormatter z podanym wzorem i max długością
     */
    private static UnaryOperator<TextFormatter.Change> createFilter(Pattern pattern, int maxLength) {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || (pattern.matcher(newText).matches() && newText.length() <= maxLength)) {
                return change;
            }
            return null;
        };
    }

    /**
     * Ustawia TextFormatter na TextField
     */
    public static void addInputFilter(TextField field, Pattern pattern) {
        field.setTextFormatter(new TextFormatter<>(createFilter(pattern)));
    }

    public static void addInputFilter(TextField field, Pattern pattern, int maxLength) {
        field.setTextFormatter(new TextFormatter<>(createFilter(pattern, maxLength)));
    }

    public static void addNameFilter(TextField field) {
        field.setTextFormatter(new TextFormatter<>(createFilter(NAME_PATTERN)));
    }

    public static void addPeselFilter(TextField field) {
        field.setTextFormatter(new TextFormatter<>(createFilter(DIGITS_ONLY_PATTERN, 11)));
    }

    public static void addPhoneFilter(TextField field) {
        field.setTextFormatter(new TextFormatter<>(createFilter(PHONE_INPUT_PATTERN)));
    }

    public static void addDigitsOnlyFilter(TextField field) {
        field.setTextFormatter(new TextFormatter<>(createFilter(DIGITS_ONLY_PATTERN)));
    }

    public static void addDigitsOnlyFilter(TextField field, int maxLength) {
        field.setTextFormatter(new TextFormatter<>(createFilter(DIGITS_ONLY_PATTERN, maxLength)));
    }

    // ==================== VALIDATORS (Check validity) ====================

    public static boolean isNotEmpty(TextInputControl field) {
        return field.getText() != null && !field.getText().trim().isEmpty();
    }

    public static boolean isEmpty(TextInputControl field) {
        return !isNotEmpty(field);
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidEmail(TextInputControl field) {
        return isNotEmpty(field) && isValidEmail(field.getText().trim());
    }

    public static boolean isValidPesel(String pesel) {
        return pesel != null && PESEL_PATTERN.matcher(pesel).matches();
    }

    public static boolean isValidPesel(TextInputControl field) {
        return isNotEmpty(field) && isValidPesel(field.getText().trim());
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidPhone(TextInputControl field) {
        return isNotEmpty(field) && isValidPhone(field.getText().trim());
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidName(TextInputControl field) {
        return isNotEmpty(field) && isValidName(field.getText().trim());
    }

    // ==================== PASSWORD VALIDATORS ====================

    public static boolean hasMinLength(TextInputControl field, int minLength) {
        return field.getText() != null && field.getText().length() >= minLength;
    }

    public static boolean passwordsMatch(TextInputControl password, TextInputControl confirmPassword) {
        if (password.getText() == null || confirmPassword.getText() == null) {
            return false;
        }
        return password.getText().equals(confirmPassword.getText());
    }

    // ==================== ERROR STYLING ====================

    private static final String ERROR_STYLE_CLASS = "error-field";

    public static void setFieldError(TextInputControl field, boolean hasError) {
        if (hasError) {
            if (!field.getStyleClass().contains(ERROR_STYLE_CLASS)) {
                field.getStyleClass().add(ERROR_STYLE_CLASS);
            }
        } else {
            field.getStyleClass().remove(ERROR_STYLE_CLASS);
        }
    }

    public static boolean validateAndStyle(TextInputControl field, Predicate<TextInputControl> validator) {
        boolean isValid = validator.test(field);
        setFieldError(field, !isValid);
        return isValid;
    }

    public static void clearErrors(TextInputControl... fields) {
        for (TextInputControl field : fields) {
            setFieldError(field, false);
        }
    }

    // ==================== CONVENIENCE METHODS ====================

    public static boolean validateRequired(TextInputControl field) {
        return validateAndStyle(field, FieldValidator::isNotEmpty);
    }

    public static boolean validateEmail(TextInputControl field) {
        return validateAndStyle(field, FieldValidator::isValidEmail);
    }

    public static boolean validatePesel(TextInputControl field) {
        return validateAndStyle(field, FieldValidator::isValidPesel);
    }

    public static boolean validatePhone(TextInputControl field) {
        return validateAndStyle(field, FieldValidator::isValidPhone);
    }

    public static boolean validateName(TextInputControl field) {
        return validateAndStyle(field, FieldValidator::isValidName);
    }

    public static String getTrimmedText(TextInputControl field) {
        return field.getText() != null ? field.getText().trim() : "";
    }
}