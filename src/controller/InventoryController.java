package controller;

import entity.Medicine;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InventoryController {
    private List<Medicine> medicines;

    public InventoryController() {
        this.medicines = new ArrayList<>();
    }

    // Add a medicine to the inventory
    public void addMedicine(Medicine medicine) {
        System.out.println("\n--- Add New Item ---");
        System.out.println("------------------------");
        medicines.add(medicine);
        System.out.println("Item added successfully: " + medicine.getName());
        System.out.println("------------------------");

        //back to main menu
        promptReturnToMenu();
    }

    // Find a specific item by its ID without UI elements (updated to use String itemId)
    public Medicine findItemById(String itemId) {
        for (Medicine medicine : medicines) {
            if (medicine.getItemId().equals(itemId)) { // Compare using equals for Strings
                return medicine;
            }
        }
        return null; // Item not found
    }

    // Check which medicines have low stock
    public List<Medicine> checkLowStock() {
        System.out.println("\n--- Check Low Stock Items ---");
        System.out.println("---------------------------------");
        List<Medicine> lowStockMedicines = new ArrayList<>();
        for (Medicine medicine : medicines) {
            if (medicine.isLowStockLevelAlert()) {
                lowStockMedicines.add(medicine);
                System.out.println("Low Stock: " + medicine.getName() + " | Quantity: " + medicine.getStockLevel());
            }
        }
        if (lowStockMedicines.isEmpty()) {
            System.out.println("No items with low stock.");
        }
        System.out.println("---------------------------------");

        //back to main menu
        promptReturnToMenu();
        return lowStockMedicines;
    }

    // Replenishment request (updated to use String itemId)
    public void requestReplenishment() {
        System.out.println("\n--- Submit Replenishment Request ---");
        System.out.println("------------------------------------");
        Scanner scanner = new Scanner(System.in);

        // Prompt for Item ID
        System.out.print("Enter Item ID for replenishment request: ");
        String itemId = scanner.nextLine().trim();

        // Find the item by ID
        Medicine medicine = findItemById(itemId);
        if (medicine != null) {
            // Check if replenishment is necessary
            if (medicine.isLowStockLevelAlert() || medicine.getStockLevel() == 0) {
                // Prompt for replenishment amount
                System.out.print("Enter replenishment amount: ");
                int replenishmentAmount;
                try {
                    replenishmentAmount = Integer.parseInt(scanner.nextLine().trim());
                    if (replenishmentAmount <= 0) {
                        System.out.println("Invalid amount. Please enter a positive integer.");
                        return;
                    }
                    
                    // Set status to "Pending Replenishment Request"
                    medicine.setPendingReplenishmentRequest();
                    System.out.println("Replenishment request submitted for Item ID: " + itemId + " with amount: " + replenishmentAmount);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount format. Please enter a valid numeric value.");
                }
            } else {
                System.out.println("Replenishment not needed for Item ID: " + itemId + " as stock is sufficient.");
            }
        } else {
            System.out.println("Item ID: " + itemId + " not found in inventory.");
        }
        System.out.println("------------------------------------");

        //back to main menu
        promptReturnToMenu();
    }


    // Display  entire inventory
    public void displayInventory() {
        System.out.println("\n------- Inventory ---------");
        System.out.println("---------------------------");
        int index = 1;
        for (Medicine medicine : medicines) {
            System.out.printf("%d. Item ID: %s | Name: %s | Type: %s | Description: %s | Quantity: %d | Status: %s%n",
                    index++, medicine.getItemId(), medicine.getName(),
                    medicine.getItemType(), medicine.getDescription(),
                    medicine.getStockLevel(), medicine.getStatus());
        }
        if (medicines.isEmpty()) {
            System.out.println("No items in inventory.");
        }
        System.out.println("---------------------------");

        //o back to main menu
        promptReturnToMenu();
    }

    // return to main menu
    private void promptReturnToMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter 0 to return: ");
        while (!"0".equals(scanner.nextLine().trim())) {
            System.out.print("Invalid input. Please enter 0 to return: ");
        }
    }
}
