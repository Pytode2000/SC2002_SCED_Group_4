package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ForgetPassword {

    private String userId;
    private String message;
    private LocalDateTime requestDateTime;

    // Constructor to initialize userId, message, and capture current date and time
    public ForgetPassword(String userId, String message) {
        this.userId = userId;
        this.message = message;
        this.requestDateTime = LocalDateTime.now(); // Captures current date and time
    }

    // Method to display the ForgetPassword details
    public void displayRequestDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.out.println("Forget Password Request Details:");
        System.out.println("User ID: " + userId);
        System.out.println("Message: " + message);
        System.out.println("Created: " + requestDateTime.format(formatter));
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }
}
