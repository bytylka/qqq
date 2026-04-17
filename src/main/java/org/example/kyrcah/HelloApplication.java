package org.example.kyrcah;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/kyrcah/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 950, 650);
        String css = getClass().getResource("/org/example/kyrcah/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("КиноМир — Главная");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}