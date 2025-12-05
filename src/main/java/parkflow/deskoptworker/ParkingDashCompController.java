package parkflow.deskoptworker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ParkingDashCompController {
    @FXML private Label numberLabel;
    @FXML private Label nameLabel;
    @FXML private Label percentLabel;
    @FXML private Label revenueLabel;
    @FXML private Label sessionsLabel;

    public void setData(int number, String name, double revenue, int sessions, int percent) {
        numberLabel.setText(String.valueOf(number));
        nameLabel.setText(name);
        revenueLabel.setText(formatRevenue(revenue) + " $");
        sessionsLabel.setText(String.valueOf(sessions));
        percentLabel.setText(percent + "%");

        // Ustaw kolor procentu na podstawie wartości
        setPercentColor(percent);
    }

    private void setPercentColor(int percent) {
        percentLabel.getStyleClass().removeAll("percent_high", "percent_medium", "percent_low");

        if (percent >= 90) {
            percentLabel.getStyleClass().add("percent_high"); // czerwony/pomarańczowy
        } else if (percent >= 70) {
            percentLabel.getStyleClass().add("percent_medium"); // żółty
        } else {
            percentLabel.getStyleClass().add("percent_low"); // zielony
        }
    }

    private String formatRevenue(double revenue) {
        if (revenue >= 1000) {
            return String.format("%.0f", revenue);
        }
        return String.format("%.2f", revenue);
    }
}