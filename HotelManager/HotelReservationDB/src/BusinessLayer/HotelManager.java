package BusinessLayer;

import DataPersistance.Database;
import javafx.scene.control.TextArea;
import DataPersistance.DLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manages hotel-related operations including adding hotels, updating hotel
 * details,
 * and retrieving hotel amenities.
 */
public class HotelManager {

    private Database db;

    /**
     * Constructs a new HotelManager with a database connection.
     *
     * @param db The Database object for database operations.
     */
    public HotelManager(Database db) {
        this.db = db;
    }

    /**
     * Adds a new hotel along with its location to the database.
     *
     * @param name          The name of the hotel.
     * @param country       The country where the hotel is located.
     * @param city          The city where the hotel is located.
     * @param zip           The ZIP code of the hotel's location.
     * @param address       The address of the hotel.
     * @param stars         The star rating of the hotel.
     * @param numberOfRooms The total number of rooms in the hotel.
     * @return true if the hotel was successfully added, false otherwise.
     * @throws DLException if a database operation fails.
     */
    public int hotelListing(String name, String country, String city, String zip, String address, int stars,
            double value, int numberOfRooms, User user) throws DLException {
        try {
            db.startTrans();

            int locationId = getLocationId(country, city, zip, address);
            if (locationId == -1) {
                String insertLocationSQL = "INSERT INTO Location (Country, City, zip, address) VALUES (?, ?, ?, ?)";
                ArrayList<String> locationValues = new ArrayList<>(Arrays.asList(country, city, zip, address));
                locationId = db.insertWithKey(insertLocationSQL, locationValues);
                if (locationId == -1) {
                    db.rollbackTrans();
                    return -1;
                }
            }

            String insertHotelSQL = "INSERT INTO Hotel (name, location_id, stars, reviewRating, numberOfRooms, posted_by) VALUES (?, ?, ?, ?, ?, ?)";
            ArrayList<String> hotelValues = new ArrayList<>(Arrays.asList(name, String.valueOf(locationId),
                    String.valueOf(stars), String.valueOf(value), String.valueOf(numberOfRooms), user.getUsername()));
            int hotelId = db.insertWithKey(insertHotelSQL, hotelValues);

            if (hotelId == -1) {
                db.rollbackTrans();
                return -1;
            }

            db.endTrans();
            return hotelId;
        } catch (DLException e) {
            db.rollbackTrans();
            throw e;
        }
    }

    public boolean hotelAmenities(String amenities, String hotelId) throws DLException {
        try {
            db.startTrans();

            String insertAmenitySQL = "INSERT INTO HotelAmenities (hotel_id, amenitiesDescription) VALUES (?, ?)";
            ArrayList<String> values = new ArrayList<>();
            values.add(hotelId);
            values.add(amenities);
            boolean amenityInsertSuccess = db.setData(insertAmenitySQL, values);
            if (!amenityInsertSuccess) {
                db.rollbackTrans();
                return false;
            }

            db.endTrans();
            return true;
        } catch (DLException e) {
            db.rollbackTrans();
            throw e;
        }
    }

    public boolean modifyHotelListing(int hotelId, String name, String country, String city, String zip, String address,
            int stars, double reviewRating, int numberOfRooms) throws DLException {
        if (hotelId <= 0) {
            System.out.println("Invalid or missing hotel ID.");
            return false;
        }
        try {
            db.startTrans();
            int locationId = getLocationId(country, city, zip, address);
            if (locationId == -1) {
                System.out.println("No location found, creating new location.");
                String insertLocationSQL = "INSERT INTO Location (Country, City, zip, address) VALUES (?, ?, ?, ?)";
                ArrayList<String> locationValues = new ArrayList<>(Arrays.asList(country, city, zip, address));
                locationId = db.insertWithKey(insertLocationSQL, locationValues);
                if (locationId == -1) {
                    System.out.println("Failed to create new location, aborting update.");
                    db.rollbackTrans();
                    return false;
                }
            }

            String query = "UPDATE Hotel SET name = ?, location_id = ?, stars = ?, reviewRating = ?, numberOfRooms = ? WHERE hotel_id = ?";
            ArrayList<String> params = new ArrayList<>(Arrays.asList(name, String.valueOf(locationId),
                    String.valueOf(stars), String.format("%.2f", reviewRating), String.valueOf(numberOfRooms),
                    String.valueOf(hotelId)));

            boolean success = db.setData(query, params);
            if (!success) {
                System.out.println("Update operation failed, no rows affected.");
                db.rollbackTrans();
                return false;
            }

            db.endTrans();
            System.out.println("Update successful.");
            return true;
        } catch (Exception e) {
            System.out.println("Error during update operation: " + e.getMessage());
            db.rollbackTrans();
            return false;
        }
    }

