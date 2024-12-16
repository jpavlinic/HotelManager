package PresentationLayer;

import BusinessLayer.Hotel;
import BusinessLayer.HotelManager;
import BusinessLayer.User;
import DataPersistance.DLException;
import DataPersistance.Database;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class List {

    private Database db;
    private Hotel selectedHotel;

    public List() {
        // Empty constructor required by JavaFX
    }

    public void start(Stage primaryStage, Database db, User u) {
        this.db = db;
        User user = u;

        if (user.getUserType().equals("Admin") || user.getUserType().equals("Listing")) {
            VBox listLayout = new VBox(10);
            listLayout.setPadding(new Insets(20));
            listLayout.setAlignment(Pos.CENTER);

            Label titleLabel = new Label("List Your Hotel");
            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            Button editButton = new Button("Edit");
            editButton.setOnAction(event -> {
                UIFactory.createUI("editlist", primaryStage, db, user);
            });

            TextField nameField = new TextField();
            nameField.setPromptText("Enter Hotel Name");

            TextField countryField = new TextField();
            countryField.setPromptText("Country");

            TextField cityField = new TextField();
            cityField.setPromptText("City");

            TextField zipField = new TextField();
            zipField.setPromptText("ZIP Code");

            TextField addressField = new TextField();
            addressField.setPromptText("Address");

            TextArea amenitiesArea = new TextArea();
            amenitiesArea.setPromptText("Enter Amenities");

            Spinner<Integer> starsSpinner = new Spinner<>(1, 5, 3);
            starsSpinner.setEditable(true);

            Slider reviewRatingSlider = new Slider(0, 5, 2.5);
            reviewRatingSlider.setShowTickLabels(true);
            reviewRatingSlider.setShowTickMarks(true);
            reviewRatingSlider.setMajorTickUnit(1);
            reviewRatingSlider.setBlockIncrement(0.1);
            reviewRatingSlider.setSnapToTicks(true);

            Spinner<Integer> numberOfRoomsSpinner = new Spinner<>(1, 1000, 10);
            numberOfRoomsSpinner.setEditable(true);

            Button submitButton = new Button("Submit");
            submitButton.setOnAction(event -> {
                HotelManager hotelManager = new HotelManager(db);
                double reviewRating = Math.round(reviewRatingSlider.getValue() * 100.0) / 100.0;
                try {
                    int hotelId = hotelManager.hotelListing(
                            nameField.getText(),
                            countryField.getText(),
                            cityField.getText(),
                            zipField.getText(),
                            addressField.getText(),
                            starsSpinner.getValue(),
                            reviewRating,
                            numberOfRoomsSpinner.getValue(),
                            user);
                    boolean success = hotelManager.hotelAmenities(amenitiesArea.getText(), Integer.toString(hotelId));
                    if (success) {
                        showAlert("Success", "Hotel data saved successfully!");
                    } else {
                        showAlert("Error", "Failed to save hotel information.");
                    }
                } catch (Exception e) {
                    showAlert("Error occured", e.getMessage());
                }
            });

            listLayout.getChildren().addAll(
                    titleLabel,
                    new Label("Hotel Name"), nameField,
                    new Label("Country"), countryField,
                    new Label("City"), cityField,
                    new Label("ZIP Code"), zipField,
                    new Label("Address"), addressField,
                    new Label("Stars"), starsSpinner,
                    new Label("Review Rating"), reviewRatingSlider,
                    new Label("Number of Rooms"), numberOfRoomsSpinner,
                    new Label("Hotel Amenities: "), amenitiesArea,
                    submitButton,

                    editButton);

            if (user.getUserType().equals("Admin")) {
                Button backButton = new Button("Back");
                backButton.setOnAction(event -> {
                    MainPage mainPage = new MainPage();
                    mainPage.start(primaryStage, db, user);
                });

                listLayout.getChildren().add(backButton);
            }

            Scene scene = new Scene(listLayout, 400, 800);
            primaryStage.setTitle("Edit Hotel");
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            System.out.println("Unauthorized access");
        }

    }

    public void startWithHotelData(Stage primaryStage, Database db, User user, Hotel hotel) {
        this.db = db;
        this.selectedHotel = hotel;

        VBox listLayout = new VBox(10);
        listLayout.setPadding(new Insets(20));
        listLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Edit Your Hotel");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField nameField = new TextField(hotel.getName());
        TextField countryField = new TextField(hotel.getLocation().getCountry());
        TextField cityField = new TextField(hotel.getLocation().getCity());
        TextField zipField = new TextField(hotel.getLocation().getZip());
        TextField addressField = new TextField(hotel.getLocation().getAddress());
        Spinner<Integer> starsSpinner = new Spinner<>(1, 5, hotel.getStars());
        starsSpinner.setEditable(true);
        Slider reviewRatingSlider = new Slider(0, 5, hotel.getReviewRating());
        reviewRatingSlider.setShowTickLabels(true);
        reviewRatingSlider.setShowTickMarks(true);
        reviewRatingSlider.setMajorTickUnit(1);
        reviewRatingSlider.setBlockIncrement(0.1);
        reviewRatingSlider.setSnapToTicks(true);
        Spinner<Integer> numberOfRoomsSpinner = new Spinner<>(1, 1000, hotel.getNumberOfRooms());
        numberOfRoomsSpinner.setEditable(true);

        HotelManager hotelManager = new HotelManager(db);
        String ameneties = "";
        try {
            ameneties = hotelManager.getTopAmenities(selectedHotel.getHotelId(), null);
        } catch (DLException e) {
            showAlert("Database Error", "Failed to save hotel information: " + e.getMessage());
        }
        TextField amenetiesField = new TextField(ameneties);

        Button submitButton = new Button("Update");
        submitButton.setOnAction(event -> {
            double reviewRating = Math.round(reviewRatingSlider.getValue() * 100.0) / 100.0;
            try {
                boolean success = hotelManager.modifyHotelListing(
                        selectedHotel.getHotelId(),
                        nameField.getText(),
                        countryField.getText(),
                        cityField.getText(),
                        zipField.getText(),
                        addressField.getText(),
                        starsSpinner.getValue(),
                        reviewRating,
                        numberOfRoomsSpinner.getValue());

                boolean amenetiesSuccess = hotelManager.modifyAmeneties(selectedHotel.getHotelId(),
                        amenetiesField.getText());
                if (success && amenetiesSuccess) {
                    showAlert("Success", "Hotel data updated successfully!");
                } else {
                    showAlert("Error", "Failed to update hotel information.");
                }
            } catch (Exception e) {
                showAlert("Database Error", "Failed to update hotel information: " + e.getMessage());
            }

            UIFactory.createUI("List", primaryStage, db, user);
        });

        Label nameLabel = new Label("Hotel Name");
        Label countryLabel = new Label("Country");
        Label cityLabel = new Label("City");
        Label zipLabel = new Label("ZIP Code");
        Label addressLabel = new Label("Address");
        Label starsLabel = new Label("Stars");
        Label reviewRatingLabel = new Label("Review Rating");
        Label numberOfRoomsLabel = new Label("Number of Rooms");
        Label amenLbl = new Label("Hotel Amenities: ");

        listLayout.getChildren().addAll(
                titleLabel,
                nameLabel, nameField,
                countryLabel, countryField,
                cityLabel, cityField,
                zipLabel, zipField,
                addressLabel, addressField,
                starsLabel, starsSpinner,
                reviewRatingLabel, reviewRatingSlider,
                numberOfRoomsLabel, numberOfRoomsSpinner, amenLbl, amenetiesField,
                submitButton);

        Scene scene = new Scene(listLayout, 400, 700);
        primaryStage.setTitle("Edit Hotel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
