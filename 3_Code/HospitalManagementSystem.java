
import java.util.Scanner;

public class HospitalManagementSystem {

    public static void main(String[] args) {
        AccountController accountController = new AccountController();
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to choose between login and registration
        System.out.println("Welcome to the Hospital Management System.");
        System.out.println("Please choose an option:");
        System.out.println("1. Login");
        System.out.println("2. Register");

        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (choice == 1) {
            // Login flow
            System.out.print("Enter User ID: ");
            String inputUserId = scanner.nextLine();
            System.out.print("Enter Password: ");
            String inputPassword = scanner.nextLine();

            // Attempt login
            Account loggedInAccount = accountController.login(inputUserId, inputPassword);

            if (loggedInAccount != null) {
                System.out.println("Login successful. Welcome, " + loggedInAccount.getUser().getFirstName());
                // Navigate menu based on user type
            } else {
                System.out.println("Login failed.");
            }

        } else if (choice == 2) {
            // Registration flow
            System.out.print("Enter a new User ID: ");
            String newUserId = scanner.nextLine();
            System.out.print("Enter First Name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter Last Name: ");
            String lastName = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            System.out.print("Enter Role (e.g., Doctor, Patient): ");
            String role = scanner.nextLine();

            // If Patient, ask for patient-specific details (like patientId)
            String patientId = null;
            if (role.equalsIgnoreCase("Patient")) {
                System.out.print("Enter Patient ID: ");
                patientId = scanner.nextLine();
            }

            // Handle registration
            if (accountController.register(newUserId, firstName, lastName, password, role, patientId)) {
                System.out.println("Registration successful. You can now log in.");
            } else {
                System.out.println("Registration failed. User ID might already exist.");
            }
        } else {
            System.out.println("Invalid choice. Exiting.");
        }

        scanner.close();
    }
}
