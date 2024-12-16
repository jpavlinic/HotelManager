package BusinessLayer;

import java.util.ArrayList;

import DataPersistance.DLException;
import DataPersistance.Database;

public class Location {
    private int locationId;
    private String country;
    private String city;
    private String zip;
    private String address;

    public Location(int locationId, String country, String city, String zip, String address) {
        this.locationId = locationId;
        this.country = country;
        this.city = city;
        this.zip = zip;
        this.address = address;
    }

    public Location(){}

    public int getLocationId() {
        return locationId;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<String> fetchLocation(Database db, String locationId) throws DLException {
        String query = "SELECT * FROM Location  WHERE location_id ='" + locationId + "'";
        ArrayList<ArrayList<String>> allData;
        ArrayList<String> locationData;

        try {
            allData = db.getData(query, true);

            locationData = allData.get(1);
        } catch (DLException e) {
            throw new DLException(e);
        }

        return locationData;
    }
}
