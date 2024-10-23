
public class Account {

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
        return this.user.getRole();  // Get the role from the associated User
    }

}
