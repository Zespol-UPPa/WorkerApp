package parkflow.deskoptworker.Controllers.Components;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import parkflow.deskoptworker.models.Section;
import parkflow.deskoptworker.utils.FieldValidator;

public class SectionController {

    @FXML private Label sectionNameLabel;
    @FXML private TextField numberOfSpacesField;  // Changed from numberOfSpotsField
    @FXML private ChoiceBox<Integer> floorChoiceBox;  // Changed from TextField to ChoiceBox
    @FXML private CheckBox isReservableCheckBox;  // Changed from reservableCheckbox
    @FXML private Button deleteButton;

    private int numberOfFloors;

    @Setter
    private Runnable onRemove;

    /**
     * -- GETTER --
     *  Get the section prefix
     */
    @Getter
    private String sectionPrefix = "A";  // Track section prefix

    @FXML
    public void initialize() {
        // Setup field validators
        setupFieldValidators();

        // Setup floor choice box with options 0-9
        floorChoiceBox.setItems(FXCollections.observableArrayList(
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        ));
        floorChoiceBox.setValue(0);  // Default to ground floor

        // Default values
        numberOfSpacesField.setText("50");
        isReservableCheckBox.setSelected(false);

        // Setup delete button handler
        deleteButton.setOnAction(event -> handleRemove());
    }

    public void setNumberOfFloors(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
        initFloorComboBox();
    }

    private void initFloorComboBox() {
        floorChoiceBox.getItems().clear();
        for (int i = 0; i < numberOfFloors; i++) {
            floorChoiceBox.getItems().add(i);
        }
        floorChoiceBox.getSelectionModel().selectFirst();
    }

    /**
     * Setup field validators
     */
    private void setupFieldValidators() {
        // Number of spaces: digits only, max 3 characters (max 100)
        FieldValidator.addDigitsOnlyFilter(numberOfSpacesField, 3);
    }

    /**
     * Set the section prefix (A, B, C, etc.)
     * Updates the label
     */
    public void setSectionPrefix(String prefix) {
        this.sectionPrefix = prefix;
        sectionNameLabel.setText("Section " + prefix);
    }

    /**
     * Handle remove button
     */
    @FXML
    private void handleRemove() {
        if (onRemove != null) {
            onRemove.run();
        }
    }

    /**
     * Get section data from fields
     */
    public Section getSection() {
        try {
            if (sectionPrefix == null || sectionPrefix.isEmpty()) {
                System.err.println("Section prefix is empty");
                return null;
            }

            String numberOfSpacesText = numberOfSpacesField.getText().trim();
            if (numberOfSpacesText.isEmpty()) {
                System.err.println("Number of spaces is empty");
                return null;
            }

            int numberOfSpots = Integer.parseInt(numberOfSpacesText);
            if (numberOfSpots <= 0) {
                System.err.println("Number of spots must be positive");
                return null;
            }
            if (numberOfSpots > 100) {
                System.err.println("Number of spots cannot exceed 100");
                return null;
            }

            Integer floorLevel = floorChoiceBox.getValue();
            if (floorLevel == null) {
                floorLevel = 0;  // Default to ground floor
            }

            boolean reservable = isReservableCheckBox.isSelected();

            return new Section(sectionPrefix, numberOfSpots, floorLevel, reservable);

        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in section fields: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validate section data
     */
    public boolean validate() {
        if (sectionPrefix == null || sectionPrefix.isEmpty()) {
            return false;
        }

        try {
            String numberOfSpacesText = numberOfSpacesField.getText().trim();
            if (numberOfSpacesText.isEmpty()) {
                return false;
            }

            int numberOfSpots = Integer.parseInt(numberOfSpacesText);
            if (numberOfSpots <= 0 || numberOfSpots > 100) {
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

}