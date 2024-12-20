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
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Scanner;
import utility.FileUtils;
import utility.PrintUtils;

public class AccountController {

    private static final String ACCOUNT_TXT = "data/account.txt";
    private static final String PATIENT_TXT = "data/patient.txt";
    private static final String STAFF_TXT = "data/staff.txt";

    /**
     * Registers a new user.
     * 
     * The method asks the user for their first name, last name, gender, contact
     * number, email address,
     * date of birth, and blood type (for patients only). It then validates the
     * input and checks if the user
     * already exists in the system. If the user does not exist, it creates a new
     * user object and writes it to
     * the appropriate file (either {@code PATIENT_TXT} or {@code STAFF_TXT})
     * depending on the user role.
     * Finally, it displays a success message.
     * 
     * If the user cancels the registration process at any point, the method returns
     * false.
     * 
     * The method does not handle any exceptions that may occur when writing to the
     * files.
     *
     * @param isAdmin whether to register a staff member (true) or a patient (false)
     * @return true if the registration is successful, false otherwise
     */
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
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║              Register Staff            ║");
            System.out.println("╚════════════════════════════════════════╝");

            while (!(userRole.equals("Doctor") || userRole.equals("Pharmacist") || userRole.equals("0"))) {
                System.out.println("Choose Role (0 to cancel):");
                System.out.println("1. Doctor");
                System.out.println("2. Pharmacist");
                // System.out.println("3. Administrator");
                System.out.println("══════════════════════════════════════════");
                System.out.print("Enter choice: ");
                userRole = scanner.nextLine().trim();

                switch (userRole) {
                    case "0":
                        return false;
                    case "1":
                        userRole = "Doctor";
                        break;
                    case "2":
                        userRole = "Pharmacist";
                        break;
                    // case "3":
                    // userRole = "Administrator";
                    // break;
                    default:
                        System.out.println("Invalid choice. Please enter either '1' or '2'.");
                }
            }
        }

        if (userRole.equals("0")) {
            return false;
        }

        // Generate userId (starting from PA00001 for patient, differs for staff)
        String userId = generateUserId(userRole);

        // Input and validation for first name
        boolean firstNameValid = false;
        while (!firstNameValid) {
            System.out.print("Enter first name (1-15 characters, 0 to cancel): ");
            firstName = scanner.nextLine().trim();
            if (firstName.equals("0")) {
                return false;
            }
            if (firstName.length() < 1 || firstName.length() > 15 || !firstName.matches("^[a-zA-Z]+$")) {
                System.out.println(
                        "First name must be between 1 and 15 characters and contain only alphabetic characters. Please try again.");
            } else {
                firstNameValid = true;
            }
        }

        // Input and validation for last name
        boolean lastNameValid = false;
        while (!lastNameValid) {
            System.out.print("Enter last name (1-15 characters, 0 to cancel): ");
            lastName = scanner.nextLine().trim();
            if (lastName.equals("0")) {
                return false;
            }
            if (lastName.length() < 1 || lastName.length() > 15 || !lastName.matches("^[a-zA-Z]+$")) {
                System.out.println(
                        "Last name must be between 1 and 15 characters and contain only alphabetic characters. Please try again.");
            } else {
                lastNameValid = true;
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

        while (!(gender.equals("Male") || gender.equals("Female") || gender.equals("Other") || gender.equals("0"))) {
            System.out.println("Choose gender (0 to cancel):");
            System.out.println("1. Male");
            System.out.println("2. Female");
            System.out.println("3. Other");
            System.out.print("Enter choice (1-3): ");
            gender = scanner.nextLine().trim();

            switch (gender) {
                case "0":
                    return false;
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
            System.out.print("Enter contact number (8-15 digits, 0 to cancel): ");
            contactNumber = scanner.nextLine().trim();
            if (contactNumber.equals("0")) {
                return false;
            }
            if (contactNumber.length() < 8 || contactNumber.length() > 15) {
                System.out.println("Contact number must be between 8 and 15 digits. Please try again.");
            }
        }

        while (!isValidEmail(emailAddress)) {
            System.out.print("Enter a valid email address (0 to cancel): ");
            emailAddress = scanner.nextLine().trim();
            if (emailAddress.equals("0")) {
                return false;
            }
            if (!emailAddress.contains("@") || !emailAddress.contains(".")) {
                System.out.println("Invalid email format. Please include '@' and a domain.");
            }
        }

        // Day input
        System.out.println("Enter date of birth (0 to cancel): ");
        while (day.length() != 2 || !day.matches("\\d{2}") || Integer.parseInt(day) < 1 || Integer.parseInt(day) > 31) {
            System.out.print("Enter day (DD): ");
            day = scanner.nextLine().trim();
            if (day.equals("0")) {
                return false;
            }
            if (day.length() != 2 || !day.matches("\\d{2}") || Integer.parseInt(day) < 1
                    || Integer.parseInt(day) > 31) {
                System.out.println("Invalid day. Please enter a two-digit day (e.g., 01, 15, 31).");
            }
        }

        // Month input
        while (month.length() != 2 || !month.matches("\\d{2}") || Integer.parseInt(month) < 1
                || Integer.parseInt(month) > 12) {
            System.out.print("Enter month (MM): ");
            month = scanner.nextLine().trim();
            if (month.equals("0")) {
                return false;
            }
            if (month.length() != 2 || !month.matches("\\d{2}") || Integer.parseInt(month) < 1
                    || Integer.parseInt(month) > 12) {
                System.out.println(
                        "Invalid month. Please enter a valid two-digit month (e.g., 01 for January, 12 for December).");
            }
        }

        // Year input
        while (year.length() != 4 || !year.matches("\\d{4}") || Integer.parseInt(year) < 1900
                || Integer.parseInt(year) > LocalDate.now().getYear()) {
            System.out.print("Enter year (YYYY, 0 to cancel): ");
            year = scanner.nextLine().trim();
            if (year.equals("0")) {
                return false;
            }
            if (year.length() != 4 || !year.matches("\\d{4}") || Integer.parseInt(year) < 1900
                    || Integer.parseInt(year) > LocalDate.now().getYear()) {
                System.out.println("Invalid year. Please enter a four-digit year (e.g., 1990, 2023).");
            }
        }

        // Combine into final date format
        dateOfBirth = day + "-" + month + "-" + year;

        if (!isAdmin) {
            while (!(bloodType.equals("A+") || bloodType.equals("A-") || bloodType.equals("B+")
                    || bloodType.equals("B-") || bloodType.equals("AB+") || bloodType.equals("AB-")
                    || bloodType.equals("O+") || bloodType.equals("O-") || bloodType.equals("0"))) {
                System.out.println("Choose blood type (0 to cancel):");
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
                    case "0":
                        return false;
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
            newUser = new Patient(userId, firstName, lastName, gender, contactNumber, emailAddress, userRole,
                    dateOfBirth, bloodType);
            roleFile = PATIENT_TXT;
        } else {
            // Create a Staff object based on userRole and set to STAFF_TXT
            switch (userRole) {
                case "Doctor":
                    newUser = new Doctor(userId, firstName, lastName, gender, dateOfBirth, contactNumber, emailAddress,
                            userRole);
                    break;
                // case "Administrator":
                // newUser = new Administrator(userId, firstName, lastName, gender, dateOfBirth,
                // contactNumber,
                // emailAddress, userRole);
                // break;
                case "Pharmacist":
                    newUser = new Pharmacist(userId, firstName, lastName, gender, dateOfBirth, contactNumber,
                            emailAddress, userRole);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid user role: " + userRole);
            }
            roleFile = STAFF_TXT;
        }

        // Write to the appropriate file based on the user role
        FileUtils.writeToFile(roleFile, newUser.registrationString());
        FileUtils.writeToFile(ACCOUNT_TXT, userId + "|" + hashPassword("password"));

        // Display success message
        System.out.println(userRole + " registered successfully!");
        System.out.println("The account's credentials are: " + userId + " | \"password\".");
        PrintUtils.pause();
        return true;

    }

    /**
     * Authenticates the user based on their User ID and Password.
     * This method prompts the user to enter their credentials and performs the
     * following:
     * 1. Verifies the User ID and Password against stored records.
     * 2. If the credentials are correct, loads the user's details.
     * 3. If the user is still using the default password, forces a password change.
     * 4. If the password is updated successfully, the user is granted access.
     * 5. In case of failure to authenticate, the user will be given up to 3
     * attempts to try again.
     * 
     * @return User object if authentication and password update are successful,
     *         null if the login fails or if the user fails to update their password
     *         after multiple attempts.
     */
    public User login() {
        Scanner scanner = new Scanner(System.in);
        int attempt = 0;
        String defaultHashedPassword = hashPassword("password");

        while (attempt < 3) {
            attempt++;
            System.out.print("Enter User ID (e.g., PA00001): ");
            String inputUserId = scanner.nextLine().trim();

            System.out.print("Enter Password: ");
            String inputPassword = scanner.nextLine().trim();

            // Step 1: Verify User ID and Password from account.txt
            if (authenticate(inputUserId.toUpperCase(), hashPassword(inputPassword))) {
                // Step 2: Load User details if authentication succeeds
                User user = loadUserDetails(inputUserId.toUpperCase());
                if (user != null) {
                    // Check if the password is still the default hashed "password"
                    if (authenticate(inputUserId.toUpperCase(), defaultHashedPassword)) {
                        System.out.println("You are using the default password. Please change it.");

                        // Force the user to change their password
                        boolean passwordUpdated = false;
                        int newPasswordAttempts = 0;

                        while (newPasswordAttempts < 3 && !passwordUpdated) {
                            System.out.print("Enter new password (min 8 chars, 1 digit, 1 special char): ");
                            String newPassword = scanner.nextLine().trim();

                            if (isValidPassword(newPassword)) {
                                passwordUpdated = updatePassword(user.getUserId(), newPassword);
                                System.out.println(
                                        passwordUpdated ? "Password updated successfully." : "Password update failed.");
                            } else {
                                newPasswordAttempts++;
                                System.out.println("Invalid password format. " + (3 - newPasswordAttempts)
                                        + " attempt(s) remaining.");
                            }
                        }

                        if (!passwordUpdated) {
                            System.out.println("Failed to update password. Exiting login.");
                            return null; // Exit the login if the user fails to update their password
                        }
                    }

                    System.out.println("══════════════════════════════════════════\n");

                    System.out.println("Welcome, " + user.getFirstName() + " " + user.getLastName() + "!");

                    return user;
                }

            } else {
                System.out.println("Incorrect User ID or Password, " + (3 - attempt) + " more attempts.");
            }
        }

        System.out.println("Login failed.");
        return null;
    }

    /**
     * Generates a unique user ID based on the given user role. The user ID is in
     * the format
     * "PREFIX00001", where PREFIX is a 2-character code based on the user role
     * (e.g. "PA" for Patient,
     * "DR" for Doctor, etc.). The numeric part is the highest existing ID number in
     * the account file
     * plus 1.
     *
     * @param userRole the user role to generate the user ID for
     * @return a unique user ID string
     * @throws IllegalArgumentException if the user role is invalid
     */
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

    /**
     * Checks if a user exists in the file specified by {@code isAdmin}.
     * <p>
     * The method reads the file line by line, normalizes each line by removing
     * spaces and converting to lowercase, and checks if any normalized line
     * contains the normalized input (first name and last name, also normalized).
     * If a match is found, the method returns true. Otherwise, it returns false.
     * <p>
     * If an I/O exception occurs, the method prints the stack trace and returns
     * false.
     *
     * @param firstName the first name of the user to search for
     * @param lastName  the last name of the user to search for
     * @param isAdmin   whether to search in the staff file (true) or patient file
     *                  (false)
     * @return true if the user is found, false otherwise
     */
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

    /**
     * Hashes the given password using SHA-256 and returns the result as a
     * hexadecimal string.
     *
     * @param password the password to hash
     * @return the hashed password as a hexadecimal string
     * @throws RuntimeException if there is an error hashing the password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return HexFormat.of().formatHex(hashedBytes); // Convert bytes to hex string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password!", e);
        }
    }

    /**
     * Authenticates the given user ID and password by checking against the
     * account records in the account file.
     *
     * @param userId   the user ID to authenticate
     * @param password the password to authenticate
     * @return true if authentication is successful, false otherwise
     */
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

    /**
     * Loads a User object based on the given userId by searching for matching
     * records in patient.txt and staff.txt. If found, the User object is
     * instantiated with the corresponding data fields and returned. If not
     * found, null is returned.
     *
     * @param userId the userId to search for
     * @return the User object if found, or null if not found
     */
    private User loadUserDetails(String userId) {
        // First, check in patient.txt for a Patient record
        try (BufferedReader br = new BufferedReader(new FileReader(PATIENT_TXT))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userData = line.split("\\|");
                if (userData[0].equals(userId)) {
                    // Assuming Patient has data fields that include specific fields such as
                    // bloodType and dateOfBirth
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
                    String role = userData[7]; // Assuming role is at index 7

                    switch (role) {
                        case "Doctor":
                            return new Doctor(userId, userData[1], userData[2], userData[3],
                                    userData[4], userData[5], userData[6], role);

                        case "Administrator":
                            return new Administrator(userId, userData[1], userData[2], userData[3],
                                    userData[4], userData[5], userData[6], role);

                        case "Pharmacist":
                            return new Pharmacist(userId, userData[1], userData[2], userData[3],
                                    userData[4], userData[5], userData[6], role);

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

    /**
     * Allows the user to update their personal information including password,
     * contact number, and email address. This method provides a user interface
     * for selecting which information to update and processes the update
     * accordingly.
     * The user is prompted with a menu displaying their current contact number and
     * email address if they are a Patient. The user can choose to update their
     * password, and if they are a Patient, can also update their contact number and
     * email address. The method continuously prompts the user for their choice
     * until
     * they decide to return to the main menu.
     *
     * @param user the User object whose information is to be updated. This can be
     *             any subclass of User, but additional options are available for
     *             instances of Patient.
     */
    public void updatePersonalInformation(User user) {
        Scanner scanner = new Scanner(System.in);
        boolean updating = true;

        while (updating) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║           Patient Information          ║");
            System.out.println("╚════════════════════════════════════════╝");
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
            System.out.println("══════════════════════════════════════════");
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

    /**
     * Handles the update password flow for all users with a 3-attempt limit for
     * both current and new passwords. First, the user is prompted to enter their
     * current password with up to 3 attempts. If the current password is correct,
     * the user is prompted to enter a new password with up to 3 attempts. The new
     * password is required to have a minimum of 8 characters, at least 1 digit, and
     * at least 1 special character. If the user fails to enter a valid new password
     * after 3 attempts, the method will print a failure message. If the user fails
     * to enter the correct current password after 3 attempts, the method will also
     * print a failure message.
     *
     * @param user    the User object whose password is to be updated
     * @param scanner the Scanner object used for reading user input
     */
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
                System.out.println(
                        "Incorrect current password. " + (3 - currentPasswordAttempts) + " attempt(s) remaining.");
            }
        }

        // If the current password is correct, proceed to update the password with a new
        // one
        if (currentPasswordCorrect) {
            int newPasswordAttempts = 0;
            boolean passwordUpdated = false;

            while (newPasswordAttempts < 3 && !passwordUpdated) {
                System.out.print("Enter new password (min 8 chars, 1 digit, 1 special char): ");
                String newPassword = scanner.nextLine().trim();

                if (isValidPassword(newPassword)) {
                    passwordUpdated = updatePassword(user.getUserId(), newPassword);
                    System.out.println(passwordUpdated ? "Password updated successfully." : "Password update failed.");
                    PrintUtils.pause();
                } else {
                    newPasswordAttempts++;
                    System.out.println(
                            "Invalid password format. " + (3 - newPasswordAttempts) + " attempt(s) remaining.");
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

    /**
     * Updates the password for a given user by hashing the new password and
     * updating the account record in the account file.
     *
     * This method attempts to update the password for the specified user ID
     * by hashing the provided new password and writing it to the designated
     * account text file. The password is stored in its hashed form to ensure
     * security.
     *
     * @param userId      the unique identifier of the user whose password is to be
     *                    updated
     * @param newPassword the new password to be set for the user, which will be
     *                    hashed
     * @return true if the password update is successful, false if an IOException
     *         occurs
     */
    public boolean updatePassword(String userId, String newPassword) {
        try {
            return updateAccountFile(userId, hashPassword(newPassword), ACCOUNT_TXT, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Allows the user to update their contact number with up to 3 attempts.
     *
     * This method prompts the user to enter their new contact number, which is
     * validated before being written to the account file. If the update is
     * successful, the user is notified and the method ends. If the update fails,
     * the user is given up to 3 additional attempts to enter a valid contact
     * number. If all 3 attempts fail, the user is notified and the method ends.
     *
     * @param patient the Patient object whose contact number is to be updated
     * @param scanner the Scanner object used to read user input
     */
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
                    PrintUtils.pause();
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

    /**
     * Allows the user to update their email address with up to 3 attempts.
     *
     * This method prompts the user to enter their new email address, which is
     * validated before being written to the account file. If the update is
     * successful, the user is notified and the method ends. If the update fails,
     * the user is given up to 3 additional attempts to enter a valid email
     * address. If all 3 attempts fail, the user is notified and the method ends.
     *
     * @param patient the Patient object whose email address is to be updated
     * @param scanner the Scanner object used to read user input
     */

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
                    PrintUtils.pause();
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

    /**
     * Updates the contact number for the given user ID in the patient account
     * file. The contact number is written to the file in the 4th field.
     *
     * @param userId           the unique identifier of the patient whose contact
     *                         number is to be updated
     * @param newContactNumber the new contact number to be set for the patient
     * @return true if the contact number is updated successfully, false otherwise
     */
    public boolean updateContactNumber(String userId, String newContactNumber) {
        try {
            return updateAccountFile(userId, newContactNumber, PATIENT_TXT, 4);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the email address for a given patient user ID in the patient account
     * file.
     *
     * This method attempts to update the email address for the specified user ID
     * by writing the new email address to the designated patient text file. The
     * email address is stored in the 5th field of the file.
     *
     * @param userId          the unique identifier of the patient whose email
     *                        address is to be updated
     * @param newEmailAddress the new email address to be set for the patient
     * @return true if the email address update is successful, false if an
     *         IOException occurs
     */
    public boolean updateEmailAddress(String userId, String newEmailAddress) {
        try {
            return updateAccountFile(userId, newEmailAddress, PATIENT_TXT, 5);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates a specific field in the account file for a given user ID.
     * 
     * This method reads the specified account file and searches for an entry with
     * the matching user ID. If found, it updates the field at the specified index
     * with the provided new value, and writes the updated content back to the file.
     * 
     * The method prints a success message if the update is successful, or a message
     * indicating that the user was not found if no matching user ID is present.
     *
     * @param userId     the unique identifier of the user whose record is to be
     *                   updated
     * @param newValue   the new value to set at the specified field index
     * @param filePath   the path to the account file to be updated
     * @param fieldIndex the index of the field to be updated in the file
     * @return true if the update is successful, false if the user ID is not found
     * @throws IOException if an I/O error occurs while reading or writing the file
     */
    private boolean updateAccountFile(String userId, String newValue, String filePath, int fieldIndex)
            throws IOException {
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

    /**
     * Prints out a formatted table of all staff records in the STAFF_TXT file.
     * 
     * The method reads the file line by line, splits each line into fields,
     * calculates the age of each staff member, and prints out the fields in a
     * formatted table. If the file is empty, the method prints a "No results
     * found" message.
     * 
     * The method also prints a header with the column names, and a footer with
     * a horizontal line.
     * 
     * Finally, the method calls {@link PrintUtils#pause()} to pause the console
     * output.
     */
    public void viewStaff() {
        try {
            List<String> staff = Files.readAllLines(Paths.get(STAFF_TXT));

            if (staff.isEmpty()) {
                System.out.println("\nNo results found.");
            } else {
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("║             View All Staff             ║");
                System.out.println("╚════════════════════════════════════════╝");
                System.out.printf("%-5s %-10s %-20s %-20s %-10s %-8s %-18s %-30s %-15s\n",
                        "No.", "User ID", "First Name", "Last Name", "Gender", "Age", "Contact Number", "Email Address",
                        "Role");
                System.out.println("══════════════════════════════════════════");
                for (int i = 0; i < staff.size(); i++) {
                    String[] fields = staff.get(i).split("\\|");
                    int age = calculateAge(LocalDate.parse(fields[4], DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    System.out.printf("%-5d %-10s %-20s %-20s %-10s %-8d %-18s %-30s %-15s\n",
                            i + 1, fields[0], fields[1], fields[2], fields[3], age, fields[5], fields[6], fields[7]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintUtils.pause();
    }

    /**
     * Prints out a formatted table of all staff records with a specific role in
     * the STAFF_TXT file. The role is specified by the user input.
     * 
     * The method reads the file line by line, splits each line into fields,
     * calculates the age of each staff member, and prints out the fields in a
     * formatted table. If the file is empty, the method prints a "No results
     * found" message.
     *
     * The method also prints a header with the column names, and a footer with
     * a horizontal line.
     * 
     * Finally, the method calls {@link PrintUtils#pause()} to pause the console
     * output.
     */
    public void filterByRole(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          Filter Staff by Role          ║");
        System.out.println("╚════════════════════════════════════════╝");

        System.out.println("1. Doctor");
        System.out.println("2. Pharmacist");
        System.out.println("3. Administrator");
        System.out.println("══════════════════════════════════════════");
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine().trim();
        String role = "";
        switch (choice) {
            case "1":
                role = "Doctor";
                break;
            case "2":
                role = "Pharmacist";
                break;
            case "3":
                role = "Administrator";
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                return;
        }

        try {
            List<String> filteredStaff = Files.readAllLines(Paths.get(STAFF_TXT));
            List<String> filtered = new ArrayList<>();
            for (String line : filteredStaff) {
                String[] fields = line.split("\\|");
                if (fields[7].equals(role)) {
                    filtered.add(line);
                }
            }

            if (filtered.isEmpty()) {
                System.out.println("\nNo results found.");
            } else {
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("║             Filtered Staff             ║");
                System.out.println("╚════════════════════════════════════════╝");
                System.out.printf("%-5s %-10s %-20s %-20s %-10s %-8s %-18s %-30s %-15s\n",
                        "No.", "User ID", "First Name", "Last Name", "Gender", "Age", "Contact Number", "Email Address",
                        "Role");
                System.out.println("══════════════════════════════════════════");
                for (int i = 0; i < filtered.size(); i++) {
                    String[] fields = filtered.get(i).split("\\|");
                    int age = calculateAge(LocalDate.parse(fields[4], DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    System.out.printf("%-5d %-10s %-20s %-20s %-10s %-8d %-18s %-30s %-15s\n",
                            i + 1, fields[0], fields[1], fields[2], fields[3], age, fields[5], fields[6], fields[7]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintUtils.pause();
    }

    /**
     * Prints out a formatted table of all staff records with a specific gender
     * in the STAFF_TXT file. The gender is specified by the user input.
     * 
     * The method reads the file line by line, splits each line into fields,
     * calculates the age of each staff member, and prints out the fields in a
     * formatted table. If the file is empty, the method prints a "No results
     * found" message.
     * 
     * The method also prints a header with the column names, and a footer with
     * a horizontal line.
     * 
     * Finally, the method calls {@link PrintUtils#pause()} to pause the console
     * output.
     */
    public void filterByGender(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          Filter Staff by Gender        ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("1. Male");
        System.out.println("2. Female");
        System.out.println("3. Other");
        System.out.println("══════════════════════════════════════════");
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine().trim();
        String gender = "";
        switch (choice) {
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
                System.out.println("Invalid choice. Please try again.");
                return;
        }

        try {
            List<String> filteredStaff = Files.readAllLines(Paths.get(STAFF_TXT));
            List<String> filtered = new ArrayList<>();
            for (String line : filteredStaff) {
                String[] fields = line.split("\\|");
                if (fields[3].equals(gender)) {
                    filtered.add(line);
                }
            }

            if (filtered.isEmpty()) {
                System.out.println("\nNo results found.");
            } else {
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("║             Filtered Staff             ║");
                System.out.println("╚════════════════════════════════════════╝");
                System.out.printf("%-5s %-10s %-20s %-20s %-10s %-8s %-18s %-30s %-15s\n",
                        "No.", "User ID", "First Name", "Last Name", "Gender", "Age", "Contact Number", "Email Address",
                        "Role");
                System.out.println("══════════════════════════════════════════");

                for (int i = 0; i < filtered.size(); i++) {
                    String[] fields = filtered.get(i).split("\\|");
                    int age = calculateAge(LocalDate.parse(fields[4], DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    System.out.printf("%-5d %-10s %-20s %-20s %-10s %-8d %-18s %-30s %-15s\n",
                            i + 1, fields[0], fields[1], fields[2], fields[3], age, fields[5], fields[6], fields[7]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintUtils.pause();
    }

    /**
     * Prints out a formatted table of all staff records in the STAFF_TXT file
     * with a specific age. The age is specified by the user input.
     * 
     * The method reads the file line by line, splits each line into fields,
     * calculates the age of each staff member, and checks if the age matches
     * the user-specified age. If a match is found, the staff record is added
     * to a filtered list. If the file is empty, the method prints a "No results
     * found" message.
     * 
     * The method also prints a header with the column names, and a footer with
     * a horizontal line.
     * 
     * Finally, the method calls {@link PrintUtils#pause()} to pause the console
     * output.
     */
    public void filterByAge(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           Filter Staff by Age          ║");
        System.out.println("╚════════════════════════════════════════╝");

        System.out.print("Enter age: ");
        int age;
        while (true) {
            try {
                age = Integer.parseInt(scanner.nextLine().trim());
                if (age < 0) {
                    System.out.println("Age cannot be negative. Please enter a valid age: ");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid age: ");
            }
        }

        try {
            List<String> filteredStaff = Files.readAllLines(Paths.get(STAFF_TXT));
            List<String> filtered = new ArrayList<>();
            for (String line : filteredStaff) {
                String[] fields = line.split("\\|");
                if (calculateAge(LocalDate.parse(fields[4], DateTimeFormatter.ofPattern("dd-MM-yyyy"))) == age) {
                    filtered.add(line);
                }
            }

            if (filtered.isEmpty()) {
                System.out.println("\nNo results found.");
            } else {
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("║             Filtered Staff             ║");
                System.out.println("╚════════════════════════════════════════╝");
                System.out.printf("%-5s %-10s %-20s %-20s %-10s %-8s %-18s %-30s %-15s\n",
                        "No.", "User ID", "First Name", "Last Name", "Gender", "Age", "Contact Number", "Email Address",
                        "Role");
                System.out.println("══════════════════════════════════════════");
                for (int i = 0; i < filtered.size(); i++) {
                    String[] fields = filtered.get(i).split("\\|");
                    System.out.printf("%-5d %-10s %-20s %-20s %-10s %-8d %-18s %-30s %-15s\n",
                            i + 1, fields[0], fields[1], fields[2], fields[3], age, fields[5], fields[6], fields[7]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintUtils.pause();
    }

    /**
     * Calculates the age of a person given their birth date.
     *
     * @param birthDate the birth date of the person
     * @return the age of the person
     */
    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Allows the user to update an existing staff record.
     * 
     * The user is prompted to enter the index of the staff record to update,
     * and then each field of the record is updated one by one. The user can
     * choose to keep the current value of a field by leaving it blank.
     * 
     * The user is also prompted to update the password of the staff member.
     * If the user chooses to update the password, the password is updated
     * using the {@link #updateStaffPasswordFlow(String, Scanner)} method.
     * 
     * After all the fields have been updated, the updated record is written
     * back to the staff file.
     * 
     * @param scanner the Scanner object to read user input from
     */
    public void updateStaff(Scanner scanner) { // Pass the scanner as a parameter
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              Update Staff              ║");
        System.out.println("╚════════════════════════════════════════╝");

        try {
            List<String> staff = Files.readAllLines(Paths.get(STAFF_TXT));
            while (true) {
                System.out.print("Enter the index of the staff to update (or 0 to exit): ");
                String input = scanner.nextLine();
                if (input.trim().equals("0")) {
                    break;
                }

                int index;
                try {
                    index = Integer.parseInt(input.trim()) - 1; // Convert to zero-based index
                } catch (NumberFormatException e) {
                    System.out.println("Invalid index entered. Please enter a valid number.");
                    continue;
                }

                if (index < 0 || index >= staff.size()) {
                    System.out.println("Invalid index. Please enter a valid number between 1 and "
                            + staff.size() + ".");
                    continue;
                }

                String[] originalFields = staff.get(index).split("\\|");
                String staffId = originalFields[0];
                String firstName = originalFields[1], lastName = originalFields[2], gender = originalFields[3],
                        contactNumber = originalFields[5], emailAddress = originalFields[6], role = originalFields[7],
                        dateOfBirth = originalFields[4];
                boolean anyFieldUpdated = false;

                // Prompt for new values if provided
                while (true) {
                    System.out.print("Enter your new First Name (leave blank to keep current value): ");
                    String input2 = scanner.nextLine();
                    if (input2 == null || input2.trim().isEmpty()) {
                        break;
                    }

                    if (!input2.trim().matches("[a-zA-Z]{1,15}")) {
                        System.out.println(
                                "First name must be between 1 and 15 alphabetic characters. Please try again.");
                    } else {
                        firstName = input2.trim();
                        anyFieldUpdated = true;
                        break;
                    }
                }

                while (true) {
                    System.out.print("Enter your new Last Name (leave blank to keep current value): ");
                    String input2 = scanner.nextLine();
                    if (input2 == null || input2.trim().isEmpty()) {
                        break;
                    }

                    if (!input2.trim().matches("[a-zA-Z]{1,15}")) {
                        System.out
                                .println("Last name must be between 1 and 15 alphabetic characters. Please try again.");
                    } else {
                        lastName = input2.trim();
                        anyFieldUpdated = true;
                        break;
                    }
                }

                while (true) {
                    System.out.println("Choose your new Gender (leave blank to keep current value):");
                    System.out.println("1. Male");
                    System.out.println("2. Female");
                    System.out.println("3. Other");
                    System.out.print("Enter your choice (1-3): ");
                    String choiceInput = scanner.nextLine().trim();
                    if (choiceInput == null || choiceInput.isEmpty()) {
                        break;
                    }

                    try {
                        int choice = Integer.parseInt(choiceInput);
                        switch (choice) {
                            case 1:
                                gender = "Male";
                                break;
                            case 2:
                                gender = "Female";
                                break;
                            case 3:
                                gender = "Other";
                                break;
                            default:
                                System.out.println("Invalid choice. Please enter a valid choice.");
                                continue;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        continue;
                    }
                    anyFieldUpdated = true;
                    break;
                }

                while (true) {
                    System.out.print("Enter your new Contact Number (leave blank to keep current value): ");
                    String input2 = scanner.nextLine();
                    if (input2 == null || input2.trim().isEmpty()) {
                        break;
                    }

                    if (!isValidPhoneNumber(input2.trim())) {
                        System.out.println("Invalid phone number. Please enter a valid phone number.");
                    } else {
                        contactNumber = input2.trim();
                        anyFieldUpdated = true;
                        break;
                    }
                }

                while (true) {
                    System.out.print("Enter your new Email Address (leave blank to keep current value): ");
                    String input2 = scanner.nextLine();
                    if (input2 == null || input2.trim().isEmpty()) {
                        break;
                    }

                    if (!isValidEmail(input2.trim())) {
                        System.out.println("Invalid email address. Please enter a valid email address.");
                    } else {
                        emailAddress = input2.trim();
                        anyFieldUpdated = true;
                        break;
                    }
                }

                while (true) {
                    System.out.print("Enter your new date of birth (dd-MM-yyyy) (leave blank to keep current value): ");
                    String input2 = scanner.nextLine();
                    if (input2 == null || input2.trim().isEmpty()) {
                        break;
                    }

                    if (!isValidDate(input2)) {
                        System.out.println("Invalid date of birth. Please enter a valid date of birth.");
                    } else {
                        dateOfBirth = input2;
                        anyFieldUpdated = true;
                        break;
                    }
                }

                while (true) {
                    System.out.println("Choose your new Role (leave blank to keep current value):");
                    System.out.println("1. Doctor");
                    System.out.println("2. Pharmacist");
                    // System.out.println("3. Administrator");
                    System.out.print("Enter your choice (1-2): ");
                    String choiceInput = scanner.nextLine().trim();
                    if (choiceInput == null || choiceInput.isEmpty()) {
                        break;
                    }

                    try {
                        int choice = Integer.parseInt(choiceInput);
                        switch (choice) {
                            case 1:
                                role = "Doctor";
                                break;
                            case 2:
                                role = "Pharmacist";
                                break;
                            // case 3:
                            // role = "Administrator";
                            // break;
                            default:
                                System.out.println("Invalid choice. Please enter a valid choice.");
                                continue;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        continue;
                    }
                    anyFieldUpdated = true;
                    break;
                }

                while (true) {
                    System.out.print("Do you want to update your password? (yes/no): ");
                    String input2 = scanner.nextLine().toLowerCase().trim();
                    if (input2.equals("yes")) {
                        updateStaffPasswordFlow((staffId), scanner);
                        break;
                    } else if (input2.equals("no")) {
                        break;
                    } else {
                        System.out.println("Invalid input. Please enter yes or no.");
                    }
                }

                if (!anyFieldUpdated) {
                    System.out.println("No fields were updated for staff ID: " + staffId);
                    continue;
                }

                boolean updated = false;
                List<String> updatedStaff = new ArrayList<>();
                for (int i = 0; i < staff.size(); i++) {
                    String[] updatedFields = staff.get(i).split("\\|");
                    if (i == index) {
                        if (!firstName.isEmpty()) {
                            updatedFields[1] = firstName;
                        }
                        if (!lastName.isEmpty()) {
                            updatedFields[2] = lastName;
                        }
                        if (!gender.isEmpty()) {
                            updatedFields[3] = gender;
                        }
                        if (!dateOfBirth.isEmpty()) {
                            updatedFields[4] = dateOfBirth;
                        }
                        if (!contactNumber.isEmpty()) {
                            updatedFields[5] = contactNumber;
                        }
                        if (!emailAddress.isEmpty()) {
                            updatedFields[6] = emailAddress;
                        }
                        if (!role.isEmpty()) {
                            updatedFields[7] = role;
                        }
                        updatedStaff.add(String.join("|", updatedFields));
                        updated = true;
                    } else {
                        updatedStaff.add(staff.get(i));
                    }
                }

                if (updated) {
                    Files.write(Paths.get(STAFF_TXT), updatedStaff);
                    System.out.println("Update successful for staff ID: " + staffId);
                } else {
                    System.out.println("Update failed for staff ID: " + staffId);
                }
                break;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the staff information.");
            System.out.println("Please make sure the staff ID is valid.");
            e.printStackTrace();
        }
    }

    /**
     * Updates the password for a given staff ID with up to 3 attempts.
     * This method prompts the user to enter their current password with up to 3
     * attempts. If the current password is correct, it then prompts the user to
     * enter a new password with up to 3 attempts. The new password is required to
     * have a minimum of 8 characters, at least 1 digit, and at least 1 special
     * character. If the user fails to enter a valid new password after 3
     * attempts, the method will print a failure message. If the user fails to
     * enter the correct current password after 3 attempts, the method will also
     * print a failure message.
     *
     * @param staffId the staff ID whose password is to be updated
     * @param scanner the Scanner object used for reading user input
     */
    private void updateStaffPasswordFlow(String staffId, Scanner scanner) {
        int currentPasswordAttempts = 0;
        boolean currentPasswordCorrect = false;

        // First, verify current password with up to 3 attempts
        while (currentPasswordAttempts < 3 && !currentPasswordCorrect) {
            System.out.print("Enter current password: ");
            String currentPassword = scanner.nextLine().trim();

            if (authenticate(staffId, hashPassword(currentPassword))) {
                currentPasswordCorrect = true;
            } else {
                currentPasswordAttempts++;
                System.out.println(
                        "Incorrect current password. " + (3 - currentPasswordAttempts) + " attempt(s) remaining.");
            }
        }

        // If the current password is correct, proceed to update the password with a new
        // one
        if (currentPasswordCorrect) {
            int newPasswordAttempts = 0;
            boolean passwordUpdated = false;

            while (newPasswordAttempts < 3 && !passwordUpdated) {
                System.out.print("Enter new password (min 8 chars, 1 digit, 1 special char): ");
                String newPassword = scanner.nextLine().trim();

                if (isValidPassword(newPassword)) {
                    passwordUpdated = updateStaffPassword(staffId, newPassword);
                    System.out.println(passwordUpdated ? "Password updated successfully." : "Password update failed.");
                    PrintUtils.pause();
                } else {
                    newPasswordAttempts++;
                    System.out.println(
                            "Invalid password format. " + (3 - newPasswordAttempts) + " attempt(s) remaining.");
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

    /**
     * Updates the password for a given staff ID in the account file.
     * 
     * @param staffId     the unique identifier of the staff whose password is to be
     *                    updated
     * @param newPassword the new password to be set for the staff
     * @return true if the password update is successful, false otherwise
     */
    public boolean updateStaffPassword(String staffId, String newPassword) {
        try {
            return updateAccountFile(staffId, hashPassword(newPassword), ACCOUNT_TXT, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a staff member from the system based on user input.
     *
     * This method prompts the user to enter the index of the staff member
     * to be removed from the list. The staff record is removed from both
     * the staff list and the account file. If the user input is invalid
     * or the index is out of range, an appropriate error message is displayed.
     * The user can exit the removal process by entering 0.
     *
     * @param scanner the Scanner object used for reading user input
     */
    public void removeStaff(Scanner scanner) {
        try {
            List<String> staff = Files.readAllLines(Paths.get(STAFF_TXT));
            while (true) {
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("║              Remove Staff              ║");
                System.out.println("╚════════════════════════════════════════╝");

                System.out.print("Enter index of staff to remove (or 0 to exit): ");
                String input = scanner.nextLine().trim();
                if (input.equals("0")) {
                    break;
                }

                int index;
                try {
                    index = Integer.parseInt(input) - 1; // Convert to zero-based index
                } catch (NumberFormatException e) {
                    System.out.println("Invalid index entered. Please enter a valid number.");
                    continue;
                }

                if (index < 0 || index >= staff.size()) {
                    System.out.println("Invalid index. Please enter a valid number between 1 and "
                            + staff.size() + ".");
                    continue;
                }

                String[] fields = staff.get(index).split("\\|");
                String userId = fields[0];
                String firstName = fields[1];
                String lastName = fields[2];

                staff.remove(index);
                Files.write(Paths.get(STAFF_TXT), staff);

                List<String> accounts = Files.readAllLines(Paths.get(ACCOUNT_TXT));
                List<String> updatedAccounts = new ArrayList<>();
                for (String account : accounts) {
                    String[] fieldsInAccount = account.split("\\|");
                    if (!fieldsInAccount[0].equals(userId)) {
                        updatedAccounts.add(account);
                    }
                }
                Files.write(Paths.get(ACCOUNT_TXT), updatedAccounts);

                System.out
                        .println("User removed successfully: " + userId + " (" + firstName + " " + lastName + ")");

            }
        } catch (IOException e) {
            System.out.println("An error occurred while removing the staff information.");
            System.out.println("Please make sure the index is valid.");
            e.printStackTrace();
        }
    }

    /**
     * Checks if the contact number is valid.
     * 
     * A contact number is valid if it is a string of digits with a length between 8
     * and 15 inclusive.
     * 
     * @param contactNumber the contact number to check
     * @return true if the contact number is valid, false otherwise
     */
    private boolean isValidContactNumber(String contactNumber) {
        return contactNumber.length() >= 8
                && contactNumber.length() <= 15
                && contactNumber.matches("[0-9]+"); // Ensures all characters are digits
    }

    /**
     * Checks if the given email address is valid.
     * 
     * A valid email address must contain both the '@' and '.' characters.
     * 
     * @param email the email address to check
     * @return true if the email address is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    /**
     * Checks if the given password is valid.
     * 
     * A valid password must contain at least 8 characters, including at least
     * one digit and one special character.
     * 
     * @param password the password to check
     * @return true if the password is valid, false otherwise
     */
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[!@#$%^&*()_+=-]).{8,}$";
        return password.matches(passwordPattern);
    }

    /**
     * Checks if the given phone number is valid.
     * 
     * A valid phone number must be exactly 8 digits long and consist of only
     * numbers.
     * 
     * @param phoneNumber the phone number to check
     * @return true if the phone number is valid, false otherwise
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        String pattern = "^[0-9]{8}$";
        return phoneNumber.matches(pattern);
    }

    /**
     * Checks if the given date string is valid and follows the format "dd-MM-yyyy".
     * 
     * This method attempts to parse the input date string using the specified date
     * format. If the parsing is successful, the date is considered valid. If a
     * DateTimeParseException occurs, the date is considered invalid.
     * 
     * @param date the date string to check
     * @return true if the date is valid, false otherwise
     */
    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Checks if the given staff ID exists in the staff file.
     * 
     * This method reads the staff file line by line, splitting each line by the
     * "|" character. It then checks if the first field (i.e. the staff ID) of any
     * line matches the given staff ID. If a match is found, the method returns
     * true. Otherwise, it returns false. If an I/O exception occurs, the method
     * prints the stack trace and returns false.
     * 
     * @param staffId the staff ID to check
     * @return true if the staff ID is valid, false otherwise
     */
    private boolean isValidStaffId(String staffId) {
        try {
            List<String> staff = Files.readAllLines(Paths.get(STAFF_TXT));
            for (String line : staff) {
                String[] fields = line.split("\\|");
                if (fields[0].equals(staffId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
