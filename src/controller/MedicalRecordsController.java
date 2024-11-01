package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import entity.MedicalRecord;
import entity.Patient;
import utility.FileUtils;
import utility.PrintUtils;

public class MedicalRecordsController {

    private List<Patient> patients;
    private List<MedicalRecord> medicalRecords;

    private static final String ACCOUNT_TXT = "data/account.txt";
    private static final String MEDICALRECORDS_TXT = "data/medicalRecords.txt";

    public MedicalRecordsController() {
        this.patients = new ArrayList<>();
        this.medicalRecords = new ArrayList<>();

        System.out.println("medicalrecordscontrollerconstructor\n");
        // Load patient records
        try {
            loadPatientsFromFile("data/patient.txt"); 
        } catch (IOException e) {
            System.out.println("Error loading patients: " + e.getMessage());
        }

        // Load medical records
        try {
            loadMedicalRecordsFromFile("data/medicalRecords.txt"); 
        } catch (IOException e) {
            System.out.println("Error loading medical records: " + e.getMessage());
        }
    }

    // Load patients from file
    private void loadPatientsFromFile(String filename) throws IOException {
        System.out.println("Loading patient file\n");
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                
                if (data.length < 9) {
                    System.out.println("Skipping invalid patient record: " + line);
                    continue;
                }
                
                Patient patient = new Patient(data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8]);
                patients.add(patient);
            }
        }
    }

    // Load medical records from file
    //Sample file: PA00005|NIL|NIL|some notes
    private void loadMedicalRecordsFromFile(String filename) throws IOException {
        System.out.println("Loading medical records file\n");
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");

                if (data.length < 4) {
                    System.out.println("Skipping invalid medical record: " + line);
                    continue;
                }
                
                String patientId = data[0];
                String allergies = data[1];
                List<String> appointmentOutcomeIds = List.of(data[2].split(","));
                String notes = data[3];
                
                // Find the associated patient using patientId
                Patient patient = findPatientById(patientId);
                
                if (patient != null) {
                    MedicalRecord record = new MedicalRecord(patientId, allergies, appointmentOutcomeIds, notes);
                    medicalRecords.add(record);
                } else {
                    System.out.println("No matching patient found for ID: " + patientId);
                }
            }
        }
    }

    // Helper method to find a patient by ID
    private Patient findPatientById(String patientId) {
        for (Patient patient : patients) {
            if (patient.getUserId().equals(patientId)) {
                return patient;
            }
        }
        return null;
    }

    // Method to select patient to perform CRUD on medical records.
    public String selectPatient() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Select a Patient ---");
        for (Patient patient : patients) {
            System.out.println("------------------------------------------------------");
            System.out.println(patient);  // Assuming Patient class has a meaningful toString() method
            System.out.println("------------------------------------------------------");
        }
        
        System.out.print("Enter the patient ID to view medical records (or 0 to exit): ");
        String patientId = scanner.nextLine().trim();

        if (patientId.equals("0")) {
            return null;  // Exit option
        }

        if (findPatientById(patientId) != null) {
            return patientId;  // Return the valid patient ID
        } else {
            System.out.println("Invalid patient ID. Please try again.");
            return selectPatient();  // Recursive call to re-prompt
        }
    }

    // Method to create a medical record for a patient
    public void createMedicalRecord(String patientId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Creating Medical Record for Patient ID: " + patientId + " ---");
        
        System.out.print("Enter allergies (or NIL if none): ");
        String allergiesInput = scanner.nextLine().trim();
        String allergies = allergiesInput.equalsIgnoreCase("NIL") ? null : allergiesInput; // Set to null if NIL

        // Initialize appointmentOutcomeIds as an empty list
        List<String> appointmentOutcomeIds = new ArrayList<>();

        System.out.print("Enter notes (or NIL if none): ");
        String notes = scanner.nextLine().trim();
        if (notes.equalsIgnoreCase("NIL")) {
            notes = "";
        }

        MedicalRecord newRecord = new MedicalRecord(patientId, allergies, appointmentOutcomeIds, notes);
        medicalRecords.add(newRecord);
        System.out.println("Medical record created successfully for patient ID: " + patientId);

        FileUtils.writeToFile(MEDICALRECORDS_TXT, patientId +'|'+ allergies +'|'+ appointmentOutcomeIds +'|'+ notes);

        // Display success message
        System.out.println(patientId + " Created Medical record successfully!");
    }

    public void updateMedicalRecord(String patientId, List<String> appointmentOutcomeIds) {
        // Find the medical record associated with the patient ID
        MedicalRecord recordToUpdate = null;
        for (MedicalRecord record : medicalRecords) {
            if (record.getPatientId().equals(patientId)) {
                recordToUpdate = record;
                break;
            }
        }
    
        // If no record is found, print a message and return
        if (recordToUpdate == null) {
            System.out.println("No medical record found for Patient ID: " + patientId);
            return;
        }
    
        // Create a new Scanner for input
        Scanner scanner = new Scanner(System.in);
        
        // Update allergies
        System.out.print("Enter new allergies (or press Enter to keep current): ");
        String allergiesInput = scanner.nextLine().trim();
        if (!allergiesInput.isEmpty()) {
            String allergies = allergiesInput.equalsIgnoreCase("NIL") ? null : allergiesInput; // Set to null if NIL
            recordToUpdate.setAllergy(allergies); // Assuming there's a setter for allergies
        }
    
        // Update appointment outcomes
        if (appointmentOutcomeIds != null) {
            recordToUpdate.setAppointmentOutcomeId(appointmentOutcomeIds); // Assuming there's a setter for appointment outcome IDs
        }
    
        // Update notes
        System.out.print("Enter new notes (or press Enter to keep current): ");
        String notesInput = scanner.nextLine().trim();
        if (!notesInput.isEmpty()) {
            recordToUpdate.setNotes(notesInput.equalsIgnoreCase("NIL") ? "" : notesInput); // Assuming there's a setter for notes
        }
    
        System.out.println("Medical record updated successfully for Patient ID: " + patientId);

        FileUtils.writeToFile(MEDICALRECORDS_TXT, patientId +'|'+ allergiesInput +'|'+ appointmentOutcomeIds +'|'+ notesInput);

        // Display success message
        System.out.println(patientId + " Update Medical record successfully!");
    }

    public void deleteMedicalRecord(String patientId) {
        // Find the medical record associated with the patient ID
        MedicalRecord recordToDelete = null;
        for (MedicalRecord record : medicalRecords) {
            if (record.getPatientId().equals(patientId)) {
                recordToDelete = record;
                break;  // Exit loop after finding the record
            }
        }
    
        // If no record is found, print a message and return
        if (recordToDelete == null) {
            System.out.println("No medical record found for Patient ID: " + patientId);
            return;
        }
    
        // Confirm deletion with the user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you want to delete the medical record for Patient ID: " + patientId + "? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
    
        if (confirmation.equals("yes")) {
            medicalRecords.remove(recordToDelete);  // Remove the medical record from the list
            System.out.println("Medical record for Patient ID: " + patientId + " has been deleted successfully.");
        } else {
            System.out.println("Deletion cancelled for Patient ID: " + patientId);
        }
    }
    
    // Method to view all medical records with associated patient information 
    public void viewMedicalRecords() {
        Scanner scanner = new Scanner(System.in);
        String patientId;
        while (true) {
            patientId = selectPatient();
            if (patientId == null) {
                System.out.println("Returning to main menu...");
                return;  // Exit if 0 was entered in selectPatient
            }

            MedicalRecord patientRecord = null;
            for (MedicalRecord record : medicalRecords) {
                if (record.getPatientId().equals(patientId)) {
                    patientRecord = record;
                    break;  // Exit loop after finding the record
                }
            }

            if (patientRecord == null) {
                System.out.println("No medical record found for patient ID: " + patientId);
                System.out.print("Would you like to create a new medical record for this patient? (yes/no): ");
                String choice = scanner.nextLine().trim().toLowerCase();
                if (choice.equals("yes")) {
                    createMedicalRecord(patientId);
                } else {
                    System.out.println("Returning to patient selection...");
                }
            } else {
                System.out.println("\n--- Medical Record for Patient ID: " + patientId + " ---");
                System.out.println(patientRecord);  // Assuming MedicalRecord class has a meaningful toString() method
                System.out.println("--------------------------------------");
                // Promt user to 1: Update medical records 2: Delete medical records 0: return;
                while (true) {
                    System.out.println("Select an option:");
                    System.out.println("1: Update medical record");
                    System.out.println("2: Delete medical record");
                    System.out.println("0: Return to patient selection");
                    System.out.print("Your choice: ");
                    String option = scanner.nextLine().trim();
    
                    switch (option) {
                        case "1":
                            // Call the updateMedicalRecord method with patientId and the current appointmentOutcomeIds
                            updateMedicalRecord(patientId, patientRecord.getAppointmentOutcomeId()); // Assuming getAppointmentOutcomeId() returns the current list
                            break;
                        case "2":
                            // Call the deleteMedicalRecord method (you will need to implement this)
                            deleteMedicalRecord(patientId);
                            break;
                        case "0":
                            System.out.println("Returning to patient selection...");
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                            continue;  // Restart the loop if the choice is invalid
                    }
                    break;  // Exit the inner loop if a valid option is selected
                }

            }
        }
    }


    
}
