package fi.tuni.tiko.objectorientedprogramming;

import com.dropbox.core.*;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.json.JsonReader;
import javafx.application.Application;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;

public class DropboxConnection {
    private static String ACCESS_TOKEN;
    private String ACCESS_TOKEN_FILE = "token.app";
    private String filename = "test.txt";
    private String keyFile = "dropbox.app";
    private TextInputDialog inputDialog;

    public DropboxConnection() {

    }

    public void connect() {
        File tokenFile = new File(ACCESS_TOKEN_FILE);
        if (tokenFile.exists() && !tokenFile.isDirectory()) {

            try {
                List<String> lines = Files.readAllLines(Paths.get(filename), Charset.defaultCharset());
                ACCESS_TOKEN = lines.get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            try {
                authenticate();
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void setInputDialog(TextInputDialog newInputDialog) {
        inputDialog = newInputDialog;
    }

    public static void save(String filename) throws DbxException, IOException {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("JSONparser").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        try (InputStream in = new FileInputStream("test.txt")) {
            FileMetadata metadata = client.files().uploadBuilder("/test.txt")
                    .uploadAndFinish(in);
        }
    }

    public void readFile() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("JSONparser").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        try {
            //output file for download --> storage location on local system to download file
            FileOutputStream downloadFile = new FileOutputStream(filename);
            try {
                FileMetadata metadata = client.files().downloadBuilder("/test.txt").download(downloadFile);
            } finally
            {
                downloadFile.close();
            }
        } catch (DbxException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }

        System.out.println("reading succeeded");
    }

    public void authenticate() throws IOException {
        String argAuthFileOutput = "accesstoken.json";

        // Read app info file (contains app key and app secret)
        DbxAppInfo appInfo;
        try {
            appInfo = DbxAppInfo.Reader.readFromFile(keyFile);
        } catch (JsonReader.FileLoadException ex) {
            System.err.println("Error reading <app-info-file>: " + ex.getMessage());
            System.exit(1); return;
        }

        // Run through Dropbox API authorization process
        DbxRequestConfig requestConfig = new DbxRequestConfig("examples-authorize");
        DbxWebAuth webAuth = new DbxWebAuth(requestConfig, appInfo);
        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                .withNoRedirect()
                .build();

        String authorizeUrl = webAuth.authorize(webAuthRequest);
        Optional<String> code = dialog(authorizeUrl);
        if (!code.isPresent() || code.get().trim().length() < 3 ) {
            System.exit(1);
        }

        DbxAuthFinish authFinish;
        try {
            authFinish = webAuth.finishFromCode(code.get().trim());
        } catch (DbxException ex) {
            System.err.println("Error in DbxWebAuth.authorize: " + ex.getMessage());
            System.exit(1); return;
        }

        System.out.println("Authorization complete.");
        ACCESS_TOKEN = authFinish.getAccessToken();

        try {
            List<String> lines = new LinkedList<>();
            lines.add(ACCESS_TOKEN);
            Files.write(Paths.get(ACCESS_TOKEN_FILE), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {e.printStackTrace();}

    }

    private Optional<String> dialog(String url) {
        inputDialog.setTitle("Dropbox Authentication");
        TextArea linkField = new TextArea("1. Go to\n" +url
                +"\n 2. Click \"Allow\" (you might have to log in first)."
                +"\n 3. Copy the authorization code.");
        linkField.setEditable(false);
        linkField.setWrapText(true);
        linkField.setPrefHeight(100);
        GridPane pane = new GridPane();
        pane.add(linkField, 0, 0);
        inputDialog.getDialogPane().setHeader(pane);
        inputDialog.setContentText("Enter the authorization code here: ");

        Optional<String> result = inputDialog.showAndWait();
        return result;
    }
}