package entity;

public class Account {

    private String password; // Hashed password
    private final String userId; // Linked 1-to-1 with User entity, immutable
    private User user;  // Reference to the associated User (Doctor, Patient, etc.)

    // Constructor to initialize the account with user ID, password, and associated user
    public Account(String userId, String password, User user) {
        this.userId = userId;
        this.password = password;
        this.user = user;
    }

    // Getter for associated user
    public User getUser() {
        return this.user;
    }

    // Getter for the role of the associated user
    public String getUserRole() {
        return this.user.getUserRole();  // Get the role from the associated User
    }
}
