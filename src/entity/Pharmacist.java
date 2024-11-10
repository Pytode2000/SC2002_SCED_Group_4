package entity;

public class Pharmacist extends entity.Staff {

    // Constructor to initialize Pharmacist-specific attributes
    public Pharmacist(String userId, String firstName, String lastName, String gender, String dateOfBirth,
            String contactNumber, String emailAddress, String userRole) {
        // Using the constructor of the abstract User class to initialize common attributes
        super(userId, firstName, lastName, gender, dateOfBirth, contactNumber, emailAddress, userRole);
    }

    // Implement the abstract method from User to return the user's role
    @Override
    public String getUserRole() {
        return "Pharmacist"; // Return the role specific to this class
    }

    // Override toString to return the formatted string representation of Pharmacist's details
    @Override
    public String toString() {
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|" + getGender() + "|" + getDateOfBirth()
                + "|" + getContactNumber() + "|" + getEmailAddress() + "|" + getUserRole();
    }
}
