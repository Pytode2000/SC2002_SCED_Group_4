package entity;

public abstract class Staff extends User {

    public Staff(String userId, String firstName, String lastName, String gender, String dateOfBirth,
            String contactNumber, String emailAddress, String userRole) {
        super(userId, firstName, lastName, gender, dateOfBirth, contactNumber, emailAddress, userRole);
        // Add staff specific attributes here.
    }

    @Override
    public String registrationString() {
        // userId, firstName, lastName, gender, dateOfBirth, contactNumber,
        // emailAddress, userRole
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|" + getGender() + "|" + getDateOfBirth()
                + "|" + getContactNumber() + "|" + getEmailAddress() + "|" + getUserRole();
    }

}
