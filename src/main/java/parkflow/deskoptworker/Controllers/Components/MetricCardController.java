package parkflow.deskoptworker.Controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.util.Objects;

public class MetricCardController {

    @Getter
    @FXML private VBox root;
    @FXML private ImageView iconView;
    @FXML private Label titleLabel;
    @FXML private Label valueLabel;
    @FXML private Label subtitleLabel;

    @FXML
    public void initialize() {
        // Domyślnie ukryj subtitle i ikonę jeśli puste
        subtitleLabel.setVisible(false);
        iconView.setVisible(false);
    }

    // Setters
    public void setTitle(String title) {
        if (title != null) {
            titleLabel.setText(title);
        }
    }

    public void setValue(String value) {
        if (value != null) {
            valueLabel.setText(value);
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

    public void setIcon(String iconPath) {
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
                iconView.setImage(icon);
                iconView.setVisible(true);
                iconView.setManaged(true);  // teraz zajmuje miejsce
            } catch (Exception e) {
                iconView.setVisible(false);
                iconView.setManaged(false);
                System.err.println("Cannot load icon: " + iconPath);
            }
        } else {
            iconView.setVisible(false);
            iconView.setManaged(false);  // nie zajmuje miejsca
        }
    }

    public void setColorTheme(String colorClass) {
        // Usuń poprzednie klasy kolorów
        root.getStyleClass().removeIf(style ->
                style.equals("card-green") ||
                        style.equals("card-blue") ||
                        style.equals("card-purple") ||
                        style.equals("card-orange") ||
                        style.equals("card-red") ||
                        style.equals("card-white")
        );

        // Dodaj nową klasę koloru
        if (colorClass != null && !colorClass.isEmpty()) {
            root.getStyleClass().add(colorClass);
        }
    }

    public void setData(String title, String value, String subtitle, String iconPath, String colorClass) {
        setTitle(title);
        setValue(value);
        setSubtitle(subtitle);
        setIcon(iconPath);
        setColorTheme(colorClass);
    }
    public void setValueColor(String color) {
        if (color != null && !color.isEmpty()) {
            String hexColor = switch (color) {
                case "value-green" -> "#10b981";
                case "value-red" -> "#ef4444";
                case "value-blue" -> "#3b82f6";
                case "value-purple" -> "#8b5cf6";
                case "value-orange" -> "#f59e0b";
                default -> "#1a1a1a";
            };
            valueLabel.setStyle("-fx-text-fill: " + hexColor + ";");
        }
    }
}