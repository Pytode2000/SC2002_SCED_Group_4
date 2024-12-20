package controller;

import entity.Prescription;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import utility.PrintUtils;

/**
 * The PrescriptionController class handles operations related to prescriptions, including
 * updating prescription statuses, managing medicine stock levels, and retrieving prescription details.
 * This class interacts with files to store and retrieve data.
 */

public class PrescriptionController {

    private static final String APPOINTMENT_OUTCOME_FILE = "data/appointmentOutcome.txt";
    private static final String PRESCRIPTION_FILE = "data/prescription.txt";
    private static final String MEDICINE_FILE = "data/medicine.txt";

    /**
     * Updates the status of a specific prescription and adjusts the medicine stock level.
     * Displays pending prescriptions for a given appointment ID, allows the user to select one,
     * and updates its status to "DISPENSED" if stock is sufficient.
     */
    public void updatePrescriptionStatus() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       Update Prescription Status       ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("═══════════════════════════════════════════════════════");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Appointment ID: ");
        String appointmentId = scanner.nextLine().trim();

        List<Prescription> prescriptions = getPendingPrescriptions(appointmentId);
        if (prescriptions.isEmpty()) {
            System.out.println("\nNo pending prescriptions found for this appointment.");
            System.out.println("═══════════════════════════════════════════════════════");
            PrintUtils.pause();
            return;
        }

        System.out.println("\nPending Prescriptions for Appointment ID: " + appointmentId);
        for (int i = 0; i < prescriptions.size(); i++) {
            Prescription prescription = prescriptions.get(i);
            System.out.printf("%d. %dx %s (%s)%n", i + 1, prescription.getQuantity(),
                    getMedicineName(prescription.getMedicineId()), prescription.getStatus());
        }

        System.out.print("Select the prescription to dispense (Enter number): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > prescriptions.size()) {
            System.out.println("\nInvalid choice. Please try again.");
            System.out.println("═══════════════════════════════════════════════════════");
            PrintUtils.pause();
            return;
        }

        Prescription selectedPrescription = prescriptions.get(choice - 1);
        int currentStock = getStockLevel(selectedPrescription.getMedicineId());

        if (currentStock < selectedPrescription.getQuantity()) {
            System.out.println("\nError: Not enough stock available for this medication.");
            System.out.println("Current stock: " + currentStock + " | Quantity required: " + selectedPrescription.getQuantity());
            System.out.println("═══════════════════════════════════════════════════════");
            PrintUtils.pause();
            return;
        }

        updateFileContent(PRESCRIPTION_FILE, selectedPrescription.getPrescriptionId(), "DISPENSED", 3);
        updateFileContent(MEDICINE_FILE, selectedPrescription.getMedicineId(),
                String.valueOf(Math.max(0, currentStock - selectedPrescription.getQuantity())), 3);

        System.out.println("\nPrescription successfully dispensed.");
        System.out.println("═══════════════════════════════════════════════════════");
        PrintUtils.pause();
    }

    // Retrieve pending prescriptions associated with a specific appointment ID
    /**
     * Retrieves a list of pending prescriptions associated with a specific appointment ID.
     *
     * @param appointmentId The ID of the appointment.
     * @return A list of pending prescriptions for the given appointment ID.
     */
    private List<Prescription> getPendingPrescriptions(String appointmentId) {
        List<Prescription> prescriptions = new ArrayList<>();
        try (BufferedReader outcomeReader = new BufferedReader(new FileReader(APPOINTMENT_OUTCOME_FILE))) {
            String line;
            while ((line = outcomeReader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].trim().equals(appointmentId)) {
                    for (String prescriptionId : fields[5].trim().split(",")) {
                        Prescription prescription = getPrescription(prescriptionId.trim());
                        if (prescription != null && prescription.getStatus() == Prescription.Status.PENDING) {
                            prescriptions.add(prescription);
                        }
                    }
                    
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading appointment outcome file: " + e.getMessage());
        }
        return prescriptions;
    }

    /**
     * Retrieves a specific prescription by its ID.
     *
     * @param prescriptionId The ID of the prescription to retrieve.
     * @return The Prescription object if found; otherwise, null.
     */
    // Retrieve a specific prescription by its ID
    private Prescription getPrescription(String prescriptionId) {
        try (BufferedReader prescriptionReader = new BufferedReader(new FileReader(PRESCRIPTION_FILE))) {
            String line;
            while ((line = prescriptionReader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].trim().equals(prescriptionId)) {
                    return new Prescription(prescriptionId, fields[1].trim(),
                            Integer.parseInt(fields[2].trim()), Prescription.Status.valueOf(fields[3].trim().toUpperCase()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading prescription file: " + e.getMessage());
        }
        return null;
    }

    // Retrieve the stock level of a specific medicine
    /**
     * Retrieves the stock level of a specific medicine.
     *
     * @param medicineId The ID of the medicine.
     * @return The stock level of the medicine, or 0 if not found.
     */
    private int getStockLevel(String medicineId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].trim().equals(medicineId)) {
                    return Integer.parseInt(fields[3].trim());
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading stock level: " + e.getMessage());
        }
        return 0;
    }

    // Update content in a file by ID and field index
    /**
     * Updates a specific field in a file for a given record ID.
     *
     * @param filePath   The path to the file to update.
     * @param id         The ID of the record to update.
     * @param newValue   The new value to set in the specified field.
     * @param fieldIndex The index of the field to update (0-based).
     */
    private void updateFileContent(String filePath, String id, String newValue, int fieldIndex) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].trim().equals(id)) {
                    fields[fieldIndex] = newValue;
                    line = String.join("|", fields);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            for (String updatedLine : lines) {
                writer.write(updatedLine + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // Retrieve the name of a specific medicine by its ID
    /**
     * Retrieves the name of a specific medicine by its ID.
     *
     * @param medicineId The ID of the medicine.
     * @return The name of the medicine, or "Unknown Medicine" if not found.
     */
    private String getMedicineName(String medicineId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].trim().equals(medicineId)) {
                    return fields[1].trim();
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine file: " + e.getMessage());
        }
        return "Unknown Medicine";
    }
}
