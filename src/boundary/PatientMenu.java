package boundary;

import controller.AccountController;
import controller.AppointmentController;
import controller.AppointmentOutcomeController;
import controller.FeedbackController;
import controller.MedicalRecordsController;
import entity.Patient;
import interfaces.MenuInterface;
import java.util.Scanner;

public class PatientMenu implements MenuInterface {

    private final Patient patient;

    // Constructor initializes patient instance
    public PatientMenu(Patient patient) {
        this.patient = patient;
    }

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
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║               Patient Menu             ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. View Personal Information");
            System.out.println("2. View Medical Record");
            System.out.println("3. View Past Appointment Outcome Records");
            System.out.println("4. View Scheduled Appointment");
            System.out.println("5. Schedule an Appointment");
            System.out.println("6. Reschedule an Appointment");
            System.out.println("7. Cancel an Appointment/Request");
            System.out.println("8. Rate a Doctor");
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
