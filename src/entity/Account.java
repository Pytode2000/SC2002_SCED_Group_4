package entity;

public class Account {
    // Create the person first (e.g., patient or doctor), then create the account. Link person to account.

    private String password; // Hashed.
    private final String userId; // Linked 1-to-1 with User entity, immutable.
    private User user;  // Reference to the associated User (Doctor, Patient, etc.)

    public Account(String userId, String password, User user) {
        this.userId = userId;
        this.password = password;
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public String getUserRole() {
        return this.user.getUserRole();  // Get the role from the associated User
    }

}
