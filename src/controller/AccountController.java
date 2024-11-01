package controller;

import entity.Administrator;
import entity.Doctor;
import entity.Patient;
import entity.Pharmacist;
import entity.User;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Scanner;
import utility.FileUtils;

public class AccountController {

    private static final String ACCOUNT_TXT = "data/account.txt";
    private static final String PATIENT_TXT = "data/patient.txt";
    private static final String STAFF_TXT = "data/staff.txt";

    // Register method to add new patient
    public boolean register(boolean isAdmin) {

        String firstName = "";
        String lastName = "";
        String gender = "";
        String contactNumber = "";
        String emailAddress = "";
        String bloodType = "";
        String day = "";
        String month = "";
        String year = "";
        String dateOfBirth = "";
        String userRole = "Patient";

        Scanner scanner = new Scanner(System.in);

        if (isAdmin) {
            while (!(userRole.equals("Doctor") || userRole.equals("Pharmacist") || userRole.equals("Administrator"))) {
                System.out.println("Choose Role:");
                System.out.println("1. Doctor");
                System.out.println("2. Pharmacist");
                System.out.println("3. Administrator");
                System.out.print("Enter choice (1-3): ");
                userRole = scanner.nextLine().trim();

                switch (userRole) {
                    case "1":
                        userRole = "Doctor";
                        break;
                    case "2":
                        userRole = "Pharmacist";
                        break;
                    case "3":
                        userRole = "Administrator";
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 3.");
                }
            }
        }
        // Generate userId (starting from PA00001 for patient, differs for staff)
        String userId = generateUserId(userRole);

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
        if (checkIfUserExists(firstName, lastName, isAdmin)) {
            if (isAdmin) {
                System.out.println("Staff already exists!");

            } else {
                System.out.println("Patient already exists!");
            }
            return false;
        }

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

        while (!isValidContactNumber(contactNumber)) {
            System.out.print("Enter contact number (8-15 digits): ");
            contactNumber = scanner.nextLine().trim();
            if (contactNumber.length() < 8 || contactNumber.length() > 15) {
                System.out.println("Contact number must be between 8 and 15 digits. Please try again.");
            }
        }

        while (!isValidEmail(emailAddress)) {
            System.out.print("Enter a valid email address: ");
            emailAddress = scanner.nextLine().trim();
            if (!emailAddress.contains("@") || !emailAddress.contains(".")) {
                System.out.println("Invalid email format. Please include '@' and a domain.");
            }
        }

        if (!isAdmin) {
// Day input
            System.out.println("Enter date of birth: ");
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
            dateOfBirth = day + "-" + month + "-" + year;

            while (!(bloodType.equals("A+") || bloodType.equals("A-") || bloodType.equals("B+") || bloodType.equals("B-") || bloodType.equals("AB+") || bloodType.equals("AB-") || bloodType.equals("O+") || bloodType.equals("O-"))) {
                System.out.println("Choose blood type:");
                System.out.println("1. A+");
                System.out.println("2. A-");
                System.out.println("3. B+");
                System.out.println("4. B-");
                System.out.println("5. AB+");
                System.out.println("6. AB-");
                System.out.println("7. O+");
                System.out.println("8. O-");
                System.out.print("Enter choice (1-8): ");
                bloodType = scanner.nextLine().trim();
                switch (bloodType) {
                    case "1":
                        bloodType = "A+";
                        break;
                    case "2":
                        bloodType = "A-";
                        break;
                    case "3":
                        bloodType = "B+";
                        break;
                    case "4":
                        bloodType = "B-";
                        break;
                    case "5":
                        bloodType = "AB+";
                        break;
                    case "6":
                        bloodType = "AB-";
                        break;
                    case "7":
                        bloodType = "O+";
                        break;
                    case "8":
                        bloodType = "O-";
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 8.");
                }
            }
        }

// Determine the file and object creation based on userRole
        User newUser;
        String roleFile;

        if (userRole.equals("Patient")) {
            // Create Patient object and set to PATIENT_TXT
            newUser = new Patient(userId, firstName, lastName, gender, contactNumber, emailAddress, userRole, dateOfBirth, bloodType);
            roleFile = PATIENT_TXT;
        } else {
            // Create a Staff object based on userRole and set to STAFF_TXT
            switch (userRole) {
                case "Doctor":
                    newUser = new Doctor(userId, firstName, lastName, gender, contactNumber, emailAddress, userRole);
                    break;
                case "Administrator":
                    newUser = new Administrator(userId, firstName, lastName, gender, contactNumber, emailAddress, userRole);
                    break;
                case "Pharmacist":
                    newUser = new Pharmacist(userId, firstName, lastName, gender, contactNumber, emailAddress, userRole);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid user role: " + userRole);
            }
            roleFile = STAFF_TXT;
        }

        // Write to the appropriate file based on the user role
        FileUtils.writeToFile(roleFile, newUser.toString());
        FileUtils.writeToFile(ACCOUNT_TXT, userId + "|" + hashPassword("password"));

        // Display success message
        System.out.println(userRole + " registered successfully!");
        System.out.println("The account's credentials are: " + userId + " | \"password\".");
        return true;
    }

