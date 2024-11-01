package boundary;

import entity.Doctor;
import interfaces.MenuInterface;
import java.util.Scanner;

import controller.MedicalRecordsController;

public class DoctorMenu implements MenuInterface {

    private final Doctor doctor;
    private MedicalRecordsController medicalRecordsController;

    // Import controller thats doctors need e.g., appointment, schedule etc.
    // All the functions should be in controller. this menu class just uses it.
    public DoctorMenu(Doctor doctor) {
        this.doctor = doctor;
        medicalRecordsController = new MedicalRecordsController();
    }

    @Override
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Doctor Menu ---");
            System.out.println("1. View Patient Medical Records"); // MedicalRecordController
            System.out.println("2. Update Patient Medical Records"); // MedicalRecordController
            System.out.println("3. View Personal Schedule"); // DoctorScheduleController
            System.out.println("4. Set Availability for Appointments"); // DoctorScheduleController (?)
            System.out.println("5. Accept or Decline Appointment Requests"); // AppointmentController
            System.out.println("6. View Upcoming Appointments"); // AppointmentController
            System.out.println("7. Record Appointment Outcome"); // AppointmentOutcomeController
            System.out.println("0. Logout");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // View Patient Medical Records();
                    medicalRecordsController.displayMedicalRecordsMenu();
                    break;
                case "2":
                    // Update Patient Medical Records();
                    break;
                case "3":
                    // View Personal Schedule();
                    break;
                case "4":
                    // Set Availability for Appointments();
                    break;
                case "5":
                    // Accept or Decline Appointment Requests();
                    break;
                case "6":
                    // View Upcoming Appointments()
                    break;
                case "7":
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
