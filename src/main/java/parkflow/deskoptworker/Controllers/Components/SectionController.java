package parkflow.deskoptworker.Controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Getter;

public class SectionController {
    @Getter
    @FXML private VBox rootContainer;
    @FXML private Label sectionNameLabel;
    @FXML private TextField numberOfSpacesField;
    @FXML private ChoiceBox<Integer> floorChoiceBox;
    @FXML private CheckBox isReservableCheckBox;
    @FXML private Button deleteButton;

    // Gettery do odczytu danych
    @Getter
    private String sectionLetter;
    private Runnable onDeleteCallback;

    public void initialize() {
        numberOfSpacesField.textProperty().addListener((_, oldVal, newVal) -> {
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
        deleteButton.setOnAction(_ -> {
            if (onDeleteCallback != null) {
                onDeleteCallback.run();
            }
        });
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

}