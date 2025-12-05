package parkflow.deskoptworker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

    @FXML
    public void initialize() {
        // Walidacja - tylko liczby dla pięter
        numberOfFloorsField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                numberOfFloorsField.setText(oldVal);
            }
        });
    }

    @FXML
    private void handleAddSection() {
        // Sprawdź czy podano liczbę pięter
        if (numberOfFloorsField.getText().isEmpty()) {
            showAlert("Error", "Please enter the number of floors first!");
            return;
        }

        int maxFloors = Integer.parseInt(numberOfFloorsField.getText());
        if (maxFloors <= 0) {
            showAlert("Error", "Number of floors must be greater than 0!");
            return;
        }

        if (sectionCounter >= SECTION_LETTERS.length) {
            showAlert("Error", "Maximum number of sections reached!");
            return;
        }

        try {
            // Ukryj empty state przy pierwszej sekcji
            if (emptyVBox.isVisible()) {
                emptyVBox.setVisible(false);
                emptyVBox.setManaged(false);
            }

            // Załaduj komponent sekcji
            FXMLLoader loader = new FXMLLoader(getClass().getResource("components/SectionItem.fxml"));
            VBox sectionItem = loader.load();
            SectionController controller = loader.getController();

            // Ustaw dane sekcji
            String sectionLetter = SECTION_LETTERS[sectionCounter];
            controller.setSectionData(sectionLetter, maxFloors);

            // Ustaw callback usuwania
            controller.setOnDelete(() -> removeSection(controller));

            // Dodaj do kontenera
            sectionsContainer.getChildren().add(sectionItem);
            sectionControllers.add(controller);
            sectionCounter++;

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load section component!");
        }
    }

    private void removeSection(SectionController controller) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Remove Section");
        confirmation.setHeaderText("Remove Section " + controller.getSectionLetter() + "?");
        confirmation.setContentText("This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                sectionsContainer.getChildren().remove(controller.getRootContainer());
                sectionControllers.remove(controller);

                // Pokaż empty state jeśli usunięto wszystkie sekcje
                if (sectionControllers.isEmpty()) {
                    emptyVBox.setVisible(true);
                    emptyVBox.setManaged(true);
                    sectionCounter = 0;
                }
            }
        });
    }

    @FXML
    private void handleAddParking() {
        // Walidacja
        if (!validateInputs()) {
            return;
        }

        // Zbierz dane
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
        showAlert("Success", "Parking added successfully!");
    }

    private boolean validateInputs() {
        if (parkingNameField.getText().isEmpty()) {
            showAlert("Validation Error", "Please enter parking name!");
            return false;
        }

        if (addressField.getText().isEmpty()) {
            showAlert("Validation Error", "Please enter address!");
            return false;
        }

        if (numberOfFloorsField.getText().isEmpty()) {
            showAlert("Validation Error", "Please enter number of floors!");
            return false;
        }

        if (sectionControllers.isEmpty()) {
            showAlert("Validation Error", "Please add at least one section!");
            return false;
        }

        for (SectionController section : sectionControllers) {
            if (!section.isValid()) {
                showAlert("Validation Error",
                        "Please fill all fields for Section " + section.getSectionLetter());
                return false;
            }
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel");
        confirmation.setHeaderText("Discard changes?");
        confirmation.setContentText("All entered data will be lost.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Zamknij okno lub wróć do poprzedniego widoku
                System.out.println("Cancelled");
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}