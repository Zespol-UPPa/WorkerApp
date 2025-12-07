package parkflow.deskoptworker.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SectionController {
    @FXML private VBox rootContainer;
    @FXML private Label sectionNameLabel;
    @FXML private TextField numberOfSpacesField;
    @FXML private ChoiceBox<Integer> floorChoiceBox;
    @FXML private CheckBox isReservableCheckBox;
    @FXML private Button deleteButton;

    private String sectionLetter;
    private Runnable onDeleteCallback;

    public void initialize() {
        // Walidacja - tylko liczby
        numberOfSpacesField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                numberOfSpacesField.setText(oldVal);
            }
        });
    }

    public void setSectionData(String letter, int maxFloors) {
        this.sectionLetter = letter;
        sectionNameLabel.setText("Section " + letter);

        // Wypełnij ChoiceBox piętrami
        floorChoiceBox.getItems().clear();
        for (int i = 1; i <= maxFloors; i++) {
            floorChoiceBox.getItems().add(i);
        }
        floorChoiceBox.setValue(1); // Domyślnie pierwsze piętro
    }

    public void setOnDelete(Runnable callback) {
        this.onDeleteCallback = callback;
        deleteButton.setOnAction(e -> {
            if (onDeleteCallback != null) {
                onDeleteCallback.run();
            }
        });
    }

    // Gettery do odczytu danych
    public String getSectionLetter() {
        return sectionLetter;
    }

    public int getNumberOfSpaces() {
        try {
            return Integer.parseInt(numberOfSpacesField.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getFloor() {
        return floorChoiceBox.getValue() != null ? floorChoiceBox.getValue() : 1;
    }

    public boolean isReservable() {
        return isReservableCheckBox.isSelected();
    }

    public boolean isValid() {
        return !numberOfSpacesField.getText().isEmpty() &&
                getNumberOfSpaces() > 0 &&
                floorChoiceBox.getValue() != null;
    }

    public VBox getRootContainer() {
        return rootContainer;
    }
}