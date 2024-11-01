package boundary;

import entity.Doctor;
import interfaces.MenuInterface;
import java.util.Scanner;

import controller.AppointmentController;
import controller.MedicalRecordsController;

public class DoctorMenu implements MenuInterface {

    private final Doctor doctor;
    private MedicalRecordsController medicalRecordsController;
    private AppointmentController appointmentController;

    // Import controller thats doctors need e.g., appointment, schedule etc.
    // All the functions should be in controller. this menu class just uses it.
    public DoctorMenu(Doctor doctor) {
        this.doctor = doctor;
        medicalRecordsController = new MedicalRecordsController();
        appointmentController = new AppointmentController();

    }

    @Override
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Doctor Menu ---");
            System.out.println("1. Patient Medical Records"); // MedicalRecordController
            System.out.println("2. View Personal Schedule"); // DoctorScheduleController
            System.out.println("3. Set Availability for Appointments"); // DoctorScheduleController (?)
            System.out.println("4. Accept or Decline Appointment Requests"); // AppointmentController
            System.out.println("5. View Upcoming Appointments"); // AppointmentController
            System.out.println("6. Record Appointment Outcome"); // AppointmentOutcomeController
            System.out.println("0. Logout");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Patient Medical Records();
                    medicalRecordsController.viewMedicalRecords(doctor);
                    break;
                case "2":
                    // View Personal Schedule();
                    appointmentController.viewPersonalSchedule(doctor.getUserId());
                    break;
                case "3":
                    // Set Availability for Appointments();
                    break;
                case "4":
                    // Accept or Decline Appointment Requests();
                    break;
                case "5":
                    // View Upcoming Appointments()
                    break;
                case "6":
                    // Record Appointment Outcome();
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
