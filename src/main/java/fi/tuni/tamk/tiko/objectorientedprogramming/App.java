package fi.tuni.tamk.tiko.objectorientedprogramming;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;

public class App extends Application {
    @Override
    public void start(Stage window) {
        window.setTitle("JavaFX HelloWorld!");
        StackPane root = new StackPane(new Button("Click ME"));
        Scene content = new Scene(root, 320, 240);
        window.initStyle(StageStyle.DECORATED);
        window.setScene(content);
        window.show();
    }
    public static void main(String args[]) {
        launch(args);
    }
}
