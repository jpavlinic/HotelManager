package PresentationLayer;

import BusinessLayer.User;
import DataPersistance.Database;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainPage {

    public void start(Stage primaryStage, Database db, User u) {

        if (u.getUserType().equals("Admin")) {
            VBox mainLayout = new VBox(10);
            mainLayout.setPadding(new Insets(20));
            mainLayout.setAlignment(Pos.CENTER);

            Label welcomeLabel = new Label("Welcome to Hotel Reservation System");
            welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            Button searchButton = new Button("Search Hotels");
            Button listButton = new Button("List Your Hotel");
            Button profileButton = new Button("Profile");

            searchButton.setOnAction(e -> UIFactory.createUI("search", primaryStage, db, u));
            listButton.setOnAction(e -> UIFactory.createUI("list", primaryStage, db, u));
            profileButton.setOnAction(e -> UIFactory.createUI("profile", primaryStage, db, u));

            mainLayout.getChildren().addAll(welcomeLabel, searchButton, listButton, profileButton);

            Scene scene = new Scene(mainLayout, 400, 300);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Hotel App");
            primaryStage.show();
        } else {
            System.out.println("Unauthorized access");
        }

    }
}
