package BusinessLayer;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import DataPersistance.DLException;
import DataPersistance.Database;

public class Reservation {

    private int reservationId;
    private int hotelId;
    private String username;
    private Date checkIn;
    private Date checkOut;
    private int room;

    public Reservation(int hotelId, String username, Date checkIn, Date checkOut, int room) {
        this.hotelId = hotelId;
        this.username = username;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.room = room;
    }

    public Reservation() {}


    public boolean reserve(Database db, int hotelId, String username, LocalDate checkIn, LocalDate checkOut, Integer room)
            throws DLException {
        try {
            String query = "INSERT INTO Reservation (hotel_id, username, checkIn, checkOut, room) VALUES (?, ?, ?, ?, ?)";

            ArrayList<String> values = new ArrayList<>();
            values.add(String.valueOf(hotelId));
            values.add(username);
            values.add(checkIn.toString());
            values.add(checkOut.toString());
            values.add(String.valueOf(room));

            boolean success = db.setData(query, values);

            if (!success) {
                System.out.println("Booking attempt failed. Please try again.");
            }
            return success;

        } catch (DLException e) {
            throw new DLException(e);
        }
    }

    public boolean cancelReservation(Database db, int reservationId) throws DLException {
        try {
            String query = "DELETE FROM Reservation WHERE reservation_id = ?";

            ArrayList<String> values = new ArrayList<>();
            values.add(String.valueOf(reservationId));

            boolean success = db.setData(query, values);

            if (!success) {
                System.out.println("Problems with removing your reservation. Please try again.");
            }
            return success;

        } catch (DLException e) {
            throw new DLException(e);
        }
    }
  

    public ArrayList<ArrayList<String>> showReservations(Database db, String username) throws DLException {
        String query = "SELECT r.reservation_id, l.Country, l.City, h.name, r.room, r.checkIn, r.checkOut " +
                       "FROM Reservation r " +
                       "JOIN Hotel h ON r.hotel_id = h.hotel_id " +
                       "JOIN Location l ON h.location_id = l.location_id " +
                       "WHERE r.username = ?";
        ArrayList<String> params = new ArrayList<>();
        params.add(username);
        
        return db.getData(query, params, true);
    }
    
    }
    

    

