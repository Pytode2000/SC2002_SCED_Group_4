package entity;

public abstract class Staff extends User {

    // Constructor to initialize staff-specific attributes
    public Staff(String userId, String firstName, String lastName, String gender, String dateOfBirth,
            String contactNumber, String emailAddress, String userRole) {
        super(userId, firstName, lastName, gender, dateOfBirth, contactNumber, emailAddress, userRole);
        // Currently no staff-specific attributes
    }

    // Registration string to return a formatted string with staff details
    @Override
    public String registrationString() {
        // Returns a string with user details (userId, firstName, lastName, gender, dateOfBirth, contactNumber, emailAddress, userRole)
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|" + getGender() + "|" + getDateOfBirth()
                + "|" + getContactNumber() + "|" + getEmailAddress() + "|" + getUserRole();
    }

}
