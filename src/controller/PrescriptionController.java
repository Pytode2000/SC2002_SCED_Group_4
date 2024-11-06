package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import utility.PrintUtils;

import entity.Medicine;
import entity.Prescription;

public class PrescriptionController {

    private static final String APPOINTMENT_OUTCOME_FILE = "data/appointmentOutcome.txt";
    private static final String PRESCRIPTION_FILE = "data/prescription.txt";
    private static final String MEDICINE_FILE = "data/medicine.txt";

    // Method to update the prescription status
    public void updatePrescriptionStatus() {

        System.out.println("\n--- Update Prescription Status ---");
        System.out.println("------------------------------------");

        Scanner scanner = new Scanner(System.in);

        // Step 1: Prompt for Appointment ID
        System.out.print("Enter Appointment ID: ");
        String appointmentId = scanner.nextLine().trim();

        // Step 2: Retrieve and display only pending prescriptions for this appointment
        List<Prescription> prescriptions = getPendingPrescriptionsByAppointmentId(appointmentId);
        if (prescriptions.isEmpty()) {
            System.out.println("No pending prescriptions found for this appointment.");
            PrintUtils.pause();
            return;
        }

        System.out.println("Pending Prescriptions for Appointment ID: " + appointmentId);
        for (int i = 0; i < prescriptions.size(); i++) {
            Prescription prescription = prescriptions.get(i);
            System.out.printf("%d. %dx %s (%s)%n", i + 1, prescription.getQuantity(),
                    getMedicineName(prescription.getMedicineId()), prescription.getStatus());
        }

        // Step 3: Prompt for prescription selection
        System.out.print("Select the prescription number to update to 'DISPENSED': ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice < 1 || choice > prescriptions.size()) {
            System.out.println("Invalid choice.");
            PrintUtils.pause();
            return;
        }

        // Step 4: Check stock level before updating
        Prescription selectedPrescription = prescriptions.get(choice - 1);
        int quantityRequired = selectedPrescription.getQuantity();
        int currentStock = getStockLevel(selectedPrescription.getMedicineId());

        if (currentStock < quantityRequired) {
            System.out.println("Error: Not enough stock available for this medication. Current stock: " + currentStock);
            PrintUtils.pause();
            return;
        }

        // Step 5: Update the selected prescription status to DISPENSED
        updatePrescriptionStatusInFile(selectedPrescription.getPrescriptionId(), "DISPENSED");

        // Step 6: Update the stock level of the associated medication
        updateStockLevel(selectedPrescription.getMedicineId(), quantityRequired);

        System.out.println("Prescription status updated to 'DISPENSED' and stock level adjusted.");

        PrintUtils.pause();
    }

    // Method to retrieve only pending prescriptions based on appointment ID
    private List<Prescription> getPendingPrescriptionsByAppointmentId(String appointmentId) {
        List<Prescription> prescriptions = new ArrayList<>();

        try (BufferedReader outcomeReader = new BufferedReader(new FileReader(APPOINTMENT_OUTCOME_FILE))) {
            String line;
            while ((line = outcomeReader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].trim().equals(appointmentId)) {
                    String[] prescriptionIds = fields[5].trim().split(";");
                    for (String prescriptionId : prescriptionIds) {
                        Prescription prescription = getPrescriptionDetails(prescriptionId.trim());
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

    // Method to retrieve prescription details by ID
    private Prescription getPrescriptionDetails(String prescriptionId) {
        try (BufferedReader prescriptionReader = new BufferedReader(new FileReader(PRESCRIPTION_FILE))) {
            String line;
            while ((line = prescriptionReader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].trim().equals(prescriptionId)) {
                    String medicineId = fields[1].trim();
                    int quantity = Integer.parseInt(fields[2].trim());
                    Prescription.Status status = Prescription.Status.valueOf(fields[3].trim().toUpperCase());
                    return new Prescription(prescriptionId, medicineId, quantity, status);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading prescription file: " + e.getMessage());
        }
        return null;
    }

    // Method to retrieve the stock level of a medicine by ID
    private int getStockLevel(String medicineId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].trim().equals(medicineId)) {
                    return Integer.parseInt(fields[3].trim()); // Return the stock level
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Stock level in medicine file is not a valid number.");
        }
        return 0; // Return 0 if not found or error occurred
    }

    // Method to update the prescription status in the file
    private void updatePrescriptionStatusInFile(String prescriptionId, String newStatus) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(PRESCRIPTION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].trim().equals(prescriptionId)) {
                    fields[3] = newStatus; // Update status
                    line = String.join("|", fields);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading prescription file: " + e.getMessage());
        }

        try (FileWriter writer = new FileWriter(PRESCRIPTION_FILE)) {
            for (String updatedLine : lines) {
                writer.write(updatedLine + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error writing to prescription file: " + e.getMessage());
        }
    }

    // Method to update stock level of a medicine
    private void updateStockLevel(String medicineId, int quantityDispensed) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].trim().equals(medicineId)) {
                    int currentStock = Integer.parseInt(fields[3].trim()); // Read stockLevel (4th field)
                    fields[3] = String.valueOf(Math.max(0, currentStock - quantityDispensed)); // Update stock level
                    line = String.join("|", fields);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Stock level in medicine file is not a valid number.");
        }

        try (FileWriter writer = new FileWriter(MEDICINE_FILE)) {
            for (String updatedLine : lines) {
                writer.write(updatedLine + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error writing to medicine file: " + e.getMessage());
        }
    }

    // Method to retrieve medicine name by ID
    private String getMedicineName(String medicineId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].trim().equals(medicineId)) {
                    return fields[1].trim(); // Return medicine name
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine file: " + e.getMessage());
        }
        return "Unknown Medicine";
    }
}
