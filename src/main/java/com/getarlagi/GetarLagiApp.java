package com.getarlagi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class GetarLagiApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlUrl = getClass().getResource("view/MainView.fxml"); //
        if (fxmlUrl == null) {
            System.err.println("Tidak dapat menemukan MainView.fxml. Pastikan path sudah benar.");
            return;
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl); //
        Parent root = loader.load(); //

        Scene scene = new Scene(root); //
        
        URL cssUrl = getClass().getResource("style.css"); //
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm()); //
        } else {
            System.err.println("Tidak dapat menemukan style.css.");
        }

        primaryStage.setTitle("GetarLagi: Aplikasi Pantauan Gempa Susulan"); //
        primaryStage.setScene(scene); //
        primaryStage.setMinWidth(950); 
        primaryStage.setMinHeight(720);
        primaryStage.show(); //
    }

    public static void main(String[] args) {
        launch(args); //
    }
}