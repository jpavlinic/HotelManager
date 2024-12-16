package PresentationLayer;

import BusinessLayer.User;
import DataPersistance.Database;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LogIn {

    Label firstFieldLabel, secondFieldLabel;

    public void start(Stage primaryStage, Database db) {
        User user = new User();
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);

        GridPane loginForm = new GridPane();
        loginForm.setHgap(10);
        loginForm.setVgap(10);
        loginForm.setPadding(new Insets(10));
        Label loginTitle = new Label("Login");
        TextField usernameField = new TextField();
        usernameField.setStyle("-fx-border-color: grey; -fx-border-width: 1px; -fx-border-style: solid;");
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-border-color: grey; -fx-border-width: 1px; -fx-border-style: solid;");
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        // invisible labels
        firstFieldLabel = new Label();
        secondFieldLabel = new Label();
        firstFieldLabel.setVisible(false);
        secondFieldLabel.setVisible(false);
        firstFieldLabel.setStyle("-fx-text-fill: red;");
        secondFieldLabel.setStyle("-fx-text-fill: red;");

        registerButton.setOnAction(e -> {
            UIFactory.createUI("register", primaryStage, db, null);
        });

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            boolean isUsernameEmpty = username.isEmpty();
            boolean isPasswordEmpty = password.isEmpty();

            usernameField.setStyle(isUsernameEmpty ? "-fx-border-color: red;" : "-fx-border-color: grey;");
            passwordField.setStyle(isPasswordEmpty ? "-fx-border-color: red;" : "-fx-border-color: grey;");

            setLabelVisible(firstFieldLabel, isUsernameEmpty ? "Username field is empty" : null);
            setLabelVisible(secondFieldLabel, isPasswordEmpty ? "Password field is empty" : null);

            if (!isUsernameEmpty && !isPasswordEmpty) {
                user.setUsername(username);
                user.setPassword(password);
                if (user.login(db.getConn())) {
                    if (user.getUserType().equals("Listing")) {
                        UIFactory.createUI("List", primaryStage, db, user);
                    } else if (user.getUserType().equals("Regular")) {
                        UIFactory.createUI("Search", primaryStage, db, user);
                    } else if (user.getUserType().equals("Admin")) {
                        UIFactory.createUI("mainpage", primaryStage, db, user);
                    }
                } else {
                    setLabelVisible(secondFieldLabel, "Log in information incorrect");
                }
            }
        });

        loginForm.addRow(0, new Label("Username:"), usernameField);
        loginForm.add(firstFieldLabel, 1, 1);
        loginForm.addRow(2, new Label("Password:"), passwordField);
        loginForm.add(secondFieldLabel, 1, 3);
        loginForm.add(loginButton, 1, 4);

        mainLayout.getChildren().addAll(loginTitle, loginForm, registerButton);

        Scene scene = new Scene(mainLayout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    private static void setLabelVisible(Label label, String text) {
        label.setText(text);
        label.setVisible(true);
    }
}
