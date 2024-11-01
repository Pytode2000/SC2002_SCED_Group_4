package entity;

public abstract class Staff extends User {

    public Staff(String userId, String firstName, String lastName, String gender, String contactNumber, String emailAddress, String userRole) {
        super(userId, firstName, lastName, gender, contactNumber, emailAddress, userRole);
        // Add staff specific attributes here.
    }


}
