package PresentationLayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import BusinessLayer.Reservation;
import BusinessLayer.User;
import DataPersistance.DLException;
import DataPersistance.Database;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Profile {

    private Database db;
    private User currentUser;

    public void start(Stage primaryStage, Database db, User u) {
        this.db = db;
        this.currentUser = u;

        if (currentUser.getUserType().equals("Admin") || currentUser.getUserType().equals("Regular")) {
            VBox profileLayout = new VBox(10);
            profileLayout.setPadding(new Insets(20));
            profileLayout.setAlignment(Pos.CENTER);

            Label userSettingsLabel = new Label("User Settings");
            HBox userSettingsButtons = new HBox(10);
            Button changeUsernameButton = new Button("Change Username");
            Button changeFullNameButton = new Button("Change Full Name");
            Button changePasswordButton = new Button("Change Password");
            userSettingsButtons.getChildren().addAll(changeUsernameButton, changeFullNameButton, changePasswordButton);
            userSettingsButtons.setAlignment(Pos.CENTER);

            changeUsernameButton.setOnAction(e -> changeUsername());
            changeFullNameButton.setOnAction(e -> changeFullName());
            changePasswordButton.setOnAction(e -> changePassword());

            Label reservationsLabel = new Label("Reservations:");
            ListView<String> listView = new ListView<>();
            HBox reservationButtons = new HBox(10);
            Button refreshButton = new Button("Refresh Reservations");
            Button cancelButton = new Button("Cancel Reservation");
            Button backButton = new Button("Back");
            reservationButtons.getChildren().addAll(refreshButton, cancelButton, backButton);
            reservationButtons.setAlignment(Pos.CENTER);

            refreshButton.setOnAction(e -> refreshReservations(listView, currentUser.getUsername()));
            cancelButton.setOnAction(e -> cancelSelectedReservation(listView));
            backButton.setOnAction(e -> UIFactory.createUI("Search", primaryStage, db, u));

            profileLayout.getChildren().addAll(userSettingsLabel, userSettingsButtons, reservationsLabel, listView,
                    reservationButtons);

            Scene scene = new Scene(profileLayout, 700, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Profile - " + currentUser.getFullName());
            primaryStage.show();
        } else {
            System.out.println("Unauthorized access");
        }

    }

    private void changeUsername() {
        String newUsername = promptForInput("Change Username", "Enter new username:");
        if (newUsername != null && !newUsername.isEmpty() && !newUsername.equals(currentUser.getUsername())) {
            try {
                if (currentUser.updateUsername(db, newUsername)) {
                    showAlert("Success", "Username updated successfully to " + newUsername);
                } else {
                    showAlert("Error", "Failed to update username.");
                }
            } catch (SQLException | DLException e) {
                showAlert("Error", "Database error: " + e.getMessage());
            }
        }
    }

    private void changeFullName() {
        String newFullName = promptForInput("Change Full Name", "Enter new full name:");
        if (newFullName != null && !newFullName.isEmpty()) {
            try {
                if (currentUser.updateFullName(db.getConn(), newFullName)) {
                    showAlert("Success", "Full name updated successfully to " + newFullName);
                } else {
                    showAlert("Error", "Failed to update full name.");
                }
            } catch (SQLException e) {
                showAlert("Error", "Database error: " + e.getMessage());
            }
        }
    }

    private void changePassword() {
        String newPassword = promptForInput("Change Password", "Enter new password:");
        if (newPassword != null && !newPassword.isEmpty()) {
            try {
                if (currentUser.updatePassword(db, newPassword)) {
                    showAlert("Success", "Password updated successfully.");
                } else {
                    showAlert("Error", "Failed to update password.");
                }
            } catch (Exception e) {
                showAlert("Error", "Database error: " + e.getMessage());
            }
        }
    }

    private String promptForInput(String title, String header) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText("Value:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void refreshReservations(ListView<String> listView, String username) {
        if (db == null) {
            showAlert("Database Error", "No valid database connection.");
            return;
        }

        try {
            Reservation reservation = new Reservation();
            ArrayList<ArrayList<String>> reservations = reservation.showReservations(db, username);

            listView.getItems().clear();

            int startIndex = (reservations.size() > 0 && reservations.get(0).get(0).matches("\\D+")) ? 1 : 0;

            for (int i = startIndex; i < reservations.size(); i++) {
                ArrayList<String> row = reservations.get(i);
                if (row.size() < 7) {
                    continue;
                }
                String formattedText = String.format(
                        "ID: %s | Location: %s, %s | Hotel: %s | Room: %s | Dates: %s to %s",
                        row.get(0), // Reservation ID
                        row.get(1), // Country
                        row.get(2), // City
                        row.get(3), // Hotel Name
                        row.get(4), // Room Number
                        row.get(5), // Check-in Date
                        row.get(6)); // Check-out Date
                listView.getItems().add(formattedText);
            }
        } catch (DLException ex) {
            showAlert("Error", "Failed to fetch reservations: " + ex.getMessage());
        }
    }

    private void cancelSelectedReservation(ListView<String> listView) {
        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isEmpty()) {
            try {
                // Correctly extracting the ID using proper regex escape characters
                int reservationId = Integer.parseInt(selected.split(" \\| ")[0].split(": ")[1].trim());
                Reservation reservation = new Reservation();
                if (reservation.cancelReservation(db, reservationId)) {
                    listView.getItems().remove(selected);
                    showAlert("Success", "Reservation cancelled successfully.");
                } else {
                    showAlert("Error", "Failed to cancel the reservation.");
                }
            } catch (NumberFormatException | DLException ex) {
                showAlert("Error", "Failed to cancel the reservation: " + ex.getMessage());
            }
        } else {
            showAlert("Error", "No reservation selected.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
