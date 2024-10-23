
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AccountController {

    private static final String ACCOUNT_CSV = "data/account.csv";
    private static final String USER_CSV = "data/user.csv";

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
                    // If the credentials match, retrieve the user details using the userId
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

                    // Create and return a User object (you can extend this for specific roles later)
                    // generate patientId
                    String patientId = "P0001";
                    return new Patient(userId, firstName, lastName, patientId);  // Example with GenericUser (see below)
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the user CSV file.");
            e.printStackTrace();
        }

        // Return null if the user is not found
        return null;
    }

    public void logout() {
        System.out.println("Logged out.");
    }
}
