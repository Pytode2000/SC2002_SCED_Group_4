package boundary;

import entity.Pharmacist;
import interfaces.MenuInterface;
import java.util.Scanner;

public class PharmacistMenu implements MenuInterface {

    private final Pharmacist pharmacist;

    // Import controller thats patient need e.g., appointment etc.
    // All the functions should be in controller. this menu class just uses it.
    public PharmacistMenu(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
    }

    @Override
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Pharmacist Menu ---");
            System.out.println("1. View Appointment Outcome Record"); // AppointmentOutcomeController
            System.out.println("2. Update Prescription Status"); // AppointmentOutcomeController
            System.out.println("3. View Medication Inventory"); // InventoryController
            System.out.println("4. Submit Replenishment Request"); // InventoryController
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // View Appointment Outcome Record();
                    break;
                case "2":
                    // Update Prescription Status;
                    break;
                case "3":
                    // View Medication Inventory;
                    break;
                case "4":
                    // Submit Replenishment Request();
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
