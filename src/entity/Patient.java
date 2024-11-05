package entity;

public class Patient extends entity.User {

    private String dateOfBirth;
    private String bloodType;

    // add more patient-specific attributes here if any.
    // Constructor
    public Patient(String userId, String firstName, String lastName, String gender, String contactNumber,
            String emailAddress, String userRole, String dateOfBirth, String bloodType) {
        // Using the constructor of the abstract User class.
        super(userId, firstName, lastName, gender, dateOfBirth, contactNumber, emailAddress, userRole);
        this.bloodType = bloodType;
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
    public String registrationString() {
        // userId, firstName, lastName, gender, contactNumber, emailAddress, userRole
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|" + getGender() + "|" + getContactNumber()
                + "|" + getEmailAddress() + "|" + getUserRole() + "|" + getDateOfBirth() + "|" + getBloodType();
    }

    @Override
    public String toString() {
        return "User ID: " + getUserId() + "\n"
                + "First Name: " + getFirstName() + "\n"
                + "Last Name: " + getLastName() + "\n"
                + "Gender: " + getGender() + "\n"
                + "Contact Number: " + getContactNumber() + "\n"
                + "Email Address: " + getEmailAddress() + "\n"
                + "User Role: " + getUserRole() + "\n"
                + "Date of Birth: " + dateOfBirth + "\n"
                + "Blood Type: " + bloodType;
    }
}
