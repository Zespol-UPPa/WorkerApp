package parkflow.deskoptworker.Controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class StatusCardController {

    @FXML
    private VBox cardContainer;

    @FXML
    private VBox iconContainer;

    @FXML
    private ImageView icon;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Label valueLabel;

    @FXML
    private Label changeLabel;

    /**
     * Inicjalizuje kartę z danymi
     * @param title Tytuł karty (np. "Total Revenue")
     * @param subtitle Podtytuł (np. "(Wallet Deposits Only)")
     * @param value Główna wartość (np. "45780.50 $")
     * @param change Zmiana (np. "+12.5% from last month")
     * @param iconPath Ścieżka do ikony PNG
     * @param iconBackgroundColor Kolor tła ikony w formacie hex (np. "#E8F5E9")
     */
    public void setData(String title, String subtitle, String value, String change,
                        String iconPath, String iconBackgroundColor) {
        titleLabel.setText(title);
        subtitleLabel.setText(subtitle);
        valueLabel.setText(value);
        changeLabel.setText(change);

        // Ustawienie ikony
        try {
            Image iconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
            icon.setImage(iconImage);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + iconPath);
            e.printStackTrace();
        }

        // Ustawienie koloru tła ikony
        iconContainer.setStyle("-fx-background-color: " + iconBackgroundColor + ";");
    }

    // === SETTERY DO AKTUALIZACJI POJEDYNCZYCH PÓL ===

    public void setTitle(String title) {
        if (title != null) {
            titleLabel.setText(title);
            titleLabel.setVisible(true);
            titleLabel.setManaged(true);
        } else {
            titleLabel.setVisible(false);
            titleLabel.setManaged(false);
        }
    }

    public void setSubtitle(String subtitle) {
        if (subtitle != null && !subtitle.isEmpty()) {
            subtitleLabel.setText(subtitle);
            subtitleLabel.setVisible(true);
            subtitleLabel.setManaged(true);
        } else {
            subtitleLabel.setVisible(false);
            subtitleLabel.setManaged(false);
        }
    }

    public void setValue(String value) {
        if (value != null) {
            valueLabel.setText(value);
        }
    }

    public void setChange(String change) {
        if (change != null && !change.isEmpty()) {
            changeLabel.setText(change);
            changeLabel.setVisible(true);
            changeLabel.setManaged(true);
        } else {
            changeLabel.setVisible(false);
            changeLabel.setManaged(false);
        }
    }

    public void setIcon(String iconPath, String backgroundColor) {
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                Image iconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
                icon.setImage(iconImage);
                icon.setVisible(true);
                icon.setManaged(true);
                iconContainer.setVisible(true);
                iconContainer.setManaged(true);
            } catch (Exception e) {
                System.err.println("Error loading icon: " + iconPath);
                hideIcon();
            }
        } else {
            hideIcon();
        }

        if (backgroundColor != null) {
            iconContainer.setStyle("-fx-background-color: " + backgroundColor + ";");
        }
    }

    private void hideIcon() {
        icon.setVisible(false);
        icon.setManaged(false);
        iconContainer.setVisible(false);
        iconContainer.setManaged(false);
    }

    /**
     * Wariant dla typu karty (green, blue, orange)
     * @param type "revenue", "usage", "pending"
     */
    public void setCardType(String type) {
        String iconBg = "";
        String iconPath = switch (type.toLowerCase()) {
            case "revenue" -> {
                iconBg = "#E8F5E9"; // Light green
                yield "/icons/dollar-icon.png";
            }
            case "usage" -> {
                iconBg = "#E3F2FD"; // Light blue
                yield "/icons/parking-icon.png";
            }
            case "pending" -> {
                iconBg = "#FFF3E0"; // Light orange
                yield "/icons/pending-icon.png";
            }
            default -> "";
        };

        iconContainer.setStyle("-fx-background-color: " + iconBg + ";");

        try {
            Image iconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
            icon.setImage(iconImage);
        } catch (Exception e) {
            System.err.println("Error loading icon for type: " + type);
        }
    }

    /**
     * Ustawia widoczność labela zmiany
     */
    public void setChangeVisible(boolean visible) {
        changeLabel.setVisible(visible);
        changeLabel.setManaged(visible);
    }

    /**
     * Ustawia kolor zmiany (dla pozytywnych/negatywnych wartości)
     */
    public void setChangeColor(String color) {
        changeLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    public void setValueColor(String color) {
        if (color != null && !color.isEmpty()) {
            valueLabel.setStyle("-fx-text-fill: " + color + ";");
            };

        }


}