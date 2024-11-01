package boundary;

import controller.AccountController;
import entity.Patient;
import interfaces.MenuInterface;
import java.util.Scanner;

public class PatientMenu implements MenuInterface {

    private final Patient patient;

    // Import controller thats patient need e.g., appointment etc.
    // All the functions should be in controller. this menu class just uses it.
    public PatientMenu(Patient patient) {
        this.patient = patient;
    }

    @Override
    public void displayMenu() {
        AccountController accountController = new AccountController();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Patient Menu ---");
            System.out.println("1. View Medical Record"); // MedicalRecordController
            System.out.println("2. Update Personal Information"); // AccountController
            System.out.println("3. Schedule an Appointment"); // AppointmentController
            System.out.println("4. Reschedule an Appointment"); // AppointmentController
            System.out.println("5. Cancel an Appointment"); // AppointmentController
            System.out.println("6. View Scheduled Appointment"); // AppointmentController
            System.out.println("7. View Past Appointment Outcome Records"); // AppointmentOutcomeController
            System.out.println("0. Logout");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // View Medical Record(); // MedicalRecordController Read
                    break;
                case "2":
                    // Update Personal Information(); // AccountController Update
                    accountController.updatePersonalInformation(this.patient);
                    break;
                case "3":
                    // Schedule an Appointment(); // AppointmentController Create
                    break;
                case "4":
                    // Reschedule an Appointment(); // AppointmentController Update
                    break;
                case "5":
                    // Cancel an Appointment(); // AppointmentController Delete/Update?
                    break;
                case "6":
                    // View Scheduled Appointment() // // AppointmentController Read
                    break;
                case "7":
                    // View Past Appointment Outcome Records(); // Read 

                    break;
                case "0":
                    System.out.println("Logging out...");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

}