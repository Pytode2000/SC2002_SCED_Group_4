package entity;

public abstract class Staff extends User {

    public Staff(String userId, String firstName, String lastName, String gender, String contactNumber, String emailAddress, String userRole) {
        super(userId, firstName, lastName, gender, contactNumber, emailAddress, userRole);
        // Add staff specific attributes here.
    }

    @Override
    public String registrationString() {
        //userId, firstName, lastName, gender, contactNumber, emailAddress, userRole
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|" + getGender() + "|" + getContactNumber() + "|" + getEmailAddress() + "|" + getUserRole();
    }

}
