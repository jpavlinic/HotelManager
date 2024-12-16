package BusinessLayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import DataPersistance.DLException;
import DataPersistance.Database;

public class Hotel {
    private int hotelId;
    private String name;
    private int locationId;
    private Location location;
    private int stars;
    private double reviewRating;
    private int numberOfRooms;

    public Hotel(String name, int locationId, Location location, int stars, double reviewRating, int numberOfRooms) {
        this.name = name;
        this.locationId = locationId;
        this.location = location;
        this.stars = stars;
        this.reviewRating = reviewRating;
        this.numberOfRooms = numberOfRooms;
    }

    public Hotel() {

    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getLocationId() {
        return locationId;
    }

    public Location getLocation() {
        return location;
    }

    public int getStars() {
        return stars;
    }

    public double getReviewRating() {
        return reviewRating;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public ArrayList<ArrayList<String>> searchWithLocationDetails(Database db, User u) throws DLException {
        String query = "SELECT h.hotel_id, h.name, h.location_id, h.stars, h.reviewRating, h.numberOfRooms, " +
        "l.Country, l.City, l.zip, l.address " +
        "FROM Hotel h " +
        "JOIN Location l ON h.location_id = l.location_id " +
        "WHERE h.posted_by = '" + u.getUsername() + "'";

        ArrayList<ArrayList<String>> hotelData;
        try {
            hotelData = db.getData(query, true);
            if (hotelData.size() > 1) {
                for (int i = 0; i < hotelData.size(); i++) {
                    for (String data : hotelData.get(i)) {
                        System.out.print(data + "\t");
                    }
                    System.out.println();
                }
            } else {
                System.out.println("No Hotels found.");
            }
        } catch (DLException e) {
            throw new DLException(e);
        }
        return hotelData;
    }

    public void save(Database db) throws DLException {
        ArrayList<String> stringParams = new ArrayList<>();
        stringParams.add(name);
        stringParams.add(String.valueOf(locationId));
        stringParams.add(String.valueOf(stars));
        stringParams.add(String.format("%.2f", reviewRating));
        stringParams.add(String.valueOf(numberOfRooms));

        String query;
        if (this.hotelId == 0) {
            query = "INSERT INTO Hotel (name, location_id, stars, reviewRating, numberOfRooms) VALUES (?, ?, ?, ?, ?)";
        } else {
            stringParams.add(String.valueOf(hotelId));
            query = "UPDATE Hotel SET name = ?, location_id = ?, stars = ?, reviewRating = ?, numberOfRooms = ? WHERE hotel_id = ?";
        }

        System.out.println("Executing query: " + query);
        System.out.println("With parameters: " + stringParams);

        db.setData(query, stringParams);
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public ArrayList<ArrayList<String>> search(Database db) throws DLException {
        String query = "SELECT * FROM Hotel";
        ArrayList<ArrayList<String>> hotelData;
        try {
            hotelData = db.getData(query, true);

            if (hotelData.size() > 1) {
                ArrayList<String> columnNames = hotelData.get(0);
                System.out.println("Hotel:");
                for (String columnName : columnNames) {
                    System.out.print(columnName + "\t");
                }
                System.out.println();

                for (int i = 1; i < hotelData.size(); i++) {
                    ArrayList<String> hotel = hotelData.get(i);
                    for (int j = 0; j < hotel.size(); j++) {
                        System.out.print(hotel.get(j) + "\t");
                    }
                    System.out.println();
                }
            } else {
                System.out.println("No Hotels found.");
            }
        } catch (DLException e) {
            throw new DLException(e);
        }

        return hotelData;
    }

    public ArrayList<ArrayList<String>> filteredSearch(Database db, String name, String location, String stars,
            String review, String rooms)
            throws DLException {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Hotel WHERE ");
        boolean firstFilter = true;
        ArrayList<ArrayList<String>> hotelData;

        if (name != null && !name.isEmpty()) {
            if (!firstFilter) {
                queryBuilder.append(" AND ");
            } else {
                firstFilter = false;
            }
            queryBuilder.append("name LIKE '%").append(name).append("%'");
        }

        if (location != null && !location.isEmpty()) {
            if (!firstFilter) {
                queryBuilder.append(" AND ");
            } else {
                firstFilter = false;
            }
            queryBuilder.append("location_id = ").append(location);
        }

        if (stars != null && !stars.isEmpty()) {
            if (!firstFilter) {
                queryBuilder.append(" AND ");
            } else {
                firstFilter = false;
            }
            queryBuilder.append("stars >= ").append(stars);
        }

        if (review != null && !review.isEmpty()) {
            if (!firstFilter) {
                queryBuilder.append(" AND ");
            } else {
                firstFilter = false;
            }
            queryBuilder.append("reviewRating >= ").append(reviewRating);
        }

        if (rooms != null && !rooms.isEmpty()) {
            if (!firstFilter) {
                queryBuilder.append(" AND ");
            } else {
                firstFilter = false;
            }
            queryBuilder.append("numberOfRooms >= ").append(rooms);
        }

        try {
            hotelData = db.getData(queryBuilder.toString(), true);

            if (hotelData.size() > 1) {
                ArrayList<String> columnNames = hotelData.get(0);
                System.out.println("Hotel:");
                for (String columnName : columnNames) {
                    System.out.print(columnName + "\t");
                }
                System.out.println();

                for (int i = 1; i < hotelData.size(); i++) {
                    ArrayList<String> hotel = hotelData.get(i);
                    for (int j = 0; j < hotel.size(); j++) {
                        System.out.print(hotel.get(j) + "\t");
                    }
                    System.out.println();
                }
            } else {
                System.out.println("No Hotels found.");
            }
        } catch (DLException e) {
            throw new DLException(e);
        }
        return hotelData;
    }

    public ArrayList<ArrayList<String>> sortedSearch(ArrayList<ArrayList<String>> hotelData, String sortBy,
            boolean ascending) {
        if (hotelData.size() > 1) {
            ArrayList<String> columnNames = hotelData.get(0);

            int sortIndex = columnNames.indexOf(sortBy);
            if (sortIndex == -1) {
                System.out.println("Column '" + sortBy + "' not found.");
                return null;
            }

            Collections.sort(hotelData.subList(1, hotelData.size()), new Comparator<ArrayList<String>>() {
                @Override
                public int compare(ArrayList<String> hotel1, ArrayList<String> hotel2) {
                    String value1 = hotel1.get(sortIndex);
                    String value2 = hotel2.get(sortIndex);
                    if (value1 == null || value2 == null) {
                        return 0;
                    }
                    int result = value1.compareTo(value2);
                    if (ascending) {
                        return result;
                    } else {
                        return -result;
                    }
                }
            });

            System.out.println("Sorted Hotels by " + sortBy + ":");
            for (String columnName : columnNames) {
                System.out.print(columnName + "\t");
            }
            System.out.println();

            for (int i = 1; i < hotelData.size(); i++) {
                ArrayList<String> hotel = hotelData.get(i);
                for (int j = 0; j < hotel.size(); j++) {
                    System.out.print(hotel.get(j) + "\t");
                }
                System.out.println();
            }
        } else {
            System.out.println("No Hotels found.");
        }
        return hotelData;
    }

}
