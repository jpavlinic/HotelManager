package BusinessLayer;

import java.sql.SQLException;
import java.util.ArrayList;

import DataPersistance.DLException;
import DataPersistance.Database;
import javafx.scene.control.TextArea;

public class Review {

    private int reviewId;
    private int hotelId;
    private String username;
    private double reviewRating;
    private String description;

    public Review(int hotelId, String username, double reviewRating, String description) {
        this.hotelId = hotelId;
        this.username = username;
        this.reviewRating = reviewRating;
        this.description = description;
    }

    public Review() {
    }

    public boolean enterReview(Database db, int hotelId, String username, Integer reviewRating, String description)
            throws DLException {
        try {
            String query = "INSERT INTO Reviews (hotel_id, username, reviewRating, description) VALUES (?, ?, ?, ?)";

            ArrayList<String> values = new ArrayList<>();
            values.add(String.valueOf(hotelId));
            values.add(username);
            values.add(String.valueOf(reviewRating));
            values.add(description);

            boolean success = db.setData(query, values);

            if (!success) {
                System.out.println("Review insert failed.  Please try again.");
            }

            return success;
        } catch (DLException e) {
            throw new DLException(e);
        }
    }

    public ArrayList<ArrayList<String>> showReviews(Database db, int hotelId, TextArea textArea) throws DLException {
        String query = "SELECT * FROM Reviews  WHERE hotel_id ='" + String.valueOf(hotelId) + "'";
        ArrayList<ArrayList<String>> reviewsData;

        try {
            reviewsData = db.getData(query, true);

            if (reviewsData.size() > 1) {
                StringBuilder text = new StringBuilder("Reviews:\n");

                for (int i = 1; i < reviewsData.size(); i++) {
                    ArrayList<String> review = reviewsData.get(i);
                    for (int j = 2; j < review.size(); j++) {
                        String field = review.get(j);
                        text.append(field).append("\t");
                    }

                    text.append("\n");
                }
                textArea.setText(text.toString());
            } else {
                textArea.setText("No reviews found.");
            }
        } catch (DLException e) {
            throw new DLException(e);
        }

        return reviewsData;
    }

}
