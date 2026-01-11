package parkflow.deskoptworker.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import parkflow.deskoptworker.Controllers.Components.SectionController;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.api.ApiClient;
import parkflow.deskoptworker.models.Section;
import parkflow.deskoptworker.utils.FieldValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddParkingController {

    @FXML private TextField parkingNameField;
    @FXML private TextField addressField;
    @FXML private TextField numberOfFloorsField;
    @FXML private Button addSectionButton;
    @FXML private VBox sectionsContainer;
    @FXML private VBox emptyVBox;
    @FXML private Button cancelButton;
    @FXML private Button addParkingButton;

    private final ApiClient api = new ApiClient();

    private int sectionCounter = 0;  // Track how many sections added

    @FXML
    public void initialize() {
        System.out.println("AddParkingController initialized");

        // Add validators
        setupFieldValidators();
    }

    /**
     * Setup field validators
     */
    private void setupFieldValidators() {
        // Number of floors: digits only, max 1 character (0-5 will be validated on submit)
        FieldValidator.addDigitsOnlyFilter(numberOfFloorsField, 1);
    }

    /**
     * Handle adding a new section
     */
    @FXML
    private void handleAddSection() {
        try {
            // Load section item FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/parkflow/deskoptworker/components/sectionItem.fxml")
            );
            VBox sectionItem = loader.load();
            SectionController controller = loader.getController();
            try {
                int numberOfFloors = Integer.parseInt(numberOfFloorsField.getText());

                if (numberOfFloors <= 0) {
                    throw new NumberFormatException("Liczba pięter musi być > 0");
                }

                controller.setNumberOfFloors(numberOfFloors);

            } catch (NumberFormatException e) {
                System.out.println("Nieprawidłowa liczba pięter");
                showAlert(
                        "Validation Error",
                        "Please enter a valid number of floors before adding sections."
                );

                return;
            }

            // Calculate section prefix (A, B, C, D, ...)
            char prefixChar = (char) ('A' + sectionCounter);
            String prefix = String.valueOf(prefixChar);
            controller.setSectionPrefix(prefix);

            // Hide empty state
            if (emptyVBox.isVisible()) {
                emptyVBox.setVisible(false);
                emptyVBox.setManaged(false);
            }

            // Set remove callback
            controller.setOnRemove(() -> {
                sectionsContainer.getChildren().remove(sectionItem);

                // Show empty state if no sections left (except emptyVBox itself)
                long actualSections = sectionsContainer.getChildren().stream()
                        .filter(node -> node != emptyVBox)
                        .count();

                if (actualSections == 0) {
                    emptyVBox.setVisible(true);
                    emptyVBox.setManaged(true);
                }
            });

            // Store controller reference in node's UserData
            sectionItem.setUserData(controller);

            // Add to container
            sectionsContainer.getChildren().add(sectionItem);
            sectionCounter++;

            System.out.println("Section " + prefix + " added to UI");

        } catch (IOException e) {
            System.err.println("Failed to load section item: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to add section: " + e.getMessage());
        }
    }

    /**
     * Handle adding parking
     */
    @FXML
    private void handleAddParking() {
        // Validate inputs
        String name = parkingNameField.getText().trim();
        String address = addressField.getText().trim();
        String floorsText = numberOfFloorsField.getText().trim();

        if (name.isEmpty()) {
            showAlert("Validation Error", "Please enter parking name");
            return;
        }

        if (address.isEmpty()) {
            showAlert("Validation Error", "Please enter address");
            return;
        }

        if (floorsText.isEmpty()) {
            showAlert("Validation Error", "Please enter number of floors");
            return;
        }

        // Parse number of floors (for validation only)
        int numberOfFloors;
        try {
            numberOfFloors = Integer.parseInt(floorsText);
            if (numberOfFloors < 0 || numberOfFloors > 5) {
                showAlert("Validation Error", "Number of floors must be between 0 and 5");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid number of floors");
            return;
        }

        // Collect sections from UI
        List<Map<String, Object>> sectionsList = new ArrayList<>();
        List<SectionController> controllers = new ArrayList<>();

        for (Node node : sectionsContainer.getChildren()) {
            // Skip emptyVBox
            if (node == emptyVBox) continue;

            // Get controller from UserData
            Object userData = node.getUserData();
            if (userData instanceof SectionController) {
                controllers.add((SectionController) userData);
            }
        }

        for (SectionController controller : controllers) {
            Section section = controller.getSection();

            if (section != null) {
                // Validate floor level doesn't exceed number of floors
                if (section.getFloorLevel() >= numberOfFloors) {
                    showAlert("Validation Error",
                            "Section " + section.getPrefix() + " floor level (" +
                                    section.getFloorLevel() + ") cannot be >= number of floors (" +
                                    numberOfFloors + ")");
                    return;
                }

                Map<String, Object> sectionMap = new HashMap<>();
                sectionMap.put("prefix", section.getPrefix());
                sectionMap.put("numberOfSpots", section.getNumberOfSpots());
                sectionMap.put("floorLevel", section.getFloorLevel());
                sectionMap.put("reservable", section.isReservable());
                sectionsList.add(sectionMap);
            } else {
                showAlert("Validation Error", "Invalid section data");
                return;
            }
        }

        if (sectionsList.isEmpty()) {
            showAlert("Validation Error", "Please add at least one section");
            return;
        }

        // Build request
        Map<String, Object> request = new HashMap<>();
        request.put("name", name);
        request.put("address", address);
        request.put("sections", sectionsList);

        // Send to backend
        try {
            addParkingButton.setDisable(true);
            addParkingButton.setText("Creating...");


            Map<String, Object> response = api.post(
                    "/admin/parkings",
                    request,
                    true,
                    Map.class
            );

            System.out.println("Parking created successfully: " + response);

            // Show success alert with reminder
            showSuccessAlert(
                    "Parking Created!",
                    "Parking has been created successfully.\n\n" +
                            "IMPORTANT: Don't forget to set the pricing in the Parkings tab!"
            );

            // Close modal
            closeModal();

        } catch (Exception e) {
            System.err.println("Failed to create parking: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to create parking: " + e.getMessage());

            addParkingButton.setDisable(false);
            addParkingButton.setText("Add Parking");
        }
    }

    /**
     * Handle cancel
     */
    @FXML
    private void handleCancel() {
        closeModal();
    }

    /**
     * Close modal
     */
    private void closeModal() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Show error alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show success alert with reminder
     */
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}