package com.example.chatjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
/*
usuarios: crear servidor?
         unirse

extra: tiempo
        /kick usuario

*/
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
//Cargar
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        ChatController controller = loader.getController();

        Scene scene = new Scene(root, 720, 480);
//error cierre
        primaryStage.setOnCloseRequest(event -> {
            controller.shutdown();
        });

        try {
            String cssPath = getClass().getResource("mainCSS.css").toExternalForm();
            if (cssPath != null) {
                scene.getStylesheets().add(cssPath);
            }
        } catch (NullPointerException e) {
            System.out.println("error css");
        }
        primaryStage.setTitle("ChatFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
//Para que no explote
        System.setProperty("prism.verbose", "true");

        launch(args);
    }
}