    private int getLocationId(String country, String city, String zip, String address) throws DLException {
        country = country.trim().toUpperCase();
        city = city.trim().toUpperCase();
        zip = zip.trim();
        address = address.trim().toUpperCase();

        String query = "SELECT location_id FROM Location WHERE Country = ? AND City = ? AND zip = ? AND address = ?";
        ArrayList<String> values = new ArrayList<>(Arrays.asList(country, city, zip, address));
        ArrayList<ArrayList<String>> results = db.getData(query, values, false);
        if (results.isEmpty()) {
            System.out.println("No matching location found for values: " + values);
            return -1;
        }
        int locationId = Integer.parseInt(results.get(0).get(0));
        return locationId;
    }

    /**
     * Updates the details of an existing hotel in the database.
     *
     * @param hotelId          The ID of the hotel to update.
     * @param newName          The new name of the hotel.
     * @param newStars         The new star rating of the hotel.
     * @param newNumberOfRooms The new total number of rooms in the hotel.
     * @return true if the hotel details were successfully updated, false otherwise.
     * @throws DLException if a database operation fails.
     */
    public boolean modifyListing(int hotelId, String newName, int newStars, int newNumberOfRooms) throws DLException {
        try {
            db.startTrans();

            String updateSQL = "UPDATE Hotel SET name = ?, stars = ?, numberOfRooms = ? WHERE hotel_id = ?";
            ArrayList<String> values = new ArrayList<>(Arrays.asList(newName, String.valueOf(newStars),
                    String.valueOf(newNumberOfRooms), String.valueOf(hotelId)));
            boolean updateSuccess = db.setData(updateSQL, values);

            if (!updateSuccess) {
                db.rollbackTrans();
                return false;
            }

            db.endTrans();
            return true;
        } catch (DLException e) {
            try {
                db.rollbackTrans();
            } catch (DLException rollbackEx) {
                throw e;
            }
            throw e;
        }
    }

    /**
     * Retrieves the amenities description for a specified hotel.
     *
     * @param hotelId The ID of the hotel for which to retrieve amenities.
     * @return The amenities description of the specified hotel, or a message
     *         indicating no description was found.
     * @throws DLException if a database operation fails or the hotel ID is not
     *                     found.
     */
    public String getTopAmenities(int hotelId, TextArea textArea) throws DLException {
        String query = "SELECT amenitiesDescription FROM HotelAmenities WHERE hotel_id = ?";
        ArrayList<String> values = new ArrayList<>();
        values.add(String.valueOf(hotelId));

        ArrayList<ArrayList<String>> data = db.getData(query, values, true);
        if (!data.isEmpty() && data.size() > 1) {
            if (textArea != null) {
                textArea.setText(data.get(1).get(0));
            }
        } else {
            if (textArea != null) {
                textArea.setText("No amenities description found.");
            }
        }
        return data.get(1).get(0);
    }

    public boolean modifyAmeneties(int hotelId, String description) throws DLException {
        try {
            db.startTrans();

            String query = "UPDATE HotelAmenities SET amenitiesDescription = ? WHERE hotel_id = ?";
            boolean success = false;
            ArrayList<String> values = new ArrayList<>();
            values.add(description);
            values.add(String.valueOf(hotelId));

            success = db.setData(query, values);

            if (!success) {
                db.rollbackTrans();
                return false;
            }

            db.endTrans();
            return true;
        } catch (DLException e) {
            try {
                db.rollbackTrans();
            } catch (DLException rollbackEx) {
                throw e;
            }
            throw e;
        }
    }
}
