package PresentationLayer;

import BusinessLayer.User;
import DataPersistance.Database;
import javafx.stage.Stage;

public class UIFactory {

    public static void createUI(String uiType, Stage stage, Database db, User u) {
        switch (uiType.toLowerCase()) {
            case "login":
                new LogIn().start(stage, db);
                break;
            case "register":
                new Registration().start(stage, db);
                break;
            case "list":
                new List().start(stage, db, u);
                break;
            case "mainpage":
                new MainPage().start(stage, db, u);
                break;
            case "profile":
                new Profile().start(stage, db, u);
                break;
            case "search":
                new Search().start(stage, db, u);
                break;
            case "editlist":
                new EditList().start(stage, db, u);
                break;
            default:
                throw new IllegalArgumentException("Unknown UI type: " + uiType);
        }
    }
}
