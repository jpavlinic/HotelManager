package BusinessLayer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import DataPersistance.DLException;
import DataPersistance.Database;

public class User {
    private String username;
    private String password;
    private String fullName;
    private String userType;

    public User() {

    }

    public User(String username, String password, String fullName, String userType) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void register(Connection connection) {
        try {
            String hashedPassword = hashPassword(this.password);

            String sql = "INSERT INTO User (username, password, fullName, usertype) VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, this.username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, this.fullName);
            preparedStatement.setString(4, this.userType);

            preparedStatement.executeUpdate();
            preparedStatement.close();

            System.out.println("User registered successfully!");
        } catch (SQLException | NoSuchAlgorithmException e) {
            System.err.println("Error while registering user: " + e.getMessage());
        }
    }

    public boolean login(Connection connection) {
        try {
            String hashedPassword = hashPassword(this.password);

            String sql = "SELECT * FROM User WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, this.username);
                preparedStatement.setString(2, hashedPassword);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        if (!resultSet.isLast()) {
                            System.err.println("Error: Multiple users found with the same username and password.");
                            return false;
                        }
                        this.userType = resultSet.getString("usertype");
                        return true;
                    }
                }
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            System.err.println("Error while authenticating user: " + e.getMessage());
        }
        return false;
    }

    public boolean updateUsername(Database db, String newUsername) throws SQLException, DLException {
        Connection conn = db.getConn();
        try {
            conn.setAutoCommit(false);

            String updateReservationsSQL = "UPDATE Reservation SET username = NULL WHERE username = ?";
            ArrayList<String> tempParams = new ArrayList<>(Arrays.asList(this.username));
            db.setData(updateReservationsSQL, tempParams);

            String updateHotelNullSQL = "UPDATE Hotel SET posted_by = NULL WHERE posted_by = ?";
            ArrayList<String> hotelNullParams = new ArrayList<>(Arrays.asList(this.username));
            db.setData(updateHotelNullSQL, hotelNullParams);

            String updateReviewsNullSQL = "UPDATE Reviews SET username  = NULL WHERE username  = ?";
            ArrayList<String> reviewsNullParams = new ArrayList<>(Arrays.asList(this.username));
            db.setData(updateReviewsNullSQL, reviewsNullParams);

            String updatePaymentSQL = "UPDATE Payment  SET username  = NULL WHERE username  = ?";
            ArrayList<String> paymentNullParams = new ArrayList<>(Arrays.asList(this.username));
            db.setData(updatePaymentSQL, paymentNullParams);

            String updateUsernameSQL = "UPDATE User SET username = ? WHERE username = ?";
            ArrayList<String> userParams = new ArrayList<>(Arrays.asList(newUsername, this.username));
            db.setData(updateUsernameSQL, userParams);

            updateReservationsSQL = "UPDATE Reservation SET username = ? WHERE username IS NULL";
            tempParams.set(0, newUsername);
            db.setData(updateReservationsSQL, tempParams);

            updateReservationsSQL = "UPDATE Reviews SET username = ? WHERE username IS NULL";
            tempParams.set(0, newUsername);
            db.setData(updateReservationsSQL, tempParams);

            updateReservationsSQL = "UPDATE Payment SET username = ? WHERE username IS NULL";
            tempParams.set(0, newUsername);
            db.setData(updateReservationsSQL, tempParams);

            String updateHotelSQL = "UPDATE Hotel SET posted_by = ? WHERE posted_by IS NULL";
            ArrayList<String> hotelParams = new ArrayList<>(Arrays.asList(newUsername));
            db.setData(updateHotelSQL, hotelParams);

            conn.commit();
            this.username = newUsername;
            return true;
        } catch (SQLException | DLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public boolean updateFullName(Connection connection, String newFullName) throws SQLException {
        if (newFullName.isEmpty())
            return false;
        String sql = "UPDATE User SET fullName = ? WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newFullName);
            preparedStatement.setString(2, this.username);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(Database db, String newPassword) throws Exception {
        Connection conn = db.getConn();
        try {
            conn.setAutoCommit(false);
            String hashedPassword = hashPassword(newPassword);

            String updateSQL = "UPDATE User SET password = ? WHERE username = ?";
            ArrayList<String> params = new ArrayList<>(Arrays.asList(hashedPassword, this.username));
            db.setData(updateSQL, params);

            conn.commit();
            return true;
        } catch (NoSuchAlgorithmException | SQLException | DLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

}
