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

public class Registration{

    public void start(Stage primaryStage, Database db) {
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);

        GridPane registrationForm = new GridPane();
        registrationForm.setHgap(10);
        registrationForm.setVgap(10);
        registrationForm.setPadding(new Insets(10));
        Label registrationTitle = new Label("User Registration");
        TextField nameField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<String> accountTypeComboBox = new ComboBox<>();
        accountTypeComboBox.getItems().addAll("Regular", "Listing");
        Button registerButton = new Button("Register");
        registrationForm.addRow(0, new Label("Name:"), nameField);
        registrationForm.addRow(1, new Label("Username:"), usernameField);
        registrationForm.addRow(2, new Label("Password:"), passwordField);
        registrationForm.addRow(3, new Label("Account Type:"), accountTypeComboBox);
        registrationForm.add(registerButton, 1, 6);

        registerButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String accType = accountTypeComboBox.getSelectionModel().getSelectedItem();

            boolean nameEmpty = name.isEmpty();
            boolean usernameEmpty = username.isEmpty();
            boolean passwordEmpty = password.isEmpty();
            boolean accTypeEmpty = accType == null || accType.isEmpty();

            nameField.setStyle(nameEmpty ? "-fx-border-color: red;" : "-fx-border-color: grey;");
            usernameField.setStyle(usernameEmpty ? "-fx-border-color: red;" : "-fx-border-color: grey;");
            passwordField.setStyle(passwordEmpty ? "-fx-border-color: red;" : "-fx-border-color: grey;");
            accountTypeComboBox.setStyle(accTypeEmpty ? "-fx-border-color: red;" : "-fx-border-color: grey;");
            if(!nameEmpty && !usernameEmpty && !passwordEmpty && !accTypeEmpty){
                User user = new User(username, password, name, accType);
                user.register(db.getConn());
                UIFactory.createUI("Login", primaryStage, db, null);
            }
        });
        
        mainLayout.getChildren().addAll(registrationTitle, registrationForm);
        mainLayout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainLayout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hotel Reservation App");
        primaryStage.show();
    }
}
