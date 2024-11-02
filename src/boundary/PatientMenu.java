package boundary;

import controller.AccountController;
import controller.AppointmentController;
import controller.AppointmentOutcomeController;
import controller.MedicalRecordsController;
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
        AppointmentController appointmentController = new AppointmentController();
        MedicalRecordsController medicalRecordsController = new MedicalRecordsController();
        AppointmentOutcomeController appointmentOutcomeController = new AppointmentOutcomeController();

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
                    medicalRecordsController.viewMedicalRecords(patient);
                    break;
                case "2":
                    // Update Personal Information(); // AccountController Update
                    accountController.updatePersonalInformation(this.patient);
                    break;
                case "3":
                    // Schedule an Appointment(); // AppointmentController Create
                    appointmentController.displayAndSelectAvailableAppointments(this.patient.getUserId());
                    break;
                case "4":
                    // Reschedule an Appointment(); // AppointmentController Update --> give up current, then choose another again.
                    appointmentController.requestRescheduleAppointment(this.patient.getUserId());
                    break;
                case "5":
                    // Cancel an Appointment(); // AppointmentController Update?
                    appointmentController.deleteBookedAppointment(this.patient.getUserId());
                    break;
                case "6":
                    // View Scheduled Appointment() // // AppointmentController Read
                    appointmentController.displayAndSelectBookedAppointments(this.patient.getUserId());
                    break;
                case "7":
                    // View Past Appointment Outcome Records(); // Read 
                    appointmentOutcomeController.displayAppointmentOutcomesByPatientId(this.patient.getUserId());

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
