package parkflow.deskoptworker;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.utils.SessionManager;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {

        // === FONTS ===
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Bold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-ExtraBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-SemiBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Medium.ttf"), 14);

        // === START CLEAN SESSION ===
        SessionManager.getInstance().clear();

        // === SHOW LOGIN ===
        ViewFactory viewFactory = new ViewFactory();
        viewFactory.showLoginWindow();
    }

    public static void main(String[] args) {
        launch();
    }
}
