package PresentationLayer;

import BusinessLayer.Hotel;
import BusinessLayer.HotelManager;
import BusinessLayer.Location;
import BusinessLayer.Payment;
import BusinessLayer.Reservation;
import BusinessLayer.Review;
import BusinessLayer.User;
import DataPersistance.DLException;
import DataPersistance.Database;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Search {

    ArrayList<ArrayList<String>> hotelData;
    TableView<HotelRow> tableView;
    ObservableList<HotelRow> data;

    public void start(Stage primaryStage, Database db, User u) {

        User user = u;

        if (user.getUserType().equals("Admin") || user.getUserType().equals("Regular")) {
            Hotel hotel = new Hotel();
            try {
                // SEARCH FUNCTIONAL REQUIREMENT
                hotelData = hotel.search(db);
            } catch (DLException e) {
                showAlert("Error occured ", e.getMessage());
                return;
            }

            BorderPane borderPane = new BorderPane();
            HBox searchCriteriaBoxRow1 = new HBox(10);
            searchCriteriaBoxRow1.setPadding(new Insets(10));
            searchCriteriaBoxRow1.setAlignment(Pos.CENTER_LEFT);
            searchCriteriaBoxRow1.setStyle("-fx-background-color: #d4ebf2;");

            Label nameLabel = new Label("Name:");
            TextField nameField = new TextField();
            Label numRoomsLabel = new Label("Number of Rooms:");
            Slider numRoomsSlider = new Slider(1, 50, 1);
            numRoomsSlider.setBlockIncrement(1);
            numRoomsSlider.setMajorTickUnit(10);
            numRoomsSlider.setShowTickMarks(true);
            numRoomsSlider.setShowTickLabels(true);
            numRoomsSlider.setSnapToTicks(true);

            Button profileBtn = new Button("Profile");

            profileBtn.setOnAction(e -> {
                UIFactory.createUI("Profile", primaryStage, db, user);
            });

            HBox numRoomsBox = new HBox(10);
            numRoomsBox.getChildren().addAll(numRoomsLabel, numRoomsSlider);

            searchCriteriaBoxRow1.getChildren().addAll(nameLabel, nameField,
                    numRoomsLabel, numRoomsBox, profileBtn);

            HBox searchCriteriaBoxRow2 = new HBox(10);
            searchCriteriaBoxRow2.setPadding(new Insets(10));
            searchCriteriaBoxRow2.setAlignment(Pos.CENTER_LEFT);
            searchCriteriaBoxRow2.setStyle("-fx-background-color: #d4ebf2;");

            Label reviewLabel = new Label("Review:");
            ComboBox<String> reviewsBox = new ComboBox<>();
            reviewsBox.getItems().addAll("1", "2", "3", "4", "5");
            reviewsBox.setValue("1");
            Label starsLabel = new Label("Stars:");
            ComboBox<String> stars = new ComboBox<>();
            stars.getItems().addAll("1", "2", "3", "4", "5");
            stars.setValue("1");

            searchCriteriaBoxRow2.getChildren().addAll(reviewLabel, reviewsBox,
                    starsLabel, stars);

            Button searchButton = new Button("Search");

            Label sortByLabel = new Label("Sort By:");
            ComboBox<String> sortByComboBox = new ComboBox<>();
            sortByComboBox.getItems().addAll("Stars", "Name", "Review");
            sortByComboBox.setValue("Stars");
            CheckBox ascendingCheckBox = new CheckBox("Ascending");
            Button sortButton = new Button("Sort");
            sortButton.setOnAction(e -> {
                String sortBy = "";
                switch (sortByComboBox.getValue()) {
                    case "Stars":
                        sortBy = "stars";
                        break;
                    case "Name":
                        sortBy = "name";
                        break;
                    case "Review":
                        sortBy = "reviewRating";
                        break;
                    default:
                        break;
                }

                // SORTED SEARCH FUNCTIONAL REQUIREMENT
                this.hotelData = hotel.sortedSearch(hotelData, sortBy, ascendingCheckBox.isSelected());

                tableView.getItems().clear();
                for (int i = 1; i < hotelData.size(); i++) {
                    ArrayList<String> row = hotelData.get(i);
                    Location loc = new Location();
                    ArrayList<String> fullLocation = null;
                    try {
                        fullLocation = loc.fetchLocation(db, row.get(2));
                    } catch (DLException e1) {
                        showAlert("Error occured ", e1.getMessage());
                    }
                    String location = fullLocation.get(2) + ", " + fullLocation.get(1);
                    HotelRow hRow = new HotelRow(row.get(1), location, row.get(3), row.get(4), row.get(5));
                    hRow.setHotelId(Integer.parseInt(row.get(0)));
                    data.add(hRow);
                }
            });

            searchButton.setOnAction(e -> {
                try {
                    // FILTERED SEARCH FUNCTIONAL REQUIREMENT
                    this.hotelData = hotel.filteredSearch(db, nameField.getText(), null,
                            stars.getValue(), reviewsBox.getValue(), String.valueOf(numRoomsSlider.getValue()));
                    tableView.getItems().clear();
                    for (int i = 1; i < hotelData.size(); i++) {
                        ArrayList<String> row = hotelData.get(i);
                        Location loc = new Location();
                        ArrayList<String> fullLocation = null;
                        try {
                            fullLocation = loc.fetchLocation(db, row.get(2));
                        } catch (DLException e1) {
                            showAlert("Error occured ", e1.getMessage());
                        }
                        String location = fullLocation.get(2) + ", " + fullLocation.get(1);
                        HotelRow hRow = new HotelRow(row.get(1), location, row.get(3), row.get(4), row.get(5));
                        hRow.setHotelId(Integer.parseInt(row.get(0)));
                        data.add(hRow);
                    }
                } catch (DLException e1) {
                    showAlert("Error occured ", e1.getMessage());
                }
            });

            searchCriteriaBoxRow2.getChildren().addAll(sortByLabel, sortByComboBox, ascendingCheckBox, sortButton);

            tableView = new TableView<>();

            TableColumn<HotelRow, String> nameColumn = new TableColumn<>("Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<HotelRow, String> locationColumn = new TableColumn<>("Location");
            locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

            TableColumn<HotelRow, String> starsColumn = new TableColumn<>("Stars");
            starsColumn.setCellValueFactory(new PropertyValueFactory<>("stars"));

            TableColumn<HotelRow, String> ratingColumn = new TableColumn<>("Rating");
            ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

            TableColumn<HotelRow, String> roomsColumn = new TableColumn<>("Rooms");
            roomsColumn.setCellValueFactory(new PropertyValueFactory<>("rooms"));

            tableView.getColumns().addAll(nameColumn, locationColumn, starsColumn, ratingColumn, roomsColumn);

            data = FXCollections.observableArrayList();
            for (int i = 1; i < hotelData.size(); i++) {
                ArrayList<String> row = hotelData.get(i);
                Location loc = new Location();
                ArrayList<String> fullLocation = null;
                try {
                    fullLocation = loc.fetchLocation(db, row.get(2));
                } catch (DLException e1) {
                    showAlert("Error occured ", e1.getMessage());
                }
                String location = fullLocation.get(2) + ", " + fullLocation.get(1);
                HotelRow hRow = new HotelRow(row.get(1), location, row.get(3), row.get(4), row.get(5));
                hRow.setHotelId(Integer.parseInt(row.get(0)));
                data.add(hRow);
            }

            tableView.setItems(data);

            VBox searchCriteriaBox = new VBox(10);
            searchCriteriaBox.getChildren().addAll(searchCriteriaBoxRow1, searchCriteriaBoxRow2);
            borderPane.setTop(searchCriteriaBox);
            searchCriteriaBox.setStyle("-fx-background-color: #d4ebf2;");

            searchCriteriaBox.getChildren().add(searchButton);
            searchCriteriaBox.getChildren().add(tableView);

            if (user.getUserType().equals("Admin")) {
                Button backButton = new Button("Back");
                backButton.setOnAction(event -> {
                    MainPage mainPage = new MainPage();
                    mainPage.start(primaryStage, db, user);
                });

                searchCriteriaBox.getChildren().add(backButton);
            }

            searchCriteriaBox.setAlignment(Pos.CENTER);
            VBox.setMargin(searchButton, new Insets(10, 0, 0, 0));

            tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    Stage reserveStage = new Stage();
                    BorderPane root = new BorderPane();

                    VBox resBox = new VBox(10);
                    resBox.setPadding(new Insets(10));
                    resBox.setAlignment(Pos.TOP_LEFT);
                    Label reservation = new Label("Reservation");
                    DatePicker checkIn = new DatePicker();
                    DatePicker checkOut = new DatePicker();
                    Label roomNum = new Label("Room Number:");
                    ComboBox<Integer> roomNumber = new ComboBox<>();
                    for (int i = 1; i <= 50; i++) {
                        roomNumber.getItems().add(i);
                    }
                    roomNumber.setValue(1);
                    Button reserveBtn = new Button("Reserve");
                    resBox.getChildren().addAll(reservation, checkIn, checkOut,
                            roomNum, roomNumber, reserveBtn);

                    reserveBtn.setOnAction(event -> {
                        Reservation res = new Reservation();
                        try {
                            // RESERVE FUNCTIONAL REQUREMENT
                            boolean success = res.reserve(db, newSelection.getHotelId(), user.getUsername(),
                                    checkIn.getValue(),
                                    checkOut.getValue(), roomNumber.getValue());
                            if (success) {
                                Alert alert = new Alert(AlertType.INFORMATION);
                                alert.setTitle("Success");
                                alert.setHeaderText(null);
                                alert.setContentText("Reservation successful!");
                                alert.showAndWait();
                            } else {
                                Alert alert = new Alert(AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText(null);
                                alert.setContentText("Reservation failed!");
                                alert.showAndWait();
                            }
                        } catch (DLException e1) {
                            showAlert("Error occured ", e1.getMessage());
                        }
                    });

                    VBox payment = new VBox(10);
                    payment.setPadding(new Insets(10));
                    payment.setAlignment(Pos.TOP_RIGHT);
                    Label paymentLbl = new Label("Payment Option");

                    TextArea cardNum = new TextArea();
                    cardNum.setPromptText("Credit Card Number");

                    DatePicker expDate = new DatePicker();
                    expDate.setPromptText("Expiration Date");

                    TextArea cvv = new TextArea();
                    cvv.setPromptText("CVV");

                    TextArea nameArea = new TextArea();
                    nameArea.setPromptText("Full Name");

                    Button payBtn = new Button("Add Payment Option");
                    payment.getChildren().addAll(paymentLbl, cardNum, expDate, cvv, nameArea, payBtn);

                    payBtn.setOnAction(event -> {
                        Payment pay = new Payment();

                        // PAYMENT FUNCTIONAL REQUIREMENT
                        boolean success = pay.insertEncryptedPayment(db.getConn(), user.getUsername(),
                                cardNum.getText().trim(), expDate.getValue(), Integer.parseInt(cvv.getText().trim()),
                                nameArea.getText().trim());
                        if (success) {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText(null);
                            alert.setContentText("Payment successfully added");
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("Problems with adding your payment");
                            alert.showAndWait();
                        }

                    });

                    ComboBox<Integer> reviewRatings = new ComboBox<>(
                            FXCollections.observableArrayList(0, 1, 2, 3, 4, 5));

                    VBox reviews = new VBox(10);
                    reviews.setPadding(new Insets(10));
                    reviews.setAlignment(Pos.BOTTOM_LEFT);

                    Label reviewsLbl = new Label("Reviews");
                    TextArea revs = new TextArea();
                    revs.setPrefRowCount(10);
                    revs.setEditable(false);
                    TextArea writeRev = new TextArea("Write a Review");
                    writeRev.setPrefRowCount(3);
                    Button postBtn = new Button("Post");

                    reviews.getChildren().addAll(reviewsLbl, revs, writeRev, reviewRatings, postBtn);

                    Review review = new Review();
                    try {
                        // SHOW REVIEWS FUNCTIONAL REQUIREMENT
                        review.showReviews(db, newSelection.getHotelId(), revs);
                    } catch (DLException e1) {
                        showAlert("Error occured ", e1.getMessage());
                    }

                    postBtn.setOnAction(event -> {
                        try {
                            // ENTER REVIEW FUNCTIONAL REQUIREMENT
                            review.enterReview(db, newSelection.getHotelId(), user.getUsername(),
                                    reviewRatings.getValue(),
                                    writeRev.getText().trim());
                            review.showReviews(db, newSelection.getHotelId(), revs);
                        } catch (DLException e1) {
                            showAlert("Error occured ", e1.getMessage());
                        }

                    });

                    VBox amenities = new VBox(10);
                    amenities.setPadding(new Insets(10));
                    amenities.setAlignment(Pos.BOTTOM_LEFT);
                    Label amenitiesLbl = new Label("Hotel Amenities");
                    TextArea amenitiesTA = new TextArea();
                    amenitiesTA.setPrefRowCount(5);
                    amenitiesTA.setEditable(false);
                    amenities.getChildren().addAll(amenitiesLbl, amenitiesTA);

                    HotelManager hManager = new HotelManager(db);
                    try {
                        // SHOW AMENITIES FUNCTIONAL REQUIREMENT
                        hManager.getTopAmenities(newSelection.getHotelId(), amenitiesTA);
                    } catch (DLException e1) {
                        showAlert("Error occured ", e1.getMessage());
                    }

                    root.setPadding(new Insets(10));
                    root.setTop(resBox);
                    root.setRight(payment);
                    root.setLeft(reviews);
                    root.setBottom(amenities);

                    Scene scene = new Scene(root, 1200, 800);
                    reserveStage.setScene(scene);
                    reserveStage.setTitle("Reserve Hotel");
                    reserveStage.show();
                }
            });

            Scene scene = new Scene(borderPane, 1000, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Search Hotels");
            primaryStage.show();
        } else {
            System.out.println("Unauthorized access");
        }

    }

    // helper class for table row
    public static class HotelRow {
        private int hotelId;
        private final String name;
        private final String location;
        private final String stars;
        private final String rating;
        private final String rooms;

        public HotelRow(String name, String location, String stars, String rating, String rooms) {
            this.name = name;
            this.location = location;
            this.stars = stars;
            this.rating = rating;
            this.rooms = rooms;
        }

        public int getHotelId() {
            return hotelId;
        }

        public void setHotelId(int hotelId) {
            this.hotelId = hotelId;
        }

        public String getName() {
            return name;
        }

        public String getLocation() {
            return location;
        }

        public String getStars() {
            return stars;
        }

        public String getRating() {
            return rating;
        }

        public String getRooms() {
            return rooms;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
