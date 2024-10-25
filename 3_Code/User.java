
public abstract class User { // ABSTRACT CLASS, NO USER OBJECT WILL EVER BE INSTANTIATED!

    private String userId;
    private String firstName;
    private String lastName;
    private String gender;
    private String contactNumber;
    private String emailAddress;
    private String userRole;

    public User(String userId, String firstName, String lastName, String gender, String contactNumber, String emailAddress, String userRole) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.emailAddress = emailAddress;
        this.userRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    // Abstract toString method to be implemented in subclasses.
    // Force child classes to implement.
    public abstract String getUserRole();
    public abstract String toString();
}
