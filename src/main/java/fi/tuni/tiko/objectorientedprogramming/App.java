package fi.tuni.tiko.objectorientedprogramming;

import com.dropbox.core.DbxException;
import fi.tuni.tiko.objectorientedprogramming.JSONparser.H2Connect;
import fi.tuni.tiko.objectorientedprogramming.JSONparser.Item;
import fi.tuni.tiko.objectorientedprogramming.JSONparser.Parser;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class App extends Application {

    private GridPane grid;
    private BorderPane root;
    private DropboxConnection dropbox = null;
    final private String SAVEFILENAME = "save.json";

    @Override
    public void start(Stage window) {

        window.initStyle(StageStyle.DECORATED);
        window.centerOnScreen();
        window.setTitle("Shopping list");

        root = new BorderPane();
        root.setCenter(createList());
        HBox listButtons = new HBox(createNewLineButton(), createClearButton());
        root.setBottom(listButtons);
        VBox saveButtons = new VBox(createLoadButton(), createSaveButton(),
                                    createLoadfromDbButton(), createSaveToDbButton(),
                                    createSaveToBaseButton(), createLoadFromBaseButton());
        root.setRight(saveButtons);
        Scene content = new Scene(root, 320, 240);
        window.setScene(content);

        window.show();

    }

    private Button createLoadFromBaseButton() {
        Button loadFromBaseButton = new Button("Load from Database");
        loadFromBaseButton.setOnAction(this::loadFromBase);
        return loadFromBaseButton;
    }

    private void loadFromBase(ActionEvent actionEvent) {
        H2Connect h2 = new H2Connect();
        List<Item> newItems = h2.fetchItems();

        Parser parser = new Parser();
        for(Item item: newItems) {
            parser.addItem(item);
        }
        h2.close();

        parser.writeToFile();
        loadList(actionEvent);
    }


    private Button createSaveToBaseButton() {
        Button savetoBaseButton = new Button("Save to Database");
        savetoBaseButton.setOnAction(this::saveToBase);
        return savetoBaseButton;
    }

    private void saveToBase(ActionEvent actionEvent) {
        saveList(actionEvent);

        Parser parser = new Parser();
        parser.readFromFile();
        List<Item> items = parser.returnAllItems().get();

        H2Connect h2 = new H2Connect();
        for (Item item: items) {
            h2.saveItem(item);
        }
        h2.close();
    }

    private Button createClearButton() {
        Button clearButton = new Button("Clear List");
        clearButton.setOnAction(this::clearList);
        return clearButton;
    }

    private void clearList(ActionEvent actionEvent) {
        createList();
        root.setCenter(grid);
    }

    private Button createLoadfromDbButton() {
        Button loadFromDbButton = new Button("Load from Dropbox");
        loadFromDbButton.setOnAction(this::loadFromDb);
        return loadFromDbButton;
    }

    private void loadFromDb(ActionEvent actionEvent) {
        if (dropbox == null) {
            connectToDropbox();
        }

        dropbox.readFile(SAVEFILENAME);
        loadList(actionEvent);
    }

    private Button createSaveToDbButton() {
        Button saveToDbButton = new Button("Save to Dropbox");
        saveToDbButton.setOnAction(this::saveToDb);
        return saveToDbButton;
    }

    private void saveToDb(ActionEvent actionEvent) {
        saveList(actionEvent);
        if (dropbox == null) {
            connectToDropbox();
        }

        dropbox.save(SAVEFILENAME);
    }

    private void connectToDropbox() {
        dropbox = new DropboxConnection(new TextInputDialog("your code"));
        dropbox.connect();
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
        Button loadButton = new Button("Load from File");
        loadButton.setOnAction(this::loadList);
        return loadButton;
    }

    private Button createSaveButton() {
        Button saveButton = new Button("Save to File");
        saveButton.setOnAction(this::saveList);
        return saveButton;
    }

    private void loadList(ActionEvent actionEvent) {
        Parser parser = new Parser();
        parser.readFromFile();
        int counter=0;
        grid = new GridPane();
        while (parser.areMoreItems()) {
            Optional<Item> item = parser.returnItem();
            if (item.isPresent()) {
                createNewLine(item.get());
            }
        }

        root.setCenter(grid);
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

    private void createNewLine(Item item) {
        TextField label = new TextField(item.getTag());
        TextField amount = new TextField(item.getProperty());
        grid.add(label, 0, grid.getRowCount()+1);
        grid.add(amount, 1, grid.getRowCount()-1);
    }
}
