package parkflow.deskoptworker;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Ładuj czcionki
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Bold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-ExtraBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-SemiBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Medium.ttf"), 14);

        // Ustaw testowego użytkownika
        User testUser = new User(
                1,
                "Jan",
                "Kowalski",
                "+48 123 456 789",
                "jan.kowalski@parkflow.com",
                "90010112345",
                UserRole.ADMIN
        );
        SessionManager.setCurrentUser(testUser);

        // Otwórz odpowiednie okno na podstawie roli
        ViewFactory viewFactory = new ViewFactory();

        if (testUser.getRole() == UserRole.ADMIN) {
            viewFactory.showAdminWindow();
        } else {
            viewFactory.showWorkerWindow();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}