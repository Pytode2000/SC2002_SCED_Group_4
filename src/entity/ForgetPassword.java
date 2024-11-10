package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ForgetPassword {

    private String userId;           // ID of the user requesting password reset
    private String message;          // Message related to the password reset request
    private LocalDateTime requestDateTime; // Date and time when the request was created

    // Constructor to initialize userId, message, and capture current date and time
    public ForgetPassword(String userId, String message) {
        this.userId = userId;
        this.message = message;
        this.requestDateTime = LocalDateTime.now(); // Captures current date and time
    }

    // Displays the details of the forget password request
    public void displayRequestDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.out.println("Forget Password Request Details:");
        System.out.println("User ID: " + userId);
        System.out.println("Message: " + message);
        System.out.println("Created: " + requestDateTime.format(formatter));
    }

    // Getter for userId
    public String getUserId() {
        return userId;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Getter for request date and time
    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }
}
