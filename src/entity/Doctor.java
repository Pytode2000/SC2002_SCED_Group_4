package entity;

public class Doctor extends entity.User {

    // add more doctor-specific attributes here if any.

    public Doctor(String userId, String firstName, String lastName, String gender, String contactNumber, String emailAddress, String userRole) {
        // Using the constructor of the abstract User class.
        super(userId, firstName, lastName, gender, contactNumber, emailAddress, userRole);
    }

    // Implement the abstract method from User.
    @Override
    public String getUserRole() {
        return "Doctor";
    }

    @Override
    public String toString() {
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|"
                + getGender() + "|" + getContactNumber() + "|" + getEmailAddress() + "|"
                + getUserRole();
    }
}
