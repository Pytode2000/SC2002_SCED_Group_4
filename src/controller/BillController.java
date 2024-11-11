package controller;

import entity.Bill;
import utility.FileUtils;
import utility.PrintUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BillController {

    public static void createBill(String FILE_PATH, String appointmentId, String patientId) {
        Bill bill = new Bill(appointmentId, patientId);
        String data = appointmentId + "|" + patientId + "|" + bill.getStatus() + "|" + String.format("%.2f", bill.getCost()) + "|" + bill.getDatetime();

        // Write to file
        FileUtils.writeToFile(FILE_PATH, data);
    }

    public static void viewAndUpdatePendingBills() {
        String FILE_PATH = "data/bill.txt";
        File file = new File(FILE_PATH);

        // Check if file exists and is readable
        if (!file.exists() || !file.canRead()) {
            System.out.println("Error: Bill file not found or is unreadable.");
            return;
        }

        List<String> bills = FileUtils.readAllLines(FILE_PATH); // Read all lines from file
        List<String> processingBills = new ArrayList<>();

        // Filter out only processing bills and index them
        int index = 1;
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              Bill Patients             ║");
        System.out.println("╚════════════════════════════════════════╝");

        for (String line : bills) {
            String[] fields = line.split("\\|");
            if (fields.length < 5 || !fields[2].equals("PROCESSING")) {
                continue; // Skip malformed lines or non-processing bills
            }
            processingBills.add(line);
            System.out.printf("%d. %s / %s / %s\n", index++, fields[0], fields[1], fields[4]);
        }
        System.out.println("══════════════════════════════════════════");

        if (processingBills.isEmpty()) {
            System.out.println("No processing bills found.");
            PrintUtils.pause();
            return;
        }

        // Selecting a bill by index
        Scanner scanner = new Scanner(System.in);
        int selection = -1;
        while (true) {
            System.out.print("Enter the number of the bill you want to update (or 0 to exit): ");
            if (scanner.hasNextInt()) {
                selection = scanner.nextInt();
                if (selection == 0) {
                    System.out.println("Exiting...");
                    PrintUtils.pause();
                    return; // Exit if user enters 0
                } else if (selection >= 1 && selection <= processingBills.size()) {
                    break;
                } else {
                    System.out.println("Invalid selection. Please enter a number between 1 and " + processingBills.size() + " or 0 to exit.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // Clear invalid input
            }
        }

        String selectedBill = processingBills.get(selection - 1);
        String[] selectedFields = selectedBill.split("\\|");

        // Input new cost with validation
        double newCost = -1;
        while (true) {
            System.out.print("Enter the new cost for the bill: ");
            if (scanner.hasNextDouble()) {
                newCost = scanner.nextDouble();
                if (newCost >= 0) { // Ensure cost is non-negative
                    break;
                } else {
                    System.out.println("Cost cannot be negative. Please enter a valid cost.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid numeric cost.");
                scanner.next(); // Clear invalid input
            }
        }

        // Round cost to 2 decimal places and update bill data
        selectedFields[2] = "BILLED"; // Status
        selectedFields[3] = String.format("%.2f", newCost); // Cost rounded to 2 decimal places

        String updatedBill = String.join("|", selectedFields);
        FileUtils.updateToFile(FILE_PATH, updatedBill, selectedFields[0]); // AppointmentID as ID
        System.out.println("Bill updated successfully.");
        PrintUtils.pause();
    }

    public static void viewAndPayBills(String patientId) {
        String FILE_PATH = "data/bill.txt";
        File file = new File(FILE_PATH);

        // Check if file exists and is readable
        if (!file.exists() || !file.canRead()) {
            System.out.println("Error: Bill file not found or is unreadable.");
            return;
        }

        List<String> bills = FileUtils.readAllLines(FILE_PATH); // Read all lines from file

        Scanner scanner = new Scanner(System.in);

        while (true) {
            List<String> processingBills = new ArrayList<>();
            List<String> billedBills = new ArrayList<>();
            List<String> paidBills = new ArrayList<>();

            // Categorize bills by status for the specified patientId
            for (String line : bills) {
                String[] fields = line.split("\\|");
                if (fields.length >= 5 && fields[1].equals(patientId)) { // Check if patientId matches
                    switch (fields[2]) {
                        case "PROCESSING":
                            processingBills.add(line);
                            break;
                        case "BILLED":
                            billedBills.add(line);
                            break;
                        case "PAID":
                            paidBills.add(line);
                            break;
                    }
                }
            }

            // Display categorized bills
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║               Your Bills               ║");
            System.out.println("╚════════════════════════════════════════╝");

            // Show processing bills
            System.out.println("\nINCOMING BILLS");
            for (String line : processingBills) {
                String[] fields = line.split("\\|");
                System.out.printf("- %s (%s)\n", fields[0], fields[4]);
            }
            System.out.println("══════════════════════════════════════════");

            // Show paid bills
            System.out.println("\nPAID BILLS:");
            for (String line : paidBills) {
                String[] fields = line.split("\\|");
                System.out.printf("- %s (%s) - $%s\n", fields[0], fields[4], fields[3]);
            }

            System.out.println("══════════════════════════════════════════");

            // Show billed bills with indexing
            System.out.println("\nBILLED BILLS (Select to Pay):");
            int index = 1;
            for (String line : billedBills) {
                String[] fields = line.split("\\|");
                System.out.printf("%d. %s (%s) - $%s\n", index++, fields[0], fields[4], fields[3]);
            }
            System.out.println("══════════════════════════════════════════");

            if (billedBills.isEmpty()) {
                System.out.println("No billed bills available for payment.");
                PrintUtils.pause();
                return;
            }

            System.out.print("Enter the number of the billed bill to pay (or 0 to exit): ");
            if (scanner.hasNextInt()) {
                int selection = scanner.nextInt();
                if (selection == 0) {
                    System.out.println("Exiting to main view.");
                    PrintUtils.pause();
                    return; // Exit to main view
                } else if (selection >= 1 && selection <= billedBills.size()) {
                    String selectedBill = billedBills.get(selection - 1);
                    String[] selectedFields = selectedBill.split("\\|");

                    // Display payment options
                    System.out.println("\n╔════════════════════════════════════════╗");
                    System.out.println("║                Pay Bill                ║");
                    System.out.println("╚════════════════════════════════════════╝");
                    System.out.printf("%s (%s) - $%s\n", selectedFields[0], selectedFields[4], selectedFields[3]);
                    System.out.println("1. Pay");
                    System.out.println("0. Exit");
                    System.out.println("══════════════════════════════════════════");

                    System.out.print("Choose an option: ");
                    if (scanner.hasNextInt()) {
                        int payOption = scanner.nextInt();
                        if (payOption == 1) {
                            selectedFields[2] = "PAID"; // Update status to PAID
                            String updatedBill = String.join("|", selectedFields);
                            FileUtils.updateToFile(FILE_PATH, updatedBill, selectedFields[0]); // Update in file by AppointmentID
                            System.out.println("Payment successful!");
                            PrintUtils.pause();
                            bills = FileUtils.readAllLines(FILE_PATH); // Refresh bills after update
                        } else if (payOption == 0) {
                            System.out.println("Exiting to main view.");
                            PrintUtils.pause();
                        } else {
                            System.out.println("Invalid option.");
                        }
                    } else {
                        System.out.println("Invalid input. Please enter 1 to pay or 0 to exit.");
                        scanner.next(); // Clear invalid input
                    }
                } else {
                    System.out.println("Invalid selection. Please enter a number between 1 and " + billedBills.size() + " or 0 to exit.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); // Clear invalid input
            }
        }
    }

}
