package PresentationLayer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import DataPersistance.DLException;
import DataPersistance.Database;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainUI extends Application {

    static Database db;
    private String localhost;
    private String port;
    private String databaseName;
    private String username;
    private String password;
    private BufferedReader fileReader;

    @Override
    public void start(Stage primaryStage) {

        try {
            fileReader = new BufferedReader(new FileReader("databaseInfo.txt"));
            String line = "";
            int counter = 0;
            while ((line = fileReader.readLine()) != null) {
                switch (counter) {
                    case 0:
                        localhost = line;
                        break;
                    case 1:
                        port = line;
                        break;
                    case 2:
                        databaseName = line;
                        break;
                    case 3:
                        username = line;
                        break;
                    case 4:
                        password = line;
                        break;
                    default:
                        break;
                }
                counter++;
            }
            String url = "jdbc:mysql://" + localhost + ":" + port + "/" + databaseName;
            db = new Database(url, username, password);
            db.establishDatabaseConnection();
            System.out.println(db.getConn());

        } catch (FileNotFoundException e) {
            showAlert("Error occured ", e.getMessage());
        } catch (IOException e) {
            showAlert("Error occured ",  e.getMessage());
        } catch (DLException e) {
            showAlert("Error occured ", e.getMessage());
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                showAlert("Error occured ", e.getMessage());
            }
        }

        try {
            UIFactory.createUI("login", primaryStage, db, null);
        } catch (Exception e) {
            showAlert("Error occured ", e.getMessage());
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