    public User login() {
        Scanner scanner = new Scanner(System.in);
        int attempt = 0;
        while (attempt < 3) {
            attempt++;
            System.out.print("Enter User ID (e.g., PA00001): ");
            String inputUserId = scanner.nextLine().trim();

            System.out.print("Enter Password: ");
            String inputPassword = scanner.nextLine().trim();

            // Step 1: Verify User ID and Password from account.txt
            if (authenticate(inputUserId, hashPassword(inputPassword))) {
                // Step 2: Load User details if authentication succeeds
                User user = loadUserDetails(inputUserId);
                if (user != null) {
                    System.out.println("Login successful. Welcome, " + user.getFirstName());
                    return user;
                }

            } else {
                System.out.println("Incorrect User ID or Password, " + (3 - attempt) + " more attempts.");
            }
        }

        System.out.println("Login failed.");
        return null;
    }

    /* HELPER FUNCTIONS BELOW */
    private String generateUserId(String userRole) {
        String prefix;
        switch (userRole) {
            case "Patient":
                prefix = "PA";
                break;
            case "Doctor":
                prefix = "DR";
                break;
            case "Administrator":
                prefix = "AM";
                break;
            case "Pharmacist":
                prefix = "PH";
                break;
            default:
                throw new IllegalArgumentException("Invalid user role: " + userRole);
        }

        int maxId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNT_TXT))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] accountData = line.split("\\|");
                String userId = accountData[0];

                // Check if userId starts with the prefix
                if (userId.startsWith(prefix)) {
                    int idNumber = Integer.parseInt(userId.substring(2)); // Extract the numeric part
                    maxId = Math.max(maxId, idNumber); // Track the highest ID number
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Increment the highest found ID number by 1
        int newIdNumber = maxId + 1;
        return String.format("%s%05d", prefix, newIdNumber); // Format as "PREFIX00001"
    }

    private boolean checkIfUserExists(String firstName, String lastName, boolean isAdmin) {

        String fileToUse;
        if (isAdmin) {
            fileToUse = STAFF_TXT;
        } else {
            fileToUse = PATIENT_TXT;
        }
        // Normalize the input by removing spaces and converting to lowercase
        String normalizedInput = (firstName + "|" + lastName).replaceAll("\\s+", "").toLowerCase();

        try {
            return Files.lines(Paths.get(fileToUse))
                    // Normalize each line: remove spaces and convert to lowercase
                    .map(line -> line.replaceAll("\\s+", "").toLowerCase())
                    // Check if any normalized line matches the normalized input
                    .anyMatch(normalizedLine -> normalizedLine.contains(normalizedInput));

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return HexFormat.of().formatHex(hashedBytes); // Convert bytes to hex string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password!", e);
        }
    }

    private boolean authenticate(String userId, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(ACCOUNT_TXT))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] accountData = line.split("\\|");
                if (accountData[0].equals(userId) && accountData[1].equals(password)) {
                    return true; // Successful authentication
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private User loadUserDetails(String userId) {
        // First, check in patient.txt for a Patient record
        try (BufferedReader br = new BufferedReader(new FileReader(PATIENT_TXT))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userData = line.split("\\|");
                if (userData[0].equals(userId)) {
                    // Assuming Patient has data fields that include specific fields such as bloodType and dateOfBirth
                    return new Patient(userId, userData[1], userData[2], userData[3],
                            userData[4], userData[5], "Patient", userData[6], userData[7]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If not found in patient.txt, check in staff.txt for Staff records
        try (BufferedReader br = new BufferedReader(new FileReader(STAFF_TXT))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userData = line.split("\\|");
                if (userData[0].equals(userId)) {
                    String role = userData[6]; // Assuming role is at index 6

                    switch (role) {
                        case "Doctor":
                            return new Doctor(userId, userData[1], userData[2], userData[3],
                                    userData[4], userData[5], role);

                        case "Administrator":
                            return new Administrator(userId, userData[1], userData[2], userData[3],
                                    userData[4], userData[5], role);

                        case "Pharmacist":
                            return new Pharmacist(userId, userData[1], userData[2], userData[3],
                                    userData[4], userData[5], role);

                        default:
                            System.out.println("Unknown role: " + role);
                            return null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // User not found in either file
        return null;
    }

    public void updatePersonalInformation(User user) {
        Scanner scanner = new Scanner(System.in);
        boolean updating = true;

        while (updating) {
            System.out.println("\n--- Update Personal Information ---");
            System.out.println("User ID: " + user.getUserId());
            if (user instanceof Patient) {
                System.out.println("Current Contact Number: " + ((Patient) user).getContactNumber());
                System.out.println("Current Email Address: " + ((Patient) user).getEmailAddress());
            }

            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Password");
            if (user instanceof Patient) {
                System.out.println("2. Contact Number");
                System.out.println("3. Email Address");
            }
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    updatePasswordFlow(user, scanner);
                    break;
                case "2":
                    if (user instanceof Patient) {
                        updateContactNumberFlow((Patient) user, scanner);
                    }
                    break;
                case "3":
                    if (user instanceof Patient) {
                        updateEmailAddressFlow((Patient) user, scanner);
                    }
                    break;
                case "0":
                    updating = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

// Handles the update password flow for all users with a 3-attempt limit for both current and new passwords
    private void updatePasswordFlow(User user, Scanner scanner) {
        int currentPasswordAttempts = 0;
        boolean currentPasswordCorrect = false;

        // First, verify current password with up to 3 attempts
        while (currentPasswordAttempts < 3 && !currentPasswordCorrect) {
            System.out.print("Enter current password: ");
            String currentPassword = scanner.nextLine().trim();

            if (authenticate(user.getUserId(), hashPassword(currentPassword))) {
                currentPasswordCorrect = true;
            } else {
                currentPasswordAttempts++;
                System.out.println("Incorrect current password. " + (3 - currentPasswordAttempts) + " attempt(s) remaining.");
            }
        }

        // If the current password is correct, proceed to update the password with a new one
        if (currentPasswordCorrect) {
            int newPasswordAttempts = 0;
            boolean passwordUpdated = false;

            while (newPasswordAttempts < 3 && !passwordUpdated) {
                System.out.print("Enter new password (min 8 chars, 1 digit, 1 special char): ");
                String newPassword = scanner.nextLine().trim();

                if (isValidPassword(newPassword)) {
                    passwordUpdated = updatePassword(user.getUserId(), newPassword);
                    System.out.println(passwordUpdated ? "Password updated successfully." : "Password update failed.");
                } else {
                    newPasswordAttempts++;
                    System.out.println("Invalid password format. " + (3 - newPasswordAttempts) + " attempt(s) remaining.");
                }
            }

            // If user fails to enter a valid new password after 3 attempts
            if (!passwordUpdated && newPasswordAttempts >= 3) {
                System.out.println("Failed to update password. Exceeded maximum attempts.");
            }
        } else {
            System.out.println("Password update failed. Exceeded maximum attempts for current password.");
        }
    }

// Update password in the account file after current password authentication
    public boolean updatePassword(String userId, String newPassword) {
        try {
            return updateAccountFile(userId, hashPassword(newPassword), ACCOUNT_TXT, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

// Handles updating contact number for patients only, with 3 attempts
    private void updateContactNumberFlow(Patient patient, Scanner scanner) {
        int attempts = 0;
        boolean contactUpdated = false;

        while (attempts < 3 && !contactUpdated) {
            System.out.print("Enter new contact number (8-15 digits): ");
            String newContactNumber = scanner.nextLine().trim();

            if (isValidContactNumber(newContactNumber)) {
                if (updateContactNumber(patient.getUserId(), newContactNumber)) {
                    patient.setContactNumber(newContactNumber);
                    System.out.println("Contact number updated successfully.");
                    contactUpdated = true;
                } else {
                    System.out.println("Failed to update contact number. Please try again.");
                }
            } else {
                attempts++;
                System.out.println("Invalid contact number format. " + (3 - attempts) + " attempt(s) remaining.");
            }
        }

        if (!contactUpdated && attempts >= 3) {
            System.out.println("Failed to update contact number. Exceeded maximum attempts.");
        }
    }

// Handles updating email address for patients only, with 3 attempts
    private void updateEmailAddressFlow(Patient patient, Scanner scanner) {
        int attempts = 0;
        boolean emailUpdated = false;

        while (attempts < 3 && !emailUpdated) {
            System.out.print("Enter new valid email address: ");
            String newEmail = scanner.nextLine().trim();

            if (isValidEmail(newEmail)) {
                if (updateEmailAddress(patient.getUserId(), newEmail)) {
                    patient.setEmailAddress(newEmail);
                    System.out.println("Email address updated successfully.");
                    emailUpdated = true;
                } else {
                    System.out.println("Failed to update email address. Please try again.");
                }
            } else {
                attempts++;
                System.out.println("Invalid email format. " + (3 - attempts) + " attempt(s) remaining.");
            }
        }

        if (!emailUpdated && attempts >= 3) {
            System.out.println("Failed to update email address. Exceeded maximum attempts.");
        }
    }

    public boolean updateContactNumber(String userId, String newContactNumber) {
        try {
            return updateAccountFile(userId, newContactNumber, PATIENT_TXT, 4);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEmailAddress(String userId, String newEmailAddress) {
        try {
            return updateAccountFile(userId, newEmailAddress, PATIENT_TXT, 5);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // General method to update a specific field in a file by userId
    private boolean updateAccountFile(String userId, String newValue, String filePath, int fieldIndex) throws IOException {
        List<String> fileContent = Files.readAllLines(Paths.get(filePath));
        boolean updated = false;

        for (int i = 0; i < fileContent.size(); i++) {
            String[] line = fileContent.get(i).split("\\|");
            if (line[0].equals(userId)) {
                line[fieldIndex] = newValue;
                fileContent.set(i, String.join("|", line));
                updated = true;
                break;
            }
        }

        if (updated) {
            Files.write(Paths.get(filePath), fileContent);
            System.out.println("Update successful for userId: " + userId);
        } else {
            System.out.println("User with userId: " + userId + " not found.");
        }
        return updated;
    }

    // Helper validation methods
    private boolean isValidContactNumber(String contactNumber) {
        return contactNumber.length() >= 8 && contactNumber.length() <= 15;
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+=-]).{8,}$";
        return password.matches(passwordPattern);
    }

}