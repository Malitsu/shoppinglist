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

    private GridPane grid;

    @Override
    public void start(Stage window) {

        window.initStyle(StageStyle.DECORATED);
        window.centerOnScreen();
        window.setTitle("Shopping list");

        BorderPane root = new BorderPane();
        root.setCenter(createList());
        root.setBottom(createNewLineButton());
        root.setRight(createSaveButton());
        Scene content = new Scene(root, 320, 240);
        window.setScene(content);

        window.show();
    }

    private Button createNewLineButton() {
        Button newLineButton = new Button("Add new item");
        newLineButton.setOnAction(this::createNewLine);
        return newLineButton;
    }

    private Button createSaveButton() {
        Button saveButton = new Button("Save the list");
        saveButton.setOnAction(this::saveList);
        return saveButton;
    }

    private void saveList(ActionEvent actionEvent) {
        int counter=0;
        String label ="";
        int amount;
        for (Node n: grid.getChildren()) {
            TextField text = (TextField) n;
            if (counter % 2 == 0) { label = text.getText(); }
            else {
                amount = Integer.parseInt(text.getText());
                Item item = new Item(label, amount);
                System.out.println(item.toString());
            }
            counter++;
        }
        // tässä kutsutaan parseria
    }

    public static void main(String[] args) {
        launch(args);
    }

    private GridPane createList() {
        grid = new GridPane();

        TextField label = new TextField();
        TextField amount = new TextField();
        grid.add(label, 0, 0);
        grid.add(amount, 1, 0);

        Button newLineButton = new Button("Add new item");
        newLineButton.setOnAction(this::createNewLine);

        return grid;
    }

    private void createNewLine(ActionEvent actionEvent) {

    }
}
