package boundary;

import controller.AccountController;
import controller.AppointmentController;
import controller.AppointmentOutcomeController;
import controller.BillController;
import controller.FeedbackController;
import controller.MedicalRecordsController;
import entity.Patient;
import interfaces.MenuInterface;
import java.util.Scanner;

/**
 * The PatientMenu class implements the MenuInterface and represents the menu
 * of options available to a patient within the hospital management system. 
 * It handles patient-specific actions such as viewing personal information, 
 * managing appointments, viewing medical records, and handling bills.
 */

public class PatientMenu implements MenuInterface {


    /** Patient object representing the currently logged-in patient. */
    private final Patient patient;

    /** ANSI color code for light yellow text in the console. */
    public static final String ANSI_TRUE_LIGHT_YELLOW = "\u001B[38;2;255;255;224m"; // Light Yellow (#FFFFE0)


     /**
     * Constructs a PatientMenu instance with the specified patient.
     * 
     * @param patient The Patient object representing the currently logged-in patient.
     */
    public PatientMenu(Patient patient) {
        this.patient = patient;
    }

     /**
     * Displays the menu of options available to the patient and handles their input.
     * This method allows the patient to perform various actions such as viewing
     * personal information, managing appointments, viewing medical records,
     * managing bills, and providing feedback for doctors.
     * <p>
     * It continuously prompts the user until the user chooses to log out.
     */
    @Override
    public void displayMenu() {
        AccountController accountController = new AccountController();
        AppointmentController appointmentController = new AppointmentController();
        MedicalRecordsController medicalRecordsController = new MedicalRecordsController();
        AppointmentOutcomeController appointmentOutcomeController = new AppointmentOutcomeController();
        FeedbackController feedbackController = new FeedbackController();

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println(ANSI_TRUE_LIGHT_YELLOW  + "\n╔════════════════════════════════════════╗");
            System.out.println("║               Patient Menu             ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. View Personal Information");
            System.out.println("2. View Medical Record");
            System.out.println("3. View Past Appointment Outcome Records");
            System.out.println("4. View Scheduled Appointment");
            System.out.println("5. Schedule an Appointment");
            System.out.println("6. Reschedule an Appointment");
            System.out.println("7. Cancel an Appointment/Request");
            System.out.println("8. Manage Bills");
            System.out.println("9. Rate a Doctor");
            System.out.println("0. Logout");
            System.out.println("══════════════════════════════════════════");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Update personal information
                    accountController.updatePersonalInformation(patient);
                    break;
                case "2":
                    // View medical records
                    medicalRecordsController.viewMedicalRecords(patient);
                    break;
                case "3":
                    // View past appointment outcome records
                    appointmentOutcomeController.displayAppointmentOutcomesByPatientId(patient.getUserId());
                    break;
                case "4":
                    // View scheduled appointments
                    appointmentController.displayAndSelectBookedAppointments(patient.getUserId());
                    break;
                case "5":
                    // Schedule a new appointment
                    appointmentController.scheduleAppointment(patient.getUserId());
                    break;
                case "6":
                    // Reschedule an existing appointment
                    appointmentController.requestRescheduleAppointment(patient.getUserId());
                    break;
                case "7":
                    // Cancel an appointment or request
                    appointmentController.deleteBookedAppointment(patient.getUserId());
                    break;
                case "8":
                    // Provide feedback on a doctor
                    BillController.viewAndPayBills(patient.getUserId());    
                    break;
                case "9":
                    // Provide feedback on a doctor
                    feedbackController.provideFeedback(patient.getUserId());
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
