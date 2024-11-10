package boundary;

import controller.AppointmentController;
import controller.AppointmentOutcomeController;
import controller.FeedbackController;
import controller.MedicalRecordsController;
import entity.Doctor;
import interfaces.MenuInterface;
import java.util.Scanner;

public class DoctorMenu implements MenuInterface {

    private final Doctor doctor;
    private final MedicalRecordsController medicalRecordsController;
    private final AppointmentController appointmentController;
    private final AppointmentOutcomeController appointmentOutcomeController;
    private final FeedbackController feedbackController;

    // Initialize DoctorMenu with necessary controllers for doctor actions
    public DoctorMenu(Doctor doctor) {
        this.doctor = doctor;
        this.medicalRecordsController = new MedicalRecordsController();
        this.appointmentController = new AppointmentController();
        this.appointmentOutcomeController = new AppointmentOutcomeController();
        this.feedbackController = new FeedbackController();
    }

    @Override
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║               Doctor Menu              ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. Patient Medical Records");
            System.out.println("2. View Personal Schedule");
            System.out.println("3. Set Availability for Appointments");
            System.out.println("4. Accept or Decline Appointment Requests");
            System.out.println("5. View Upcoming Appointments");
            System.out.println("6. Record Appointment Outcome");
            System.out.println("7. View Feedbacks");
            System.out.println("0. Logout");
            System.out.println("══════════════════════════════════════════");


            System.out.print("Enter your choice: ");
            
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // View patient medical records
                    medicalRecordsController.viewMedicalRecords(doctor);
                    break;
                case "2":
                    // View personal schedule
                    appointmentController.viewPersonalSchedule(doctor.getUserId());
                    break;
                case "3":
                    // Set availability for appointments
                    appointmentController.setAvailability(doctor.getUserId());
                    break;
                case "4":
                    // Accept or decline appointment requests
                    appointmentController.viewAppointmentRequest(doctor.getUserId());
                    break;
                case "5":
                    // View upcoming appointments
                    appointmentController.viewUpcomingAppointments(doctor.getUserId());
                    break;
                case "6":
                    // Record outcome of an appointment
                    appointmentOutcomeController.viewDoctorMenu(doctor.getUserId());
                    break;
                case "7":
                    // View doctor feedback
                    feedbackController.viewDoctorRatings(doctor.getUserId());
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
