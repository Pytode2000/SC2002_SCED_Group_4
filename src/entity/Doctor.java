package entity;

public class Doctor extends entity.Staff {

    // Constructor to initialize a Doctor object with the given details.
    public Doctor(String userId, String firstName, String lastName, String gender, String dateOfBirth,
            String contactNumber, String emailAddress, String userRole) {
        // Using the constructor of the abstract User class to initialize common attributes.
        super(userId, firstName, lastName, gender, dateOfBirth, contactNumber, emailAddress, userRole);
    }

    // Implement the abstract method from the User class.
    @Override
    public String getUserRole() {
        return "Doctor";  // Return the role of the user as "Doctor".
    }

    // String representation of the Doctor object with all relevant details.
    @Override
    public String toString() {
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|" + getGender() + "|" + getDateOfBirth()
                + "|" + getContactNumber() + "|" + getEmailAddress() + "|" + getUserRole();
    }
}
