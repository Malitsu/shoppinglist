package fi.tuni.tiko.objectorientedprogramming;

import com.dropbox.core.DbxException;
import fi.tuni.tiko.objectorientedprogramming.JSONparser.H2Connect;
import fi.tuni.tiko.objectorientedprogramming.JSONparser.Item;
import fi.tuni.tiko.objectorientedprogramming.JSONparser.Parser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Shoppinglist extends Application {

    private GridPane grid;
    private BorderPane root;
    private Scene content;

    private DropboxConnection dropbox = null;
    final private String SAVEFILENAME = "save.json";
    private Parser<String, String> parser = new Parser<>();

    enum Save { FILE, DROPBOX, DATABASE; }
    private Save saveMethod = Save.FILE;

    @Override
    public void start(Stage window) {

        window.initStyle(StageStyle.DECORATED);
        window.centerOnScreen();
        window.setTitle("Shopping list");

        root = new BorderPane();
        root.setTop(generateMenubar());
        root.setCenter(createList());
        HBox listButtons = new HBox(createClearButton(), createNewLineButton());
        listButtons.setPadding(new Insets(10,10,10,10));
        root.setBottom(listButtons);
        VBox saveButtons = new VBox(createLoadButton(), createSaveButton());
        saveButtons.setPadding(new Insets(10,10,10,10));
        root.setRight(saveButtons);

        content = new Scene(root, 450, 330);
        window.setScene(content);

        window.show();

    }

    private MenuBar generateMenubar() {
        MenuBar menubar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem deleteItem = new MenuItem("Delete information");
        deleteItem.setOnAction(this::deleteSaves);
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());
        fileMenu.getItems().addAll(deleteItem, exitItem);

        Menu saveMenu = new Menu("Save method");
        ToggleGroup saveGroup = new ToggleGroup();
        RadioMenuItem fileItem = new RadioMenuItem("Filesystem");
        RadioMenuItem dropboxItem = new RadioMenuItem("Dropbox");
        RadioMenuItem databaseItem = new RadioMenuItem("Database");
        fileItem.setOnAction(e -> { saveMethod = Save.FILE; });
        dropboxItem.setOnAction(e -> { saveMethod = Save.DROPBOX; });
        databaseItem.setOnAction(e -> { saveMethod = Save.DATABASE; });
        fileItem.setSelected(true);
        saveGroup.getToggles().addAll(fileItem, dropboxItem, databaseItem);
        saveMenu.getItems().addAll(fileItem, dropboxItem, databaseItem);

        Menu helpMenu = new Menu("Help");
        MenuItem infoItem = new MenuItem("About");
        infoItem.setOnAction(this::showAboutWindow);
        helpMenu.getItems().addAll(infoItem);

        menubar.setPadding(new Insets(0,0,0,10));
        menubar.getMenus().addAll(fileMenu, saveMenu, helpMenu);
        return menubar;
    }

    private void showAboutWindow(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Thank you for using Shopping List!");
        alert.setContentText("2019 tiina.malinen@tuni.fi");

        alert.showAndWait();
    }

    private Button createLoadButton() {
        Button loadButton = new Button("Load list");
        loadButton.setOnAction(this::loadAction);
        return loadButton;
    }

    private Button createSaveButton() {
        Button saveButton = new Button("Save list");
        saveButton.setOnAction(this::saveAction);
        return saveButton;
    }

    private void loadAction(ActionEvent actionEvent) {
        switch (saveMethod) {
            case FILE:
                loadList(actionEvent);
                break;
            case DROPBOX:
                loadFromDropbox(actionEvent);
                break;
            case DATABASE:
                loadFromDatabase(actionEvent);
                break;
        }
    }

    private void saveAction(ActionEvent actionEvent) {
        switch (saveMethod) {
            case FILE:
                saveList(actionEvent);
                break;
            case DROPBOX:
                saveToDropbox(actionEvent);
                break;
            case DATABASE:
                saveToDatabase(actionEvent);
                break;
        }
    }

    private void loadFromDatabase(ActionEvent actionEvent) {
        H2Connect h2 = new H2Connect();
        List<Item> newItems = h2.fetchItems();

        parser.reset();
        for(Item item: newItems) {
            parser.addItem(item);
        }
        h2.close();

        parser.writeToFile();
        loadList(actionEvent);
    }

    private void saveToDatabase(ActionEvent actionEvent) {
        saveList(actionEvent);

        parser.reset();
        parser.readFromFile();
        List<Item> items = parser.returnAllItems().get();

        H2Connect h2 = new H2Connect();
        h2.removeAll();
        h2.saveItems(items);
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

    private void deleteSaves(ActionEvent actionEvent) {
        clearList(actionEvent);
        saveToDatabase(actionEvent);
        saveToDropbox(actionEvent);
    }

    private void loadFromDropbox(ActionEvent actionEvent) {
        if (dropbox == null) {
            connectToDropbox();
        }

        dropbox.readFile(SAVEFILENAME);
        loadList(actionEvent);
    }

    private void saveToDropbox(ActionEvent actionEvent) {
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
        refreshView();
    }

    private void loadList(ActionEvent actionEvent) {
        parser.reset();
        parser.readFromFile();
        int counter=0;
        grid = new GridPane();

        while (parser.areMoreItems()) {
            Optional<Item> item = parser.returnItem();
            if (item.isPresent()) {
                createNewLine(item.get());
            }
        }

        refreshView();
    }

    private void refreshView() {
        if(grid.getRowCount() > 10) {
            ScrollPane scroll = new ScrollPane();
            scroll.setContent(grid);
            scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            root.setCenter(scroll);
        }
        else {
            root.setCenter(grid);
        }
        grid.setPadding(new Insets(10,10,10,10));
    }

    private void saveList(ActionEvent actionEvent) {
        parser.reset();
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

    private GridPane createList() {
        grid = new GridPane();

        Item item = new Item("item", "amount");
        createNewLine(item);

        for (int i=0; i<4; i++) {
            createNewLine();
        }

        grid.setPadding(new Insets(10,10,10,10));
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

    public static void main(String args[]){
        launch(args);
    }
}
