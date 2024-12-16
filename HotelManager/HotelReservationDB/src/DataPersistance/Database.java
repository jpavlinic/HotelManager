package DataPersistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * This class is responsible for managing the database connection and operations
 * such as
 * establishing a connection, closing it, inserting/updating data, and
 * retrieving data.
 */
public class Database {

    private String url;
    private String username;
    private String password;
    private Connection conn;

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    /**
     * Constructs a Database object with the specified URL, username, and password.
     *
     * @param url      the database URL
     * @param username the database user's username
     * @param password the database user's password
     */
    public Database(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Attempts to establish a connection to the database using the provided
     * credentials.
     * Prints a success message if connected.
     *
     * @return true if the connection was successfully established, false otherwise.
     * @throws DLException if a SQLException occurs during database connection.
     */
    public boolean establishDatabaseConnection() throws DLException {
        Properties credentials = new Properties();
        credentials.setProperty("user", username);
        credentials.setProperty("password", password);
        try {
            conn = DriverManager.getConnection(url, credentials);
            System.out.println("Successfully connected to the database");
            return true;
        } catch (SQLException sqlException) {
            throw new DLException(sqlException, Arrays.asList("Failed to establish database connection"));
        }
    }

    /**
     * Closes the database connection if it is currently open.
     * Prints a success message if the connection was successfully closed.
     *
     * @return true if the connection was successfully closed, false if there was no
     *         connection to close.
     * @throws DLException if a SQLException occurs while closing the connection.
     */
    public boolean closeConnection() throws DLException {
        if (this.conn == null) {
            return false;
        }
        try {
            this.conn.close();
            System.out.println("\nConnection terminated successfully.");
            this.conn = null;
            return true;
        } catch (SQLException sqlException) {
            throw new DLException(sqlException, Collections.singletonList("Error terminating the database connection"));
        }
    }

    public PreparedStatement prepare(String query, ArrayList<String> values) throws DLException {
        PreparedStatement preparedS = null;

        try {
            preparedS = conn.prepareStatement(query);

            for (int i = 0; i < values.size(); i++) {
                preparedS.setString(i + 1, values.get(i));
            }

        } catch (Exception e) {
            throw new DLException(e);
        }

        return preparedS;
    }

    /**
     * Executes a SQL statement for inserting or updating data in the database.
     *
     * @param sql the SQL statement to execute
     * @return true if the operation was successful
     * @throws DLException if there is no database connection or a SQLException
     *                     occurs.
     */
    public boolean setData(String sql, ArrayList<String> values) throws DLException {
        if (conn == null) {
            throw new DLException(new SQLException("No database connection"),
                    Arrays.asList("Connection not established"));
        }
        try (PreparedStatement statement = prepare(sql, values)) {
            int rowsChanged = statement.executeUpdate();

            if (rowsChanged > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new DLException(e);
        }
    }

    /**
     * Retrieves data from the database by executing a SQL query.
     * Optionally includes column names as the first row of the result.
     *
     * @param sql                the SQL query to execute
     * @param includeColumnNames true if column names should be included in the
     *                           result
     * @return a list of rows, where each row is a list of strings representing
     *         column values
     * @throws DLException if there is no database connection or a SQLException
     *                     occurs during the query.
     */
    public ArrayList<ArrayList<String>> getData(String sql, ArrayList<String> values, boolean includeColumnNames) throws DLException {
        if (conn == null) {
            throw new DLException(new SQLException("No database connection"),
                    Arrays.asList("Connection not established"));
        }
        ArrayList<ArrayList<String>> data = new ArrayList<>();
    
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            // Set the values for the prepared statement
            for (int i = 0; i < values.size(); i++) {
                statement.setString(i + 1, values.get(i));
            }
            
            try (ResultSet result = statement.executeQuery()) {
                int columnCount = result.getMetaData().getColumnCount();
    
                if (includeColumnNames) {
                    ArrayList<String> columnNames = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames.add(result.getMetaData().getColumnName(i));
                    }
                    data.add(columnNames);
                }
    
                while (result.next()) {
                    ArrayList<String> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(result.getString(i));
                    }
                    data.add(row);
                }
            }
        } catch (SQLException e) {
            throw new DLException(e);
        }
        return data;
    }

    public ArrayList<ArrayList<String>> getData(String sql, boolean includeColumnNames) throws DLException {
        if (conn == null) {
            throw new DLException(new SQLException("No database connection"),
                    Arrays.asList("Connection not established"));
        }
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        try (Statement statement = conn.createStatement();
                ResultSet result = statement.executeQuery(sql)) {

            int columnCount = result.getMetaData().getColumnCount();

            if (includeColumnNames) {
                ArrayList<String> columnNames = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(result.getMetaData().getColumnName(i));
                }
                data.add(columnNames);
            }

            while (result.next()) {
                ArrayList<String> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(result.getString(i));
                }
                data.add(row);
            }
        } catch (SQLException e) {
            throw new DLException(e);
        }
        return data;
    }
    

    /**
     * Starts a database transaction by setting auto-commit to false.
     * 
     * @throws DLException if starting the transaction fails.
     */
    public void startTrans() throws DLException {
        try {
            if (this.conn != null) {
                this.conn.setAutoCommit(false);
            } else {
                throw new DLException(new SQLException("No database connection"),
                        Arrays.asList("Connection not established"));
            }
        } catch (SQLException e) {
            throw new DLException(e, Collections.singletonList("Failed to start transaction"));
        }
    }

    /**
     * Commits the current transaction and sets auto-commit back to true.
     * 
     * @throws DLException if committing the transaction fails.
     */
    public void endTrans() throws DLException {
        try {
            if (this.conn != null) {
                this.conn.commit();
                this.conn.setAutoCommit(true);
            } else {
                throw new DLException(new SQLException("No database connection"),
                        Arrays.asList("Connection not established"));
            }
        } catch (SQLException e) {
            throw new DLException(e, Collections.singletonList("Failed to commit transaction"));
        }
    }

    /**
     * Rolls back the current transaction and sets auto-commit back to true.
     * 
     * @throws DLException if rolling back the transaction fails.
     */
    public void rollbackTrans() throws DLException {
        try {
            if (this.conn != null) {
                this.conn.rollback();
                this.conn.setAutoCommit(true);
            } else {
                throw new DLException(new SQLException("No database connection"),
                        Arrays.asList("Connection not established"));
            }
        } catch (SQLException e) {
            throw new DLException(e, Collections.singletonList("Failed to rollback transaction"));
        }
    }



    /**
     * Executes an INSERT SQL statement and returns the generated key.
     * 
     * @param sql The INSERT SQL statement.
     * @param values The values to be inserted.
     * @return The generated key for the inserted row, or -1 if the insert operation failed.
     * @throws DLException if a database access error occurs or the SQL statement does not return a generated key.
     */
    public int insertWithKey(String sql, ArrayList<String> values) throws DLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                pstmt.setString(i + 1, values.get(i));
            }
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DLException(new SQLException("Insert operation failed, no rows affected."), Arrays.asList("Insert operation failed, no rows affected."));
            }
    
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); 
                } else {
                    throw new DLException(new SQLException("Insert operation succeeded, but no ID was obtained."), Arrays.asList("Insert operation succeeded, but no ID was obtained."));
                }
            }
        } catch (SQLException e) {
            throw new DLException(e, Arrays.asList("Error executing insertWithKey operation."));
        }
    }
    
}


 

