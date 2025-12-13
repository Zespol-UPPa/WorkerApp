package parkflow.deskoptworker.Controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MetricCardController {

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
        } else {
            subtitleLabel.setVisible(false);
        }
    }

    public void setIcon(String iconPath) {
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                Image icon = new Image(getClass().getResourceAsStream(iconPath));
                iconView.setImage(icon);
                iconView.setVisible(true);
            } catch (Exception e) {
                iconView.setVisible(false);
                System.err.println("Cannot load icon: " + iconPath);
            }
        } else {
            iconView.setVisible(false);
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

    public VBox getRoot() {
        return root;
    }
}