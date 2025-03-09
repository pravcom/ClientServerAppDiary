package org.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Client extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        URL resource = getClass().getResource("/client-view.fxml");
        if (resource == null) {
            System.err.println("FXML file not found!");
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setTitle("Client");
            stage.setScene(scene);
            stage.show();
        }
        //stop app after close window
        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}