
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AccountController {

    private static final String ACCOUNT_CSV = "data/account.csv";
    private static final String USER_CSV = "data/user.csv";
    private static final String PATIENT_CSV = "data/patient.csv";

    // Method to validate login by checking the account CSV for matching userId and password
    public Account login(String userId, String password) {
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNT_CSV))) {
            while ((line = br.readLine()) != null) {
                String[] accountData = line.split(csvSplitBy);
                String csvUserId = accountData[0];
                String csvPassword = accountData[1];

                // Check if the userId and password match
                if (csvUserId.equals(userId) && csvPassword.equals(password)) {
                    // Retrieve user details using the userId
                    User user = getUserFromCSV(userId);
                    if (user != null) {
                        return new Account(userId, password, user);  // Return the Account with the associated User
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the account CSV file.");
            e.printStackTrace();
        }

        // Return null if login fails
        System.out.println("Login failed: invalid userId or password.");
        return null;
    }

    // Method to retrieve user information from the user CSV based on userId
    private User getUserFromCSV(String userId) {
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(USER_CSV))) {
            while ((line = br.readLine()) != null) {
                String[] userData = line.split(csvSplitBy);
                if (userData[0].equals(userId)) {
                    String firstName = userData[1];
                    String lastName = userData[2];
                    String role = "Patient";

                    // If the user is a patient, get patient-specific details
                    if (role.equalsIgnoreCase("Patient")) {
                        // String patientId = userData[4];
                        String patientId = "P001";
                        return new Patient(userId, firstName, lastName, patientId);
                    }
                    // Extend this with other roles (like Doctor) later
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the user CSV file.");
            e.printStackTrace();
        }

        // Return null if the user is not found
        return null;
    }

    // Registration method to add new user to the CSV files
    public boolean register(String userId, String firstName, String lastName, String password, String role, String patientId) {
        // Check if the userId already exists
        if (getUserFromCSV(userId) != null) {
            return false;  // User already exists
        }

        // Write to the account.csv file
        try (BufferedWriter accountWriter = new BufferedWriter(new FileWriter(ACCOUNT_CSV, true))) {
            accountWriter.write(userId + "," + password);
            accountWriter.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to account CSV.");
            e.printStackTrace();
            return false;
        }

        // Write to the user.csv file (handling role-specific details like patientId)
        try (BufferedWriter userWriter = new BufferedWriter(new FileWriter(USER_CSV, true))) {
            if (role.equalsIgnoreCase("Patient")) {
                userWriter.write(userId + "," + firstName + "," + lastName + "," + role + "," + patientId);
            } else {
                // Extend this for other roles (e.g., Doctor)
                userWriter.write(userId + "," + firstName + "," + lastName + "," + role);
            }
            userWriter.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to user CSV.");
            e.printStackTrace();
            return false;
        }

        return true;  // Registration successful
    }
}
