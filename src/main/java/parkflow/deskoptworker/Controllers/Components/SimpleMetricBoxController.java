package parkflow.deskoptworker.Controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SimpleMetricBoxController {

    @FXML
    private VBox cardContainer;

    @FXML
    private Label titleLabel;

    @FXML
    private Label valueLabel;

    @FXML
    private Label subtitleLabel;  // ✓ NOWY - opcjonalny subtitle

    /**
     * Inicjalizuje kartę z danymi (bez subtitle)
     * @param title Tytuł metryki (np. "Avg Transaction Value")
     * @param value Wartość (np. "36.70 $")
     */
    public void setData(String title, String value) {
        titleLabel.setText(title);
        valueLabel.setText(value);
        // Subtitle pozostaje ukryty
    }

    /**
     * Inicjalizuje kartę z danymi i subtitle
     * @param title Tytuł metryki
     * @param value Wartość
     * @param subtitle Dodatkowy tekst pod wartością (opcjonalny)
     */
    public void setData(String title, String value, String subtitle) {
        titleLabel.setText(title);
        valueLabel.setText(value);

        if (subtitle != null && !subtitle.isEmpty()) {
            subtitleLabel.setText(subtitle);
            subtitleLabel.setVisible(true);
            subtitleLabel.setManaged(true);
        } else {
            subtitleLabel.setVisible(false);
            subtitleLabel.setManaged(false);
        }
    }

    /**
     * Ustawia typ karty z predefiniowanymi kolorami
     * @param type "green", "blue", "purple", "orange", "red", "pink"
     */
    public void setCardType(String type) {
        // Usuwamy wszystkie klasy typu
        cardContainer.getStyleClass().removeAll(
                "metric-green",
                "metric-blue",
                "metric-purple",
                "metric-orange",
                "metric-red",
                "metric-pink"
        );

        // Dodajemy nową klasę
        switch (type.toLowerCase()) {
            case "green":
                cardContainer.getStyleClass().add("metric-green");
                break;
            case "blue":
                cardContainer.getStyleClass().add("metric-blue");
                break;
            case "purple":
                cardContainer.getStyleClass().add("metric-purple");
                break;
            case "orange":
                cardContainer.getStyleClass().add("metric-orange");
                break;
            case "red":
                cardContainer.getStyleClass().add("metric-red");
                break;
            case "pink":
                cardContainer.getStyleClass().add("metric-pink");
                break;
        }
    }

    /**
     * Ustawia niestandardowy kolor obramowania
     * @param borderColor Kolor w formacie hex (np. "#4CAF50")
     * @param backgroundColor Kolor tła w formacie hex (np. "#E8F5E9")
     */
    public void setCustomColors(String borderColor, String backgroundColor) {
        cardContainer.setStyle(
                "-fx-border-color: " + borderColor + ";" +
                        "-fx-background-color: " + backgroundColor + ";"
        );
    }

    /**
     * Ustawia niestandardowy kolor wartości
     * @param color Kolor w formacie hex
     */
    public void setValueColor(String color) {
        valueLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    /**
     * Ustawia subtitle (opcjonalny tekst pod wartością)
     * @param subtitle Tekst subtitle
     */
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

    /**
     * Aktualizuje tylko wartość
     * @param value Nowa wartość
     */
    public void setValue(String value) {
        if (value != null) {
            valueLabel.setText(value);
        }
    }

    /**
     * Aktualizuje tylko tytuł
     * @param title Nowy tytuł
     */
    public void setTitle(String title) {
        if (title != null) {
            titleLabel.setText(title);
        }
    }

    /**
     * Zwraca główny kontener (dla kompatybilności)
     * @return VBox container
     */
    public VBox getRoot() {
        return cardContainer;
    }
}