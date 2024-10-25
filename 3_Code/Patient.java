
public class Patient extends User {

    private String dateOfBirth;
    private String bloodType;
    // add more patient-specific attributes here if any.

    // Constructor
    public Patient(String userId, String firstName, String lastName, String gender, String contactNumber, String emailAddress, String userRole, String dateOfBirth, String bloodType) {
        // Using the constructor of the abstract User class.
        super(userId, firstName, lastName, gender, contactNumber, emailAddress, userRole);
        this.dateOfBirth = dateOfBirth;
        this.bloodType = bloodType;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getBloodType() {
        return bloodType;
    }

    // Implement the abstract method from User.
    @Override
    public String getUserRole() {
        return "Patient";
    }

    @Override
    public String toString() {
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|"
                + getGender() + "|" + getContactNumber() + "|" + getEmailAddress() + "|"
                + getUserRole() + "|" + dateOfBirth + "|" + bloodType;
    }
}
