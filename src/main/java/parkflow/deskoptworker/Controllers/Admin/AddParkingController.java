package parkflow.deskoptworker.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Getter;
import parkflow.deskoptworker.Controllers.Components.SectionController;
import parkflow.deskoptworker.models.Parking;
import parkflow.deskoptworker.utils.AlertHelper;
import parkflow.deskoptworker.utils.ModalHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddParkingController {
    @FXML private TextField parkingNameField;
    @FXML private TextField addressField;
    @FXML private TextField numberOfFloorsField;
    @FXML private Button addSectionButton;
    @FXML private VBox sectionsContainer;
    @FXML private VBox emptyVBox;
    @FXML private Button cancelButton;
    @FXML private Button addParkingButton;

    private List<SectionController> sectionControllers = new ArrayList<>();
    private int sectionCounter = 0;
    private static final String[] SECTION_LETTERS = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"
    };

    @Getter
    private Parking savedParking = null;

    @FXML
    public void initialize() {
        // Walidacja - tylko liczby dla pięter
        numberOfFloorsField.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                numberOfFloorsField.setText(oldVal);
            }
        });
    }

    @FXML
    private void handleAddSection() {
        if (numberOfFloorsField.getText().isEmpty()) {
            AlertHelper.showError("Error", "Please enter the number of floors first!");
            return;
        }

        int maxFloors = Integer.parseInt(numberOfFloorsField.getText());
        if (maxFloors <= 0) {
            AlertHelper.showError("Error", "Number of floors must be greater than 0!");
            return;
        }

        if (sectionCounter >= SECTION_LETTERS.length) {
            AlertHelper.showWarning("Limit Reached", "Maximum number of sections reached!");
            return;
        }

        try {
            if (emptyVBox.isVisible()) {
                emptyVBox.setVisible(false);
                emptyVBox.setManaged(false);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/SectionItem.fxml"));
            VBox sectionItem = loader.load();
            SectionController controller = loader.getController();

            String sectionLetter = SECTION_LETTERS[sectionCounter];
            controller.setSectionData(sectionLetter, maxFloors);
            controller.setOnDelete(() -> removeSection(controller));

            sectionsContainer.getChildren().add(sectionItem);
            sectionControllers.add(controller);
            sectionCounter++;

        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "Failed to load section component!");
        }
    }

    private void removeSection(SectionController controller) {
        boolean confirmed = AlertHelper.showConfirm(
                "Remove Section",
                "Remove Section " + controller.getSectionLetter() + "? This action cannot be undone."
        );

        if (confirmed) {
            sectionsContainer.getChildren().remove(controller.getRootContainer());
            sectionControllers.remove(controller);

            if (sectionControllers.isEmpty()) {
                emptyVBox.setVisible(true);
                emptyVBox.setManaged(true);
                sectionCounter = 0;
            }
        }
    }

    @FXML
    private void handleAddParking() {
        if (!validateInputs()) {
            return;
        }

        String parkingName = parkingNameField.getText();
        String address = addressField.getText();
        int numberOfFloors = Integer.parseInt(numberOfFloorsField.getText());

        System.out.println("=== Parking Data ===");
        System.out.println("Name: " + parkingName);
        System.out.println("Address: " + address);
        System.out.println("Floors: " + numberOfFloors);
        System.out.println("\n=== Sections ===");

        for (SectionController section : sectionControllers) {
            System.out.println("Section " + section.getSectionLetter() + ":");
            System.out.println("  - Spaces: " + section.getNumberOfSpaces());
            System.out.println("  - Floor: " + section.getFloor());
            System.out.println("  - Reservable: " + section.isReservable());
        }

        // TODO: Zapisz do bazy danych
        AlertHelper.showSuccess("Success", "Parking added successfully!");
        ModalHelper.closeModal(cancelButton);
    }

    private boolean validateInputs() {
        if (parkingNameField.getText().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Please enter parking name!");
            return false;
        }

        if (addressField.getText().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Please enter address!");
            return false;
        }

        if (numberOfFloorsField.getText().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Please enter number of floors!");
            return false;
        }

        if (sectionControllers.isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Please add at least one section!");
            return false;
        }

        for (SectionController section : sectionControllers) {
            if (!section.isValid()) {
                AlertHelper.showWarning("Validation Error",
                        "Please fill all fields for Section " + section.getSectionLetter());
                return false;
            }
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        boolean confirmed = AlertHelper.showConfirm(
                "Discard Changes",
                "All entered data will be lost. Are you sure?"
        );

        if (confirmed) {
            ModalHelper.closeModal(cancelButton);
        }
    }
}