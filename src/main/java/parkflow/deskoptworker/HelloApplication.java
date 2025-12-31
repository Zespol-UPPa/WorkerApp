package parkflow.deskoptworker;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;
import parkflow.deskoptworker.utils.SessionManager;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage)  {
        // Ładuj czcionki
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Bold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-ExtraBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-SemiBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Medium.ttf"), 14);

        // Ustaw testowego użytkownika
        User testUser = new User(
                1,
                "Janka",
                "Kowalska",
                "+48123456789",
                "jan.kowalski@parkflow.com",
                "90010112345",
                UserRole.WORKER, true
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