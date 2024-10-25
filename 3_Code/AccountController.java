
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import utility.FileUtils;

public class AccountController {

    private static final String ACCOUNT_TXT = "data/account.txt";
    private static final String PATIENT_TXT = "data/patient.txt";
    // private static final String DOCTOR_CSV = "data/doctor.txt";
    // private static final String PHARMACIST_CSV = "data/pharmacist.txt";
    // private static final String ADMINISTRATOR_CSV = "data/administrator.txt";

    // Register method to add new patient
    public boolean register() { // should take in an attribute called String type

        String firstName = "";
        String lastName = "";
        String gender = "";
        String contactNumber = "";
        String emailAddress = "";
        String bloodType = "";
        String day = "";
        String month = "";
        String year = "";

        Scanner scanner = new Scanner(System.in);

        // Input and validation for first name
        while (firstName.length() < 1 || firstName.length() > 15) {
            System.out.print("Enter first name (1-15 characters): ");
            firstName = scanner.nextLine().trim();
            if (firstName.length() < 1 || firstName.length() > 15) {
                System.out.println("First name must be between 1 and 15 characters. Please try again.");
            }
        }

        // Input and validation for last name
        while (lastName.length() < 1 || lastName.length() > 15) {
            System.out.print("Enter last name (1-15 characters): ");
            lastName = scanner.nextLine().trim();
            if (lastName.length() < 1 || lastName.length() > 15) {
                System.out.println("Last name must be between 1 and 15 characters. Please try again.");
            }
        }

        // Check if patient exists
        if (checkIfPatientExists(firstName, lastName)) {
            System.out.println("Patient already exists!");
            return false;
        }

        // Generate userId (starting from Pa0001)
        String userId = generateUserId();

        while (!(gender.equals("Male") || gender.equals("Female") || gender.equals("Other"))) {
            System.out.println("Choose gender:");
            System.out.println("1. Male");
            System.out.println("2. Female");
            System.out.println("3. Other");
            System.out.print("Enter choice (1-3): ");
            gender = scanner.nextLine().trim();

            switch (gender) {
                case "1":
                    gender = "Male";
                    break;
                case "2":
                    gender = "Female";
                    break;
                case "3":
                    gender = "Other";
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 3.");
            }
        }

        while (contactNumber.length() < 8 || contactNumber.length() > 15) {
            System.out.print("Enter contact number (8-15 digits): ");
            contactNumber = scanner.nextLine().trim();
            if (contactNumber.length() < 8 || contactNumber.length() > 15) {
                System.out.println("Contact number must be between 8 and 15 digits. Please try again.");
            }
        }

        while (!emailAddress.contains("@") || !emailAddress.contains(".")) {
            System.out.print("Enter a valid email address: ");
            emailAddress = scanner.nextLine().trim();
            if (!emailAddress.contains("@") || !emailAddress.contains(".")) {
                System.out.println("Invalid email format. Please include '@' and a domain.");
            }
        }

        // Day input
        while (day.length() != 2 || !day.matches("\\d{2}")) {
            System.out.print("Enter day (DD): ");
            day = scanner.nextLine().trim();
            if (day.length() != 2 || !day.matches("\\d{2}")) {
                System.out.println("Invalid day. Please enter a two-digit day (e.g., 01, 15, 31).");
            }
        }

        // Month input
        while (month.length() != 2 || !month.matches("\\d{2}") || Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12) {
            System.out.print("Enter month (MM): ");
            month = scanner.nextLine().trim();
            if (month.length() != 2 || !month.matches("\\d{2}") || Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12) {
                System.out.println("Invalid month. Please enter a valid two-digit month (e.g., 01 for January, 12 for December).");
            }
        }

        // Year input
        while (year.length() != 4 || !year.matches("\\d{4}")) {
            System.out.print("Enter year (YYYY): ");
            year = scanner.nextLine().trim();
            if (year.length() != 4 || !year.matches("\\d{4}")) {
                System.out.println("Invalid year. Please enter a four-digit year (e.g., 1990, 2023).");
            }
        }

        // Combine into final date format
        String dateOfBirth = day + "-" + month + "-" + year;

        while (!(bloodType.equals("1") || bloodType.equals("2") || bloodType.equals("3") || bloodType.equals("4"))) {
            System.out.println("Choose blood type:");
            System.out.println("1. A");
            System.out.println("2. B");
            System.out.println("3. AB");
            System.out.println("4. O");
            System.out.print("Enter choice (1-4): ");
            bloodType = scanner.nextLine().trim();
            switch (bloodType) {
                case "1":
                    bloodType = "A";
                    break;
                case "2":
                    bloodType = "B";
                    break;
                case "3":
                    bloodType = "AB";
                    break;
                case "4":
                    bloodType = "O";
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 4.");
            }
        }

        // Create patient and account objects
        Patient newPatient = new Patient(userId, firstName, lastName, gender, contactNumber, emailAddress, "Patient", dateOfBirth, bloodType);
        // Account newAccount = new Account(userId, "password", newPatient); // Or prompt for password if needed
        // Write to files
        FileUtils.writeToFile(PATIENT_TXT, newPatient.toString());
        FileUtils.writeToFile(ACCOUNT_TXT, userId + "|" + "password");

        System.out.println("Patient registered successfully! You may now log in.");
        System.out.println("Your account's credentials are: " + userId + " | \"password\".");
        System.out.println("Please remember to change your default password!");

        return true;
    }

    /* HELPER FUNCTIONS BELOW */
    private String generateUserId() {
        // Helper method to generate userId
        int idCounter = 1;
        String userId;
        try {
            long lineCount = Files.lines(Paths.get(PATIENT_TXT)).count();
            idCounter += lineCount;
            userId = String.format("PA%05d", idCounter);
        } catch (IOException e) {
            e.printStackTrace();
            userId = "PA0001";
        }
        return userId;
    }

    private boolean checkIfPatientExists(String firstName, String lastName) {
        // Normalize the input by removing spaces and converting to lowercase
        String normalizedInput = (firstName + "|" + lastName).replaceAll("\\s+", "").toLowerCase();

        try {
            return Files.lines(Paths.get(PATIENT_TXT))
                    // Normalize each line: remove spaces and convert to lowercase
                    .map(line -> line.replaceAll("\\s+", "").toLowerCase())
                    // Check if any normalized line matches the normalized input
                    .anyMatch(normalizedLine -> normalizedLine.contains(normalizedInput));

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
