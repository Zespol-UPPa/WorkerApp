package parkflow.deskoptworker.Controllers.Worker;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.Setter;

import java.util.Objects;

public class CustomersTopMenuController {
    @FXML private HBox customersBox;
    @FXML private HBox paymentsBox;
    @FXML private HBox reservationsBox;

    @FXML private Button customersButton;
    @FXML private Button paymentsButton;
    @FXML private Button reservationsButton;

    @FXML private Label customersFilterLabel;
    @FXML private Label paymentsFilterLabel;
    @FXML private Label reservationsFilterLabel;

    @FXML private ImageView customersIcon;
    @FXML private ImageView paymentsIcon;
    @FXML private ImageView reservationsIcon;

    private HBox activeBox;
    @Setter
    private CustomersController parentController;

    @FXML
    public void initialize() {
        setActiveBox(customersBox);

        customersButton.setOnAction(_ -> handleMenuClick(customersBox, "Customers"));
        paymentsButton.setOnAction(_ -> handleMenuClick(paymentsBox, "Payments"));
        reservationsButton.setOnAction(_ -> handleMenuClick(reservationsBox, "Reservations"));

        hideAllFilterLabels();
    }

    private void handleMenuClick(HBox clickedBox, String viewName) {
        setActiveBox(clickedBox);

        if (parentController != null) {
            parentController.onMenuItemSelected(viewName);
        } else {
            System.err.println("Parent controller is null!");
        }
    }

    private void setActiveBox(HBox box) {
        if (activeBox != null) {
            activeBox.getStyleClass().remove("selected");
            changeIconColor(activeBox, false);
        }

        box.getStyleClass().add("selected");
        changeIconColor(box, true);
        activeBox = box;
    }

    /**
     * Programowo ustawia aktywną zakładkę (bez wywołania handleMenuClick)
     * Używane przy nawigacji z filtrem z innych modułów
     */
    public void setActiveTab(String tabName) {
        HBox targetBox = switch (tabName) {
            case "Customers" -> customersBox;
            case "Payments" -> paymentsBox;
            case "Reservations" -> reservationsBox;
            default -> null;
        };

        if (targetBox != null) {
            setActiveBox(targetBox);
        }
    }


    private void changeIconColor(HBox box, boolean isActive) {
        ImageView icon = null;
        String iconPath = "";

        if (box == customersBox) {
            icon = customersIcon;
            iconPath = isActive ? "customersWhite.png" : "customersBlack.png";
        } else if (box == paymentsBox) {
            icon = paymentsIcon;
            iconPath = isActive ? "dollarWhite.png" : "dollarBlack.png";
        } else if (box == reservationsBox) {
            icon = reservationsIcon;
            iconPath = isActive ? "calendarWhite.png" : "calendarBlack.png";
        }

        if (icon == null) return;

        try {
            String fullPath = "/parkflow/deskoptworker/images/" + iconPath;
            icon.setImage(new Image(Objects.requireNonNull(getClass().getResource(fullPath)).toExternalForm()));
        } catch (Exception e) {
            System.err.println("Nie można załadować ikony: " + iconPath);
            e.printStackTrace();
        }
    }

    // ===== FILTER LABEL MANAGEMENT =====

    private void hideAllFilterLabels() {
        if (customersFilterLabel != null) customersFilterLabel.setVisible(false);
        if (paymentsFilterLabel != null) paymentsFilterLabel.setVisible(false);
        if (reservationsFilterLabel != null) reservationsFilterLabel.setVisible(false);
    }

    public void showCustomersFilter() {
        if (customersFilterLabel != null) customersFilterLabel.setVisible(true);
    }

    public void hideCustomersFilter() {
        if (customersFilterLabel != null) customersFilterLabel.setVisible(false);
    }

    public void showPaymentsFilter() {
        if (paymentsFilterLabel != null) paymentsFilterLabel.setVisible(true);
    }

    public void hidePaymentsFilter() {
        if (paymentsFilterLabel != null) paymentsFilterLabel.setVisible(false);
    }

    public void showReservationsFilter() {
        if (reservationsFilterLabel != null) reservationsFilterLabel.setVisible(true);
    }

    public void hideReservationsFilter() {
        if (reservationsFilterLabel != null) reservationsFilterLabel.setVisible(false);
    }
}