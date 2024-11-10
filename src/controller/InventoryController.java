package controller;

import entity.Medicine;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import utility.FileUtils;
import utility.PrintUtils;

public class InventoryController {

    private List<Medicine> medicines;
    private static final String MEDICINE_FILE = "data/medicine.txt";
    private static final String MEDICINE_REPLENISHMENT_REQUESTS = "data/medicineReplenishmentRequests.txt";

    // Constructor to initialize the inventory controller and load medicines from file
    public InventoryController() {
        this.medicines = new ArrayList<>();
        loadMedicinesFromFile();
    }

    // Load medicines from the file into the medicines list
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

    // Save the current list of medicines to the file
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

    // Generate a new unique medicine ID
    private String generateMedicineId() {
        String lastId = "MD00000";
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

    // Display the current inventory of medicines
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

    // Add a new medicine to the inventory
    public void addMedicine(Scanner scanner) {
        System.out.println("\n--- Add Medicine ---");

        try {
            if (medicines == null) {
                medicines = new ArrayList<>();
            }

            String medicineId = generateMedicineId();

            String name = promptForInput(scanner, "Enter Medicine Name (max 20 characters), or -1 to cancel: ", 1, 20, true);
            if (name == null) return;

            String description = promptForInput(scanner, "Enter Medicine Description (max 30 characters), or -1 to cancel: ", 1, 30, true);
            if (description == null) return;

            int stockLevel = promptForIntegerInput(scanner, "Enter Stock Level, or -1 to cancel: ", 0, Integer.MAX_VALUE);
            if (stockLevel == -1) return;

            int lowStockLevel = promptForIntegerInput(scanner, "Enter Low Stock Level, or -1 to cancel: ", 0, stockLevel);
            if (lowStockLevel == -1) return;

            String medicineType = promptForInput(scanner, "Enter Medicine Type (max 15 characters), or -1 to cancel: ", 1, 15, true);
            if (medicineType == null) return;

            Medicine newMedicine = new Medicine(medicineId, name, description, stockLevel, lowStockLevel, medicineType);
            medicines.add(newMedicine);
            saveMedicinesToFile();

            System.out.println("Medicine added successfully with ID: " + medicineId);
        } catch (Exception e) {
            System.out.println("An error occurred while adding the medicine: " + e.getMessage());
        }
    }

    // Update an existing medicine in the inventory
    public void updateMedicine(Scanner scanner) {
        System.out.println("\n--- Update Medicine ---");

        int index = promptForIntegerInput(scanner, "Enter the index of the medicine to update (or -1 to cancel): ", 1, medicines.size());
        if (index == -1) return;

        Medicine medicine = medicines.get(index - 1);
        boolean anyFieldUpdated = false;

        String name = promptForInput(scanner, "Enter the new name (leave blank to keep current value): ", 1, 20, false);
        if (name != null) {
            medicine.setName(name);
            anyFieldUpdated = true;
        }

        String description = promptForInput(scanner, "Enter the new description (leave blank to keep current value): ", 1, 30, false);
        if (description != null) {
            medicine.setDescription(description);
            anyFieldUpdated = true;
        }

        int stockLevel = promptForIntegerInput(scanner, "Enter the new stock level (leave blank to keep current value): ", 0, Integer.MAX_VALUE);
        if (stockLevel != -1) {
            medicine.setStockLevel(stockLevel);
            anyFieldUpdated = true;
        }

        int lowStockLevel = promptForIntegerInput(scanner, "Enter the new low stock level (leave blank to keep current value): ", 0, stockLevel);
        if (lowStockLevel != -1) {
            medicine.setLowStockLevel(lowStockLevel);
            anyFieldUpdated = true;
        }

        String medicineType = promptForInput(scanner, "Enter the new medicine type (leave blank to keep current value): ", 1, 15, false);
        if (medicineType != null) {
            medicine.setMedicineType(medicineType);
            anyFieldUpdated = true;
        }

        if (!anyFieldUpdated) {
            System.out.println("No fields were updated for medicine ID: " + medicine.getMedicineId());
        } else {
            saveMedicinesToFile();
            System.out.println("Update successful for medicine ID: " + medicine.getMedicineId());
        }
    }

    // Remove a medicine from the inventory
    public void removeMedicine(Scanner scanner) {
        System.out.println("\n--- Remove Medicine ---");

        int index = promptForIntegerInput(scanner, "Enter the index of the medicine to remove (or -1 to cancel): ", 1, medicines.size());
        if (index == -1) return;

        medicines.remove(index - 1);
        saveMedicinesToFile();
        System.out.println("Medicine removed successfully.");
    }

    // Approve replenishment requests for low stock medicines
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

                    updateMedicineStockInFile(medicine);

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

    // Update the stock level of a medicine in the file
    private void updateMedicineStockInFile(Medicine updatedMedicine) {
        List<String> fileContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data[0].equals(updatedMedicine.getMedicineId())) {
                    data[3] = String.valueOf(updatedMedicine.getStockLevel());
                    line = String.join("|", data);
                }
                fileContent.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine file: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEDICINE_FILE))) {
            for (String fileLine : fileContent) {
                writer.write(fileLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating medicine file: " + e.getMessage());
        }
    }

    // Check and display medicines with low stock levels
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

    // Submit a replenishment request for a medicine
    public void requestReplenishment() {
        System.out.println("\n--- Submit Replenishment Request ---");
        System.out.println("=========================================================================");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Medicine ID for replenishment request: ");
        String medicineId = scanner.nextLine().trim();

        Medicine medicine = findMedicineById(medicineId);
        if (medicine == null) {
            System.out.println("Medicine ID: " + medicineId + " not found in inventory.");
            System.out.println("=========================================================================");
            PrintUtils.pause();
            return;
        }

        if (!medicine.isLowStockLevelAlert() && medicine.getStockLevel() != 0) {
            System.out.println(
                    "Replenishment not needed for medicine ID: " + medicineId + " as stock is sufficient.");
            System.out.println("=========================================================================");
            PrintUtils.pause();
            return;
        }

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

            FileUtils.writeToFile(MEDICINE_REPLENISHMENT_REQUESTS,
                    medicineId + "|" + replenishmentAmount + "|" + medicine.getName());
            System.out
                    .println("Replenishment request submitted for medicine ID: " + medicineId + " with amount: "
                            + replenishmentAmount);

            medicine.setPendingReplenishmentRequest();
            saveMedicinesToFile();
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Please enter a valid numeric value.");
        }
        System.out.println("=========================================================================");

        PrintUtils.pause();
    }

    // Find a medicine by its ID
    public Medicine findMedicineById(String medicineId) {
        for (Medicine medicine : medicines) {
            if (medicine.getMedicineId().equals(medicineId)) {
                return medicine;
            }
        }
        return null;
    }

    // Prompt the user to return to the menu
    private void promptReturnToMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter -1 to return: ");
        while (!"-1".equals(scanner.nextLine().trim())) {
            System.out.print("Invalid input. Please enter -1 to return: ");
        }
    }

    // Prompt the user for input with validation
    private String promptForInput(Scanner scanner, String prompt, int minLength, int maxLength, boolean isRequired) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equals("-1")) {
                System.out.println("Operation cancelled.");
                return null;
            }
            if (input.length() >= minLength && input.length() <= maxLength) {
                return input;
            }
            if (!isRequired && input.isEmpty()) {
                return null;
            }
            System.out.printf("Input must be between %d and %d characters. Please try again.%n", minLength, maxLength);
        }
    }

    // Prompt the user for integer input with validation
    private int promptForIntegerInput(Scanner scanner, String prompt, int minValue, int maxValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equals("-1")) {
                System.out.println("Operation cancelled.");
                return -1;
            }
            try {
                int value = Integer.parseInt(input);
                if (value >= minValue && value <= maxValue) {
                    return value;
                }
                System.out.printf("Input must be between %d and %d. Please try again.%n", minValue, maxValue);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}