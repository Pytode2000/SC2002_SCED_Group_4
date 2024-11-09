package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Feedback {

    private String patientId;
    private String doctorId;
    private int rating;
    private String comments;
    private String datetime; // New field for the date and time of feedback

    // Constructor to initialize feedback
    public Feedback(String patientId, String doctorId, int rating, String comments) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.rating = rating;
        this.comments = comments;

        // Generate current date and time (DD-MM-YYYY HH:MM)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        this.datetime = LocalDateTime.now().format(formatter); // Set current date and time
    }

    // Getters and Setters
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    // Display feedback in a formatted way, including datetime
    public void displayFeedback() {
        System.out.println("\n--- Feedback from Patient ID: " + patientId + " ---");
        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Rating: " + rating + "/10");
        System.out.println("Date and Time: " + datetime);
        if (!comments.equals("-")) {
            System.out.println("Comments: " + comments);
        }
    }
}
