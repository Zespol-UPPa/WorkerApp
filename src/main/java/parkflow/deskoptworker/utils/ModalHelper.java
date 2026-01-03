package parkflow.deskoptworker.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Utility class do tworzenia modalnych okien w aplikacji.
 * Zapewnia spójny wygląd wszystkich modali - bez paska, z cieniem i przezroczystym tłem.
 */
public class ModalHelper {

    // Ustawienia cienia - dopasowane do Employee Details
    private static final double SHADOW_RADIUS = 50;
    private static final double SHADOW_OFFSET_X = 0;
    private static final double SHADOW_OFFSET_Y = 10;
    private static final double SHADOW_OPACITY = 0.3;  // Zwiększone z 0.15!

    /**
     * Otwiera modal i zwraca controller.
     */
    public static <T> T showModal(String fxmlPath, String title) {
        return showModal(fxmlPath, title, null);
    }

    /**
     * Otwiera modal z możliwością konfiguracji controllera przed wyświetleniem.
     */
    public static <T> T showModal(String fxmlPath, String title, Consumer<T> controllerSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(ModalHelper.class.getResource(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            if (controllerSetup != null && controller != null) {
                controllerSetup.accept(controller);
            }

            Stage stage = createModalStage(root, title);
            stage.showAndWait();

            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load modal: " + fxmlPath);
            return null;
        }
    }

    /**
     * Otwiera modal bez czekania na zamknięcie.
     */
    public static <T> T showModalNonBlocking(String fxmlPath, String title, Consumer<T> controllerSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(ModalHelper.class.getResource(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();

            if (controllerSetup != null && controller != null) {
                controllerSetup.accept(controller);
            }

            Stage stage = createModalStage(root, title);
            stage.show();

            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load modal: " + fxmlPath);
            return null;
        }
    }

    /**
     * Tworzy skonfigurowany Stage dla modala.
     */
    private static Stage createModalStage(Parent root, String title) {
        // Cień - taki jak w Employee Details
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(SHADOW_RADIUS);
        dropShadow.setOffsetX(SHADOW_OFFSET_X);
        dropShadow.setOffsetY(SHADOW_OFFSET_Y);
        dropShadow.setColor(Color.rgb(0, 0, 0, SHADOW_OPACITY));
        root.setEffect(dropShadow);

        // Scena z przezroczystym tłem
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        // Stage
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);

        if (title != null) {
            stage.setTitle(title);
        }

        return stage;
    }

    /**
     * Zamyka modal.
     */
    public static void closeModal(javafx.scene.Node anyNodeInModal) {
        Stage stage = (Stage) anyNodeInModal.getScene().getWindow();
        stage.close();
    }
}