package parkflow.deskoptworker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Font regular = Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Regular.ttf"), 14);
        Font bold = Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Bold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-ExtraBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-SemiBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/Inter_18pt-Medium.ttf"), 14);

        //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("shared/login.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("admin/pricingDetailsE.fxml"));

        if (regular != null) {
            System.out.println("Nazwa czcionki Regular: " + regular.getName());
            System.out.println("Rodzina czcionki: " + regular.getFamily());
        }
        if (bold != null) {
            System.out.println("Nazwa czcionki Bold: " + bold.getName());
        }

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setResizable(false);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}