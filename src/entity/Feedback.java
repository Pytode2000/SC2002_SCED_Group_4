package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Feedback {

    private String patientId;       // Patient's ID
    private String doctorId;        // Doctor's ID
    private int rating;             // Rating given by the patient (1-10)
    private String comments;        // Comments provided by the patient
    private String datetime;        // Date and time when the feedback was given

    // Constructor to initialize feedback
    public Feedback(String patientId, String doctorId, int rating, String comments) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.rating = rating;
        this.comments = comments;

        // Generate current date and time in the format (DD-MM-YYYY HH:MM)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        this.datetime = LocalDateTime.now().format(formatter); // Set current date and time
    }

    // Getter and setter for patientId
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    // Getter and setter for doctorId
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    // Getter and setter for rating
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    // Getter and setter for comments
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // Getter and setter for datetime
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
        if (!comments.equals("-")) { // Only display comments if not "-"
            System.out.println("Comments: " + comments);
        }
    }
}
