package controller;

import entity.Medicine;
import utility.FileUtils;
import utility.PrintUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InventoryController {
    private List<Medicine> medicines;
    private static final String MEDICINE_FILE = "data/medicine.txt";
    private static final String MEDICINE_REPLENISHMENT_REQUESTS = "data/medicineReplenishmentRequests.txt";

    public InventoryController() {
        this.medicines = new ArrayList<>();
        loadMedicinesFromFile();
    }

    // Load medicines from medicine.txt
    private void loadMedicinesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                Medicine medicine = new Medicine(data[0], data[1], data[2],
                        Integer.parseInt(data[3]), Integer.parseInt(data[4]), data[5]);
                medicines.add(medicine);
            }
        } catch (IOException e) {
            System.out.println("Error loading medicines: " + e.getMessage());
        }
    }

    // Save medicines to medicine.txt
    private void saveMedicinesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEDICINE_FILE))) {
            for (Medicine medicine : medicines) {
                writer.write(String.join("|",
                        medicine.getMedicineId(), medicine.getName(), medicine.getDescription(),
                        String.valueOf(medicine.getStockLevel()), String.valueOf(medicine.getLowStockLevel()),
                        medicine.getMedicineType()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving medicines: " + e.getMessage());
        }
    }

    // Generate the next medicine ID by reading the last ID from the file and
    // incrementing it
    private String generateMedicineId() {
        String lastId = "MD00000"; // Default ID if no entries are found

        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lastId = line.split("\\|")[0];
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine file: " + e.getMessage());
        }

        int lastNum = Integer.parseInt(lastId.substring(2));
        return String.format("MD%05d", lastNum + 1);
    }

    // Display entire inventory
    public void displayInventory() {
        System.out.println("\n------- Inventory ---------");
        System.out.println(String.format("%-10s %-20s %-15s %-30s %-12s %-18s %-20s",
                "Medicine ID", "Name", "Type", "Description", "Quantity", "Low Stock Level", "Status"));
        System.out.println(
                "------------------------------------------------------------------------------------------------------------------------------");

        for (Medicine medicine : medicines) {
            System.out.printf("%-10s %-20s %-15s %-30s %-12d %-18d %-20s%n",
                    medicine.getMedicineId(), medicine.getName(),
                    medicine.getMedicineType(), medicine.getDescription(),
                    medicine.getStockLevel(), medicine.getLowStockLevel(), medicine.getStatus());
        }

        if (medicines.isEmpty()) {
            System.out.println("No medicines in inventory.");
        }

        PrintUtils.pause();
    }

    // Method to add a new medicine
    public void addMedicine(Scanner scanner) {
        try {
            String medicineId = generateMedicineId();

            System.out.print("Enter Medicine Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Medicine Description: ");
            String description = scanner.nextLine().trim();

            System.out.print("Enter Stock Level: ");
            int stockLevel = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Low Stock Level: ");
            int lowStockLevel = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Medicine Type: ");
            String medicineType = scanner.nextLine().trim();

            if (name.isEmpty() || description.isEmpty() || medicineType.isEmpty()) {
                System.out.println("Input fields cannot be empty. Medicine not added.");
                return;
            }

            // Create and add new Medicine object
            Medicine newMedicine = new Medicine(medicineId, name, description, stockLevel, lowStockLevel, medicineType);
            if (medicines == null) {
                medicines = new ArrayList<>();
            }
            medicines.add(newMedicine);

            // Save the updated list to the file
            saveMedicinesToFile();

            System.out.println("Medicine added successfully with ID: " + medicineId);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter valid integers for stock levels.");
        } catch (Exception e) {
            System.out.println("An error occurred while adding the medicine: " + e.getMessage());
        }
    }

    // Update an existing medicine
    public void updateMedicine(Scanner scanner) {
        System.out.println("\n--- Update Medicine ---");
        System.out.println("------------------------");
        System.out.print("Enter the ID of the medicine to update: ");
        String medicineId = scanner.nextLine().trim();

        Medicine medicine = findMedicineById(medicineId); // Find the medicine by ID
        if (medicine == null) {
            System.out.println("Medicine ID does not exist.");
            return;
        }

        System.out.print("Enter the new name (leave blank to keep current value): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            medicine.setName(name);
        }

        System.out.print("Enter the new description (leave blank to keep current value): ");
        String description = scanner.nextLine().trim();
        if (!description.isEmpty()) {
            medicine.setDescription(description);
        }

        System.out.print("Enter the new stock level (leave blank to keep current value): ");
        String stockLevelInput = scanner.nextLine().trim();
        if (!stockLevelInput.isEmpty()) {
            try {
                int stockLevel = Integer.parseInt(stockLevelInput);
                medicine.setStockLevel(stockLevel);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter valid integers for stock levels.");
            }
        }

        System.out.print("Enter the new low stock level (leave blank to keep current value): ");
        String lowStockLevelInput = scanner.nextLine().trim();
        if (!lowStockLevelInput.isEmpty()) {
            try {
                int lowStockLevel = Integer.parseInt(lowStockLevelInput);
                medicine.setLowStockLevel(lowStockLevel);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter valid integers for stock levels.");
            }
        }

        System.out.print("Enter the new medicine type (leave blank to keep current value): ");
        String medicineType = scanner.nextLine().trim();
        if (!medicineType.isEmpty()) {
            medicine.setMedicineType(medicineType);
        }

        // Save the updated list to the file
        saveMedicinesToFile();

        System.out.println("Medicine updated successfully.");
    }

    // Decrement stock level by 1 depending on medication prescribed in
    // AppointmentOutcome. [NOT COMPLETED YET]
    public void decrementStockLevel(String medicineId) {
        Medicine medicine = findMedicineById(medicineId); // Find the medicine by ID

        if (medicine != null) {
            if (medicine.getStockLevel() > 0) {
                medicine.setStockLevel(medicine.getStockLevel() - 1); // Decrement stock level by 1
                saveMedicinesToFile(); // Save updated inventory to file
                System.out.println("Stock level decremented by 1 for medicine ID: " + medicineId);
            } else {
                System.out.println("Stock level is already at 0. Cannot decrement further.");
            }
        } else {
            System.out.println("Medicine not found.");
        }
    }

    // Remove a specific medicine by its ID
    public void removeMedicine(Scanner scanner) {
        System.out.println("\n--- Remove Medicine ---");
        System.out.println("------------------------");
        System.out.print("Enter the ID of the medicine to remove: ");
        String medicineId = scanner.nextLine().trim();

        // Check if the medicine ID exists
        Medicine medicine = findMedicineById(medicineId);
        if (medicine == null) {
            System.out.println("Medicine ID does not exist or is invalid.");
            return;
        }

        try {
            medicines.remove(medicine);

            // Save the updated list to the file
            saveMedicinesToFile();

            System.out.println("Medicine removed successfully.");
        } catch (Exception e) {
            System.out.println("An error occurred while removing the medicine: " + e.getMessage());
        }
    }

    // Approve replenishment requests
    public void approveReplenishmentRequests() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Approve Replenishment Requests ---");

        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_REPLENISHMENT_REQUESTS))) {
            String line;
            List<Medicine> lowStockMedicines = new ArrayList<>();
            System.out.println(
                    String.format("%-15s %-30s %-10s %-18s %-30s",
                            "Medicine ID", "Name", "Stock", "Low Stock Level", "Replenishment Requested Amount"));
            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------");

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                String medicineId = data[0];
                int replenishmentAmount = Integer.parseInt(data[1]);

                Medicine medicine = findMedicineById(medicineId);
                if (medicine != null && medicine.isLowStockLevelAlert()) {
                    lowStockMedicines.add(medicine);
                    System.out.println(
                            String.format("%-15s %-30s %-10d %-18d %-30d", medicine.getMedicineId(),
                                    medicine.getName(), medicine.getStockLevel(),
                                    medicine.getLowStockLevel(), replenishmentAmount));
                }
            }

            if (lowStockMedicines.isEmpty()) {
                System.out.println("No medicines require replenishment approval.");
                return;
            }

            // Ask the user for the medicine ID to replenish
            System.out.print("Enter the Medicine ID to approve replenishment (leave blank to cancel): ");
            String medicineId = scanner.nextLine().trim();

            if (medicineId.isEmpty()) {
                System.out.println("Replenishment request cancelled.");
                return;
            }

            Medicine medicine = lowStockMedicines.stream()
                    .filter(m -> m.getMedicineId().equals(medicineId))
                    .findFirst()
                    .orElse(null);

            if (medicine == null) {
                System.out.println("Invalid Medicine ID or no replenishment request found for this ID.");
                return;
            }

            int replenishmentAmount = 0;
            // Read the replenishment amount from the request file
            try (BufferedReader reader2 = new BufferedReader(new FileReader(MEDICINE_REPLENISHMENT_REQUESTS))) {
                String line2;
                List<String> lines = new ArrayList<>();
                while ((line2 = reader2.readLine()) != null) {
                    String[] data2 = line2.split("\\|");
                    if (data2[0].equals(medicineId)) {
                        replenishmentAmount = Integer.parseInt(data2[1]);
                    } else {
                        lines.add(line2);
                    }
                }

                if (replenishmentAmount > 0) {
                    medicine.setStockLevel(medicine.getStockLevel() + replenishmentAmount);
                    System.out.println("Replenished " + replenishmentAmount + " units of " + medicine.getName());

                    // Update the stock level in medicine.txt
                    updateMedicineStockInFile(medicine);

                    // Remove the request entry from the request file
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEDICINE_REPLENISHMENT_REQUESTS))) {
                        for (String lineToWrite : lines) {
                            writer.write(lineToWrite);
                            writer.newLine();
                        }
                    } catch (IOException e) {
                        System.out.println("Error writing to medicine replenishment request file: " + e.getMessage());
                    }
                } else {
                    System.out.println("No replenishment amount found for the specified Medicine ID.");
                }
            } catch (IOException e) {
                System.out.println("Error reading medicine replenishment request file: " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine replenishment request file: " + e.getMessage());
        }
    }

    // Helper method to update the medicine.txt file with the replenished stock
    // level
    private void updateMedicineStockInFile(Medicine updatedMedicine) {
        List<String> fileContent = new ArrayList<>();

        // Read all lines from the medicine file and update the specific entry
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data[0].equals(updatedMedicine.getMedicineId())) {
                    // Update the stock level for the specified medicine
                    data[3] = String.valueOf(updatedMedicine.getStockLevel());
                    line = String.join("|", data);
                }
                fileContent.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine file: " + e.getMessage());
            return;
        }

        // Write the updated content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEDICINE_FILE))) {
            for (String fileLine : fileContent) {
                writer.write(fileLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating medicine file: " + e.getMessage());
        }
    }

    // Check low stock medicines and display them to the user
    public List<Medicine> checkLowStock() {
        System.out.println("\n--- Check Low Stock Medicines ---");

        List<Medicine> lowStockMedicines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_REPLENISHMENT_REQUESTS))) {
            String line;
            System.out.println(
                    String.format("%-15s %-30s %-10s %-18s %-30s",
                            "Medicine ID", "Name", "Stock", "Low Stock Level", "Replenishment Requested Amount"));
            System.out.println(
                    "-----------------------------------------------------------------------------------------------------------");

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                String medicineId = data[0];
                int replenishmentAmount = Integer.parseInt(data[1]);

                Medicine medicine = findMedicineById(medicineId);
                if (medicine != null && medicine.isLowStockLevelAlert()) {
                    lowStockMedicines.add(medicine);
                    System.out.println(
                            String.format("%-15s %-30s %-10d %-18d %-30d", medicine.getMedicineId(),
                                    medicine.getName(), medicine.getStockLevel(),
                                    medicine.getLowStockLevel(), replenishmentAmount));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine replenishment request file: " + e.getMessage());
        }

        if (lowStockMedicines.isEmpty()) {
            System.out.println("No medicines with low stock.");
        }

        return lowStockMedicines;
    }

    // Replenishment request (updated to use String medicineId)
    public void requestReplenishment() {
        System.out.println("\n--- Submit Replenishment Request ---");
        System.out.println("------------------------------------");
        Scanner scanner = new Scanner(System.in);

        // Prompt for Medicine ID
        System.out.print("Enter Medicine ID for replenishment request: ");
        String medicineId = scanner.nextLine().trim();

        // Find the medicine by ID
        Medicine medicine = findMedicineById(medicineId);
        if (medicine == null) {
            System.out.println("Medicine ID: " + medicineId + " not found in inventory.");
            return;
        }

        // Check if replenishment is necessary
        if (!medicine.isLowStockLevelAlert() && medicine.getStockLevel() != 0) {
            System.out.println(
                    "Replenishment not needed for medicine ID: " + medicineId + " as stock is sufficient.");
            return;
        }

        // Prompt for replenishment amount
        System.out.print("Enter replenishment amount: ");
        int replenishmentAmount;
        try {
            replenishmentAmount = Integer.parseInt(scanner.nextLine().trim());
            if (replenishmentAmount <= 0) {
                System.out.println("Invalid amount. Please enter a positive integer.");
                return;
            }

            // Add the replenishment request to the file
            FileUtils.writeToFile(MEDICINE_REPLENISHMENT_REQUESTS,
                    medicineId + "|" + replenishmentAmount + "|" + medicine.getName());
            System.out
                    .println("Replenishment request submitted for medicine ID: " + medicineId + " with amount: "
                            + replenishmentAmount);

            // Set status to "Pending Replenishment Request"
            medicine.setPendingReplenishmentRequest();

            // Save changes to file
            saveMedicinesToFile();
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Please enter a valid numeric value.");
        }
        System.out.println("------------------------------------");

        // back to main menu
        promptReturnToMenu();
    }

    // Find a specific medicine by its ID
    public Medicine findMedicineById(String medicineId) {
        for (Medicine medicine : medicines) {
            if (medicine.getMedicineId().equals(medicineId)) { // Compare using equals for Strings
                return medicine;
            }
        }
        return null; // Medicine not found
    }

    // Return to main menu
    private void promptReturnToMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter 0 to return: ");
        while (!"0".equals(scanner.nextLine().trim())) {
            System.out.print("Invalid input. Please enter 0 to return: ");
        }
    }
}
