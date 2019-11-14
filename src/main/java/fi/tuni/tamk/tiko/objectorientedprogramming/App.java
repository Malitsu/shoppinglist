package fi.tuni.tamk.tiko.objectorientedprogramming;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;

public class App extends Application {

    @Override
    public void start(Stage window) {

        window.initStyle(StageStyle.DECORATED);
        window.centerOnScreen();
        window.setTitle("Shopping list");

        BorderPane root = new BorderPane();
        root.setCenter(createList());
        root.setBottom(createSaveButton());
        Scene content = new Scene(root, 320, 240);
        window.setScene(content);

        window.show();
    }

    private Button createSaveButton() {
        Button saveButton = new Button("Save the list");
        saveButton.setOnAction(this::saveList);
        return saveButton;
    }

    private void saveList(ActionEvent actionEvent) {
        // tässä kutsutaan parseria
    }

    public static void main(String[] args) {
        launch(args);
    }

    private GridPane createList() {
        GridPane grid = new GridPane();

        TextField label = new TextField();
        TextField amount = new TextField();
        grid.add(label, 0, 0);
        grid.add(amount, 1, 0);

        Button newLineButton = new Button("Add new item");
        newLineButton.setOnAction(this::createNewLine);
        grid.add(newLineButton, 0, 1);

        return grid;
    }

    private void createNewLine(ActionEvent actionEvent) {

    }
}
