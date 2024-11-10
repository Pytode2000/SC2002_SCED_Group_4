package entity;

public abstract class User {  // ABSTRACT CLASS, NO USER OBJECT WILL EVER BE INSTANTIATED!

    private String userId;          // User's unique identifier.
    private String firstName;       // User's first name.
    private String lastName;        // User's last name.
    private String gender;          // User's gender.
    private String dateOfBirth;     // User's date of birth.
    private String contactNumber;   // User's contact number.
    private String emailAddress;    // User's email address.
    private String userRole;        // Role of the user (e.g., Doctor, Patient, etc.).

    // Constructor to initialize the user details.
    public User(String userId, String firstName, String lastName, String gender, String dateOfBirth,
            String contactNumber, String emailAddress, String userRole) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;
        this.emailAddress = emailAddress;
        this.userRole = userRole;
    }

    // Getter for the user ID.
    public String getUserId() {
        return userId;
    }

    // Getter for the user's first name.
    public String getFirstName() {
        return firstName;
    }

    // Getter for the user's last name.
    public String getLastName() {
        return lastName;
    }

    // Getter for the user's gender.
    public String getGender() {
        return gender;
    }

    // Getter for the user's date of birth.
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    // Getter for the user's contact number.
    public String getContactNumber() {
        return contactNumber;
    }

    // Getter for the user's email address.
    public String getEmailAddress() {
        return emailAddress;
    }

    // Setter for the user's contact number.
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    // Setter for the user's email address.
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    // Abstract method to get the user's role.
    // Forces subclasses to implement a specific user role.
    public abstract String getUserRole();

    // Abstract toString method to return a string representation of the user.
    // Forces subclasses to implement their own toString method.
    public abstract String toString();

    // Abstract method for generating a registration string.
    // Forces subclasses to implement their own registration string format.
    public abstract String registrationString();
}
