package parkflow.deskoptworker.Controllers.sharedPanels.Reports;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Setter;

import java.util.Objects;

public class ReportsTopMenuController {
    @FXML private Button overviewButton;
    @FXML private Button financialButton;
    @FXML private Button occupancyButton;
    @FXML private Button customersButton;

    @FXML private ImageView overviewIcon;
    @FXML private ImageView financialIcon;
    @FXML private ImageView occupancyIcon;
    @FXML private ImageView sessionIcon;
    @FXML private ImageView customersIcon;
    @FXML private ImageView reservationIcon;

    private Button activeButton;
    @Setter
    private ReportsController parentController;

    @FXML
    public void initialize() {
        setActiveButton(overviewButton);

        overviewButton.setOnAction(e -> handleMenuClick(overviewButton, "Overview"));
        financialButton.setOnAction(e -> handleMenuClick(financialButton, "Financial"));
        occupancyButton.setOnAction(e -> handleMenuClick(occupancyButton, "Occupancy"));
        customersButton.setOnAction(e -> handleMenuClick(customersButton, "Customers"));
    }

    private void handleMenuClick(Button clickedButton, String viewName) {
        setActiveButton(clickedButton);

        if (parentController != null) {
            parentController.onMenuItemSelected(viewName);
        } else {
            System.err.println("Parent controller is null!");
        }
    }

    private void setActiveButton(Button button) {
        // Usuń klasę "active" z poprzedniego przycisku
        if (activeButton != null) {
            activeButton.getStyleClass().remove("selected");
            changeIconColor(activeButton, false); // Zmień na czarną ikonę
        }

        // Dodaj klasę "active" do nowego przycisku
        button.getStyleClass().add("selected");
        changeIconColor(button, true); // Zmień na białą ikonę
        activeButton = button;
    }

    private void changeIconColor(Button button, boolean isActive) {
        ImageView icon = (ImageView) button.getGraphic();
        if (icon == null) return;

        String iconPath = "";

        // Określ ścieżkę do odpowiedniej ikony
        if (button == overviewButton) {
            iconPath = isActive ? "beatWhite.png" : "beatBlack.png";
        } else if (button == financialButton) {
            iconPath = isActive ? "dollarWhite.png" : "dollarBlack.png";
        } else if (button == occupancyButton) {
            iconPath = isActive ? "columnWhite.png" : "columnBlack.png";
        } else if (button == customersButton) {
            iconPath = isActive ? "customersWhite.png" : "customersBlack.png";
        }

        String fullPath = null;
        try {
            // Załaduj odpowiednią ikonę - dodaj pełną ścieżkę tutaj
            fullPath = "/parkflow/deskoptworker/images/" + iconPath;
            icon.setImage(new Image(Objects.requireNonNull(getClass().getResource(fullPath)).toExternalForm()));
        } catch (Exception e) {
            System.err.println("Nie można załadować ikony: " + fullPath);
            e.printStackTrace();
        }
    }
}
