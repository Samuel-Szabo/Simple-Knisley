/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.protonmail.sarahszabo.knisleyformattool.core;

import java.io.FileInputStream;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Sarah Szabo
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Main_PanelController controller = new Main_PanelController(this);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main_Panel.fxml"));
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(new Image(new FileInputStream(Paths.get("Pictures", "SBU Emblem.png").toFile())));
        stage.setAlwaysOnTop(true);
        stage.setTitle("Simple Knisley Citation Tool α 0.5");
        stage.setResizable(false);
        stage.show();
    }

    public static void openURL(String url) {

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
