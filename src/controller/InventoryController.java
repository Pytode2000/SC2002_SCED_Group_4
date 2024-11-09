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
        System.out.println(String.format("%-10s %-15s %-25s %-15s %-35s %-12s %-18s %-15s",
                "Index", "Medicine ID", "Name", "Type", "Description", "Quantity", "Low Stock Level", "Status"));
        System.out.println(
                "------------------------------------------------------------------------------------------------------------------------------------------------");

        int index = 1;
        for (Medicine medicine : medicines) {
            System.out.printf("%-10d %-15s %-25s %-15s %-35s %-12d %-18d %-15s%n",
                    index++, medicine.getMedicineId(), medicine.getName(),
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
        System.out.println("\n--- Add Medicine ---");

        try {
            if (medicines == null) {
                medicines = new ArrayList<>();
            }

            String medicineId = generateMedicineId();

            String name;
            while (true) {
                System.out.print("Enter Medicine Name (max 20 characters), or -1 to cancel: ");
                name = scanner.nextLine().trim();

                if (name.equals("-1")) {
                    System.out.println("Operation cancelled. Exiting add medicine menu.");
                    return;
                }

                if (name == null || name.isEmpty() || name.length() > 20) {
                    System.out.println("Medicine name must be between 1 and 20 characters. Please enter a valid name.");
                } else if (findMedicineById(name) != null) {
                    System.out.println("Medicine name already exists. Please enter a unique name.");
                } else {
                    break;
                }
            }

            String description;
            while (true) {
                System.out.print("Enter Medicine Description (max 30 characters), or -1 to cancel: ");
                description = scanner.nextLine().trim();

                if (description.equals("-1")) {
                    System.out.println("Operation cancelled. Exiting add medicine menu.");
                    return;
                }

                if (description == null || description.isEmpty() || description.length() > 30) {
                    System.out.println(
                            "Medicine description must be between 1 and 30 characters. Please enter a valid description.");
                } else {
                    break;
                }
            }

            int stockLevel;
            while (true) {
                System.out.print("Enter Stock Level, or -1 to cancel: ");
                String stockLevelStr = scanner.nextLine().trim();

                if (stockLevelStr.equals("-1")) {
                    System.out.println("Operation cancelled. Exiting add medicine menu.");
                    return;
                }

                try {
                    stockLevel = Integer.parseInt(stockLevelStr);

                    if (stockLevel < 0) {
                        System.out.println("Stock level cannot be negative. Please enter a valid number.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Stock level must be a valid integer. Please try again.");
                }
            }

            int lowStockLevel;
            while (true) {
                System.out.print("Enter Low Stock Level, or -1 to cancel: ");
                String lowStockLevelStr = scanner.nextLine().trim();

                if (lowStockLevelStr.equals("-1")) {
                    System.out.println("Operation cancelled. Exiting add medicine menu.");
                    return;
                }

                try {
                    lowStockLevel = Integer.parseInt(lowStockLevelStr);

                    if (lowStockLevel < 0) {
                        System.out.println("Low stock level cannot be negative. Please enter a valid number.");
                    } else if (lowStockLevel > stockLevel) {
                        System.out.println("Low stock level cannot be greater than stock level. Please try again.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Low stock level must be a valid integer. Please try again.");
                }
            }

            String medicineType;
            while (true) {
                System.out.print("Enter Medicine Type (max 15 characters), or -1 to cancel: ");
                medicineType = scanner.nextLine().trim();

                if (medicineType.equals("-1")) {
                    System.out.println("Operation cancelled. Exiting add medicine menu.");
                    return;
                }

                if (medicineType == null || medicineType.isEmpty() || medicineType.length() > 15) {
                    System.out.println("Medicine type must be between 1 and 15 characters. Please enter a valid type.");
                } else {
                    break;
                }
            }

            // Create and add new Medicine object
            Medicine newMedicine = new Medicine(medicineId, name, description, stockLevel, lowStockLevel, medicineType);
            medicines.add(newMedicine);

            // Save the updated list to the file
            saveMedicinesToFile();

            System.out.println("Medicine added successfully with ID: " + medicineId);
        } catch (NullPointerException e) {
            System.out.println("Error: null pointer reference. Please try again.");
        } catch (Exception e) {
            System.out.println("An error occurred while adding the medicine: " + e.getMessage());
        }
    }

    // Update an existing medicine
    public void updateMedicine(Scanner scanner) {
        System.out.println("\n--- Update Medicine ---");

        while (true) {
            System.out.print("Enter the index of the medicine to update (or -1 to cancel): ");
            int index;
            try {
                index = Integer.parseInt(scanner.nextLine().trim());
                if (index == -1) {
                    System.out.println("Operation cancelled. Exiting update medicine menu.");
                    return;
                } else if (index < 0 || index > medicines.size()) {
                    System.out.println(
                            "Invalid index. Please enter a valid number between 1 and " + medicines.size() + ".");
                    continue;
                }
                index--;

            } catch (NumberFormatException e) {
                System.out.println("Invalid index format. Please enter a valid integer.");
                continue;
            }

            Medicine medicine = medicines.get(index);
            boolean anyFieldUpdated = false;

            while (true) {
                System.out.print("Enter the new name (leave blank to keep current value): ");
                String name = scanner.nextLine().trim();
                if (!name.isEmpty()) {
                    if (name.length() > 20) {
                        System.out.println(
                                "Name length is too long. Please enter a name with a maximum length of 20 characters.");
                        continue;
                    }
                    medicine.setName(name);
                    anyFieldUpdated = true;
                }
                break;
            }

            while (true) {
                System.out.print("Enter the new description (leave blank to keep current value): ");
                String description = scanner.nextLine().trim();
                if (!description.isEmpty()) {
                    if (description.length() > 30) {
                        System.out.println(
                                "Description length is too long. Please enter a description with a maximum length of 30 characters.");
                        continue;
                    }
                    medicine.setDescription(description);
                    anyFieldUpdated = true;
                }
                break;
            }

            while (true) {
                System.out.print("Enter the new stock level (leave blank to keep current value): ");
                String stockLevelInput = scanner.nextLine().trim();
                if (!stockLevelInput.isEmpty()) {
                    try {
                        int stockLevel = Integer.parseInt(stockLevelInput);
                        if (stockLevel < 0) {
                            System.out.println("Stock level cannot be negative. Please enter a positive integer.");
                            continue;
                        }
                        medicine.setStockLevel(stockLevel);
                        anyFieldUpdated = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format. Please enter valid integers for stock levels.");
                        continue;
                    }
                }
                break;
            }

            while (true) {
                System.out.print("Enter the new low stock level (leave blank to keep current value): ");
                String lowStockLevelInput = scanner.nextLine().trim();
                if (!lowStockLevelInput.isEmpty()) {
                    try {
                        int lowStockLevel = Integer.parseInt(lowStockLevelInput);
                        if (lowStockLevel < 0) {
                            System.out.println("Low stock level cannot be negative. Please enter a positive integer.");
                            continue;
                        }
                        if (lowStockLevel > medicine.getStockLevel()) {
                            System.out.println("Low stock level cannot be greater than stock level. Please try again.");
                            continue;
                        }
                        medicine.setLowStockLevel(lowStockLevel);
                        anyFieldUpdated = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format. Please enter valid integers for stock levels.");
                        continue;
                    }
                }
                break;
            }

            while (true) {
                System.out.print("Enter the new medicine type (leave blank to keep current value): ");
                String medicineType = scanner.nextLine().trim();
                if (!medicineType.isEmpty()) {
                    if (medicineType.length() > 15) {
                        System.out.println(
                                "Medicine type length is too long. Please enter a medicine type with a maximum length of 15 characters.");
                        continue;
                    }
                    medicine.setMedicineType(medicineType);
                    anyFieldUpdated = true;
                }
                break;
            }

            if (!anyFieldUpdated) {
                System.out.println("No fields were updated for medicine ID: " + medicine.getMedicineId());
            } else {
                // Save the updated list to the file
                saveMedicinesToFile();
                System.out.println("Update successful for medicine ID: " + medicine.getMedicineId());
            }
            break;
        }
    }

    // Remove a specific medicine by its index
    public void removeMedicine(Scanner scanner) {
        System.out.println("\n--- Remove Medicine ---");

        while (true) {
            System.out.print("Enter the index of the medicine to remove (or -1 to cancel): ");
            int index;
            try {
                index = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid index entered. Please enter a valid number.");
                continue;
            }

            if (index == -1) {
                System.out.println("Operation cancelled. Exiting remove medicine menu.");
                return;
            }

            index--; // Convert to zero-based index after cancel check

            if (index < 0 || index >= medicines.size()) {
                System.out
                        .println("Invalid index. Please enter a valid number between 1 and " + medicines.size() + ".");
                continue;
            }

            try {
                medicines.remove(index);

                // Save the updated list to the file
                saveMedicinesToFile();

                System.out.println("Medicine removed successfully.");
                break;
            } catch (Exception e) {
                System.out.println("An error occurred while removing the medicine: " + e.getMessage());
            }
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
            System.out.print("\nEnter the Medicine ID to approve replenishment (enter -1 to cancel): ");
            String medicineId = scanner.nextLine().trim();

            if (medicineId.equals("-1")) {
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

            int totalReplenishmentAmount = 0;
            // Read the replenishment amount from the request file
            try (BufferedReader reader2 = new BufferedReader(new FileReader(MEDICINE_REPLENISHMENT_REQUESTS))) {
                String line2;
                List<String> lines = new ArrayList<>();
                while ((line2 = reader2.readLine()) != null) {
                    String[] data2 = line2.split("\\|");
                    if (data2[0].equals(medicineId)) {
                        totalReplenishmentAmount += Integer.parseInt(data2[1]);
                    } else {
                        lines.add(line2);
                    }
                }

                if (totalReplenishmentAmount > 0) {
                    medicine.setStockLevel(medicine.getStockLevel() + totalReplenishmentAmount);
                    System.out.println("Replenished " + totalReplenishmentAmount + " units of " + medicine.getName());

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
        System.out.println("=========================================================================");
        Scanner scanner = new Scanner(System.in);

        // Prompt for Medicine ID
        System.out.print("Enter Medicine ID for replenishment request: ");
        String medicineId = scanner.nextLine().trim();

        // Find the medicine by ID
        Medicine medicine = findMedicineById(medicineId);
        if (medicine == null) {
            System.out.println("Medicine ID: " + medicineId + " not found in inventory.");
            System.out.println("=========================================================================");
            PrintUtils.pause();
            return;
        }

        // Check if replenishment is necessary
        if (!medicine.isLowStockLevelAlert() && medicine.getStockLevel() != 0) {
            System.out.println(
                    "Replenishment not needed for medicine ID: " + medicineId + " as stock is sufficient.");
            System.out.println("=========================================================================");
            PrintUtils.pause();
            return;
        }

        // Prompt for replenishment amount
        System.out.print("Enter replenishment amount: ");
        int replenishmentAmount;
        try {
            replenishmentAmount = Integer.parseInt(scanner.nextLine().trim());
            if (replenishmentAmount <= 0) {
                System.out.println("Invalid amount. negative value.");
                System.out.println("=========================================================================");
                PrintUtils.pause();
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
        System.out.println("=========================================================================");

        PrintUtils.pause();
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
