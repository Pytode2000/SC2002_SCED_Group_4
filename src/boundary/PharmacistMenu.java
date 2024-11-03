package boundary;

import entity.Pharmacist;
import controller.InventoryController;
import controller.AppointmentOutcomeController;
import entity.Medicine;
import interfaces.MenuInterface;
import java.util.Scanner;

public class PharmacistMenu implements MenuInterface {

    private final Pharmacist pharmacist;
    //testing
    AppointmentOutcomeController appointmentOutcomeController = new AppointmentOutcomeController();


    // Import controller thats patient need e.g., appointment etc.
    // All the functions should be in controller. this menu class just uses it.
    public PharmacistMenu(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
    }

    @Override
    public void displayMenu() {
        InventoryController inventoryController = new InventoryController();
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
                    viewAptOC();
                    break;
                case "2":
                    // Update Prescription Status;

                    break;
                case "3":
                    // View Medication Inventory;
                    inventoryController.displayInventory();
                    break;
                case "4":
                    // Submit Replenishment Request();
                    inventoryController.requestReplenishment();
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


        public void viewAptOC() {
        System.out.println("\n--- Appoinment Outcomes ---");
        System.out.println("------------------------------------");
        Scanner scanner = new Scanner(System.in);

        // Prompt for Patient ID
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine().trim();

        // Find the patient by ID
        appointmentOutcomeController.displayAppointmentOutcomesByPatientId(patientId);

        System.out.println("------------------------------------");

        // back to main menu
        //promptReturnToMenu();
    }


}
