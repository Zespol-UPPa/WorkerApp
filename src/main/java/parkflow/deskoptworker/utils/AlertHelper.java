package parkflow.deskoptworker.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import parkflow.deskoptworker.Controllers.Components.AlertController;

import java.io.IOException;

/**
 * Utility class do wyświetlania własnych alertów spójnych z designem aplikacji.
 */
public class AlertHelper {

    public enum AlertType {
        INFO,
        SUCCESS,
        WARNING,
        ERROR,
        CONFIRM
    }

    /**
     * Wyświetla alert informacyjny
     */
    public static void showInfo(String title, String message) {
        showAlert(AlertType.INFO, title, message);
    }

    /**
     * Wyświetla alert sukcesu
     */
    public static void showSuccess(String title, String message) {
        showAlert(AlertType.SUCCESS, title, message);
    }

    /**
     * Wyświetla alert ostrzeżenia
     */
    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    /**
     * Wyświetla alert błędu
     */
    public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    /**
     * Wyświetla alert potwierdzenia i zwraca true jeśli user potwierdził
     */
    public static boolean showConfirm(String title, String message) {
        return showAlert(AlertType.CONFIRM, title, message);
    }

    /**
     * Główna metoda wyświetlająca alert
     */
    private static boolean showAlert(AlertType type, String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AlertHelper.class.getResource("/parkflow/deskoptworker/components/CustomAlert.fxml")
            );
            Parent root = loader.load();

            AlertController controller = loader.getController();
            controller.setAlertData(type, title, message);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setResizable(false);

            stage.showAndWait();

            return controller.isConfirmed();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load CustomAlert.fxml");
            return false;
        }
    }
}