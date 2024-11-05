package entity;

public class Administrator extends entity.Staff {

    // add more administrator-specific attributes here if any.
    public Administrator(String userId, String firstName, String lastName, String gender, String dateOfBirth,
            String contactNumber, String emailAddress, String userRole) {
        // Using the constructor of the abstract User class.
        super(userId, firstName, lastName, gender, dateOfBirth, contactNumber, emailAddress, userRole);
    }

    // Implement the abstract method from User.
    @Override
    public String getUserRole() {
        return "Administrator";
    }

    @Override
    public String toString() {
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|" + getGender() + "|" + getDateOfBirth()
                + "|" + getContactNumber() + "|" + getEmailAddress() + "|" + getUserRole();
    }
}
