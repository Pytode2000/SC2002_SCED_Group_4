
public class Patient extends User {

    private String patientId;

    // Constructor
    public Patient(String userId, String firstName, String lastName, String patientId) {
        // Using the constructor of the abstract User class.
        super(userId, firstName, lastName);
        this.patientId = patientId; // temp. delete later
    }

    // Implement the abstract method from User to return the role.
    @Override
    public String getRole() {
        return "Patient";
    }

    // // Optional: Override toString() method for easy display
    // @Override
    // public String toString() {
    //     return "Patient [ID=" + getUserId() + ", Name=" + getName() + ", Contact=" + getContactInfo()
    //             + ", Medical History=" + medicalHistory + "]";
    // }
}
