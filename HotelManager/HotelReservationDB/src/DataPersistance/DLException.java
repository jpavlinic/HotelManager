package DataPersistance;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The {@code DLException} class extends the standard Java {@code Exception} class
 * to provide custom error handling for the application. This class is designed to
 * log exceptions along with additional details to a log file for troubleshooting.
 */
public class DLException extends Exception {
    private Exception exception;
    private List<String> strings;

    /**
     * Constructs a {@code DLException} with the specified exception.
     * The constructor logs the exception details to a file.
     *
     * @param exception The original exception that was caught.
     */
    public DLException(Exception exception) {
        super("Unable to complete operation. Please contact the administrator.");
        this.exception = exception;
        this.strings = new ArrayList<>();
        logExceptions();
    }

    /**
     * Constructs a {@code DLException} with the specified exception and additional details.
     * The constructor logs the exception and the additional details to a file.
     *
     * @param exception The original exception that was caught.
     * @param strings   A list of additional details or context information to be logged along with the exception.
     */
    public DLException(Exception exception, List<String> strings) {
        super("Unable to complete operation. Please contact the administrator.");
        this.exception = exception;
        this.strings = strings;
        logExceptions();
    }

    /**
     * Logs the details of the exception to a file named "error_log.txt".
     * The log includes a timestamp, the exception message, and any additional details provided.
     * If the exception is an instance of {@code SQLException}, the SQL state and error code are also logged.
     * This method uses {@code BufferedWriter} for writing to the file.
     */
    private void logExceptions() {
        try (BufferedWriter errorLogWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("error_log.txt", true)))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            errorLogWriter.append(String.format("Log Timestamp: %s%n", formatter.format(currentTime)));
            errorLogWriter.append(String.format("Error Message: %s%n", exception.getMessage()));
            if (exception instanceof SQLException) {
                SQLException sqlException = (SQLException) exception;
                errorLogWriter.append(String.format("SQL State: %s%n", sqlException.getSQLState()));
                errorLogWriter.append(String.format("SQL Error Code: %s%n", sqlException.getErrorCode()));
            }
            for (String detail : strings) {
                errorLogWriter.append(String.format("Detail: %s%n", detail));
            }
            errorLogWriter.append(System.lineSeparator());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}