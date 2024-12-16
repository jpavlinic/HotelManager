package PresentationLayer;

import BusinessLayer.Hotel;
import BusinessLayer.Location;
import BusinessLayer.User;
import DataPersistance.Database;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class EditList {
    private ObservableList<Hotel> hotelData;
    private TableView<Hotel> tableView;

    public void start(Stage primaryStage, Database db, User user) {

        if (user.getUserType().equals("Admin") || user.getUserType().equals("Listing")) {
            tableView = new TableView<>();
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            setupTableColumns();

            hotelData = loadHotels(db, user);

            tableView.setItems(hotelData);

            Button backButton = new Button("Back");
            backButton.setOnAction(event -> {
                UIFactory.createUI("list", primaryStage, db, user);
            });

            tableView.setRowFactory(tv -> {
                TableRow<Hotel> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        Hotel rowData = row.getItem();
                        openEditUI(primaryStage, db, user, rowData);
                    }
                });
                return row;
            });

            tableView.setRowFactory(tv -> {
                TableRow<Hotel> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        Hotel rowData = row.getItem();
                        openEditUI(primaryStage, db, user, rowData);
                    }
                });
                return row;
            });

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));
            layout.getChildren().addAll(tableView, backButton);

            Scene scene = new Scene(layout, 800, 600);
            primaryStage.setTitle("Edit Hotel Listings");
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            System.out.println("Unauthorized access");
        }

    }

    private ObservableList<Hotel> loadHotels(Database db, User user) {
        ObservableList<Hotel> data = FXCollections.observableArrayList();
        try {
            ArrayList<ArrayList<String>> rawData = new Hotel().searchWithLocationDetails(db, user);
            if (!rawData.isEmpty()) {
                rawData.remove(0);
                for (ArrayList<String> entry : rawData) {
                    String name = entry.get(1);
                    int hotelId = Integer.parseInt(entry.get(0));
                    int locationId = Integer.parseInt(entry.get(2));
                    int stars = Integer.parseInt(entry.get(3));
                    double reviewRating = Double.parseDouble(entry.get(4));
                    int numberOfRooms = Integer.parseInt(entry.get(5));
                    String country = entry.get(6);
                    String city = entry.get(7);
                    String zip = entry.get(8);
                    String address = entry.get(9);
                    Location location = new Location(locationId, country, city, zip, address);
                    Hotel hotel = new Hotel(name, locationId, location, stars, reviewRating, numberOfRooms);
                    hotel.setHotelId(hotelId);
                    data.add(hotel);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading hotels: " + e.getMessage());
        }
        return data;
    }

    private void setupTableColumns() {
        TableColumn<Hotel, String> nameCol = new TableColumn<>("Hotel Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Hotel, String> countryCol = new TableColumn<>("Country");
        countryCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getLocation().getCountry()));

        TableColumn<Hotel, String> cityCol = new TableColumn<>("City");
        cityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation().getCity()));

        TableColumn<Hotel, String> zipCol = new TableColumn<>("Zipcode");
        zipCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation().getZip()));

        TableColumn<Hotel, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getLocation().getAddress()));

        TableColumn<Hotel, Integer> starsCol = new TableColumn<>("Stars");
        starsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStars()).asObject());

        TableColumn<Hotel, Double> ratingCol = new TableColumn<>("Review Rating");
        ratingCol.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getReviewRating()).asObject());

        TableColumn<Hotel, Integer> roomsCol = new TableColumn<>("Number of Rooms");
        roomsCol.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getNumberOfRooms()).asObject());

        tableView.getColumns().addAll(nameCol, countryCol, cityCol, zipCol, addressCol, starsCol, ratingCol, roomsCol);
    }

    private void openEditUI(Stage primaryStage, Database db, User user, Hotel hotel) {
        List listUI = new List();
        listUI.startWithHotelData(primaryStage, db, user, hotel);
    }
}
