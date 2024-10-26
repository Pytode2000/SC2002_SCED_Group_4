package boundary;

import entity.Doctor;
import interfaces.MenuInterface;
import java.util.Scanner;

public class DoctorMenu implements MenuInterface {

    private final Doctor doctor;

    // Import controller thats doctors need e.g., appointment, schedule etc.
    // All the functions should be in controller. this menu class just uses it.
    public DoctorMenu(Doctor doctor) {
        this.doctor = doctor;
    }

    @Override
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Doctor Menu ---");
            System.out.println("1. View Patient Medical Records");
            System.out.println("2. Update Patient Medical Records");
            System.out.println("3. View Personal Schedule");
            System.out.println("4. Set Availability for Appointments");
            System.out.println("5. Accept or Decline Appointment Requests");
            System.out.println("6. View Upcoming Appointments");
            System.out.println("7. Record Appointment Outcome");
            System.out.println("0. Logout");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // View Patient Medical Records();
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
