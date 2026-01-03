package parkflow.deskoptworker.Controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import parkflow.deskoptworker.utils.AlertHelper.AlertType;

import java.util.Objects;

public class AlertController {

    @FXML private VBox alertContainer;
    @FXML private ImageView alertIcon;
    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private HBox buttonsContainer;
    @FXML private Button primaryButton;
    @FXML private Button secondaryButton;

    @Getter
    private boolean confirmed = false;

    public void setAlertData(AlertType type, String title, String message) {
        titleLabel.setText(title);
        messageLabel.setText(message);

        // Konfiguruj wygląd na podstawie typu
        configureForType(type);
    }

    private void configureForType(AlertType type) {

        alertContainer.getStyleClass().removeAll(
                "alert-info", "alert-success", "alert-warning", "alert-error", "alert-confirm"
        );

        String iconPath;
        String iconStyleClass;
        String containerStyleClass;

        switch (type) {
            case SUCCESS:
                iconPath = "/parkflow/deskoptworker/images/check-circle.png";
                iconStyleClass = "icon-success";
                containerStyleClass = "alert-success";
                primaryButton.setText("OK");
                secondaryButton.setVisible(false);
                secondaryButton.setManaged(false);
                break;

            case WARNING:
                iconPath = "/parkflow/deskoptworker/images/triangle-warning.png";
                iconStyleClass = "icon-warning";
                containerStyleClass = "alert-warning";
                primaryButton.setText("OK");
                secondaryButton.setVisible(false);
                secondaryButton.setManaged(false);
                break;

            case ERROR:
                iconPath = "/parkflow/deskoptworker/images/cross-circle.png";
                iconStyleClass = "icon-error";
                containerStyleClass = "alert-error";
                primaryButton.setText("OK");
                secondaryButton.setVisible(false);
                secondaryButton.setManaged(false);
                break;

            case CONFIRM:
                iconPath = "/parkflow/deskoptworker/images/interrogation.png";
                iconStyleClass = "icon-confirm";
                containerStyleClass = "alert-confirm";
                primaryButton.setText("Confirm");
                secondaryButton.setText("Cancel");
                secondaryButton.setVisible(true);
                secondaryButton.setManaged(true);
                break;

            case INFO:
            default:
                iconPath = "/parkflow/deskoptworker/images/info.png";
                iconStyleClass = "icon-info";
                containerStyleClass = "alert-info";
                primaryButton.setText("OK");
                secondaryButton.setVisible(false);
                secondaryButton.setManaged(false);
                break;
        }

        // Ustaw ikonę
        try {
            alertIcon.setImage(new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream(iconPath))
            ));
        } catch (Exception e) {
            System.err.println("Could not load alert icon: " + iconPath);
        }

        alertContainer.getStyleClass().add(containerStyleClass);
    }

    @FXML
    private void handlePrimaryAction() {
        confirmed = true;
        closeAlert();
    }

    @FXML
    private void handleSecondaryAction() {
        confirmed = false;
        closeAlert();
    }

    private void closeAlert() {
        Stage stage = (Stage) primaryButton.getScene().getWindow();
        stage.close();
    }
}