package entity;

public class Administrator extends entity.Staff {

    // Constructor to initialize an Administrator object
    public Administrator(String userId, String firstName, String lastName, String gender, String dateOfBirth,
            String contactNumber, String emailAddress, String userRole) {
        // Using the constructor of the abstract Staff class.
        super(userId, firstName, lastName, gender, dateOfBirth, contactNumber, emailAddress, userRole);
    }

    // Return the role of the user as "Administrator"
    @Override
    public String getUserRole() {
        return "Administrator";
    }

    // Return a string representation of the Administrator object
    @Override
    public String toString() {
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|" + getGender() + "|" + getDateOfBirth()
                + "|" + getContactNumber() + "|" + getEmailAddress() + "|" + getUserRole();
    }
}
