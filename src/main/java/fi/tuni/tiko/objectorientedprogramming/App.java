package fi.tuni.tiko.objectorientedprogramming;

import fi.tuni.tiko.objectorientedprogramming.JSONparser.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
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
        newLineButton.setOnAction(this::newLineAction);
        return newLineButton;
    }

    private void newLineAction(ActionEvent actionEvent) {
        createNewLine();
    }

    private Button createLoadButton() {
        Button loadButton = new Button("Load saved list");
        loadButton.setOnAction(this::loadList);
        return loadButton;
    }

    private Button createSaveButton() {
        Button saveButton = new Button("Save the list");
        saveButton.setOnAction(this::saveList);
        return saveButton;
    }

    private void loadList(ActionEvent actionEvent) {

    }

    private void saveList(ActionEvent actionEvent) {
        Parser parser = new Parser();
        int counter=0;
        String label = "";
        String amount = "";
        for (Node n: grid.getChildren()) {
            TextField text = (TextField) n;
            if (counter % 2 == 0) {
                label = text.getText();
                if (label.isEmpty()) {
                    break;
                }
            }
            else {
                amount = text.getText();
                Item item = new Item(label, amount);
                parser.addItem(item);
            }
            counter++;
        }
        parser.writeToFile();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private GridPane createList() {
        grid = new GridPane();

        for (int i=0; i<5; i++) {
            createNewLine();
        }

        return grid;
    }

    private void createNewLine() {
        TextField label = new TextField();
        TextField amount = new TextField();
        grid.add(label, 0, grid.getRowCount()+1);
        grid.add(amount, 1, grid.getRowCount()-1);
    }
}
