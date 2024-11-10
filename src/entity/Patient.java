package entity;

public class Patient extends entity.User {

    private String dateOfBirth;  // Patient's date of birth
    private String bloodType;    // Patient's blood type

    // Constructor to initialize Patient-specific attributes
    public Patient(String userId, String firstName, String lastName, String gender, String contactNumber,
            String emailAddress, String userRole, String dateOfBirth, String bloodType) {
        // Using the constructor of the abstract User class to initialize common attributes
        super(userId, firstName, lastName, gender, dateOfBirth, contactNumber, emailAddress, userRole);
        this.bloodType = bloodType;  // Set the patient's blood type
    }

    // Getter for blood type
    public String getBloodType() {
        return bloodType; // Return the blood type
    }

    // Implement the abstract method from User to return the user's role
    @Override
    public String getUserRole() {
        return "Patient"; // Return the role specific to this class
    }

    // Override registrationString to return formatted registration details for Patient
    @Override
    public String registrationString() {
        return getUserId() + "|" + getFirstName() + "|" + getLastName() + "|" + getGender() + "|" + getContactNumber()
                + "|" + getEmailAddress() + "|" + getUserRole() + "|" + getDateOfBirth() + "|" + getBloodType();
    }

    // Override toString to return the formatted string representation of Patient's details
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
