package entity;

public class Pharmacist extends entity.Staff {

    // add more Pharmacist-specific attributes here if any.
    public Pharmacist(String userId, String firstName, String lastName, String gender, String contactNumber, String emailAddress, String userRole) {
        // Using the constructor of the abstract User class.
        super(userId, firstName, lastName, gender, contactNumber, emailAddress, userRole);
    }

    // Implement the abstract method from User.
    @Override
    public String getUserRole() {
        return "Pharmacist";
    }

    @Override
    public String toString() {
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|"
                + getGender() + "|" + getContactNumber() + "|" + getEmailAddress() + "|"
                + getUserRole();
    }
}
