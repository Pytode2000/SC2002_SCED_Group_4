package controller;

import entity.Doctor;
import entity.MedicalRecord;
import entity.Patient;
import entity.User;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import utility.FileUtils;
import utility.PrintUtils;

public class MedicalRecordsController {

    private List<Patient> patients;
    private List<MedicalRecord> medicalRecords;
    private static final String MEDICALRECORDS_TXT = "data/medicalRecords.txt";
    private static final String PATIENT_TXT = "data/patient.txt";
    Scanner scanner = new Scanner(System.in);

    // Constructor to initialize the controller and load data from files
    public MedicalRecordsController() {
        this.patients = new ArrayList<>();
        this.medicalRecords = new ArrayList<>();

        // Load patient records
        try {
            loadPatientsFromFile(PATIENT_TXT);
        } catch (IOException e) {
            System.out.println("Error loading patients: " + e.getMessage());
        }

        // Load medical records
        try {
            loadMedicalRecordsFromFile(MEDICALRECORDS_TXT);
        } catch (IOException e) {
            System.out.println("Error loading medical records: " + e.getMessage());
        }
    }

    // Load patients from file
    private void loadPatientsFromFile(String filename) throws IOException {
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
    private void loadMedicalRecordsFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");

                if (data.length < 5) {
                    System.out.println("Skipping invalid medical record: " + line);
                    continue;
                }

                String medicalRecordId = data[0];
                String doctorId = data[1];
                String patientId = data[2];
                String diagnosis = data[3];
                String treatment = data[4];

                // Find the associated patient using patientId
                Patient patient = findPatientById(patientId);

                if (patient != null) {
                    MedicalRecord record = new MedicalRecord(medicalRecordId, doctorId, patientId, diagnosis, treatment);
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

    // Method to select a patient to perform CRUD on medical records
    private String selectPatient() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║            Select a Patient            ║");
        System.out.println("╚════════════════════════════════════════╝");
        for (Patient patient : patients) {
            System.out.println("══════════════════════════════════════════════════════");
            System.out.println(patient);  // Assuming Patient class has a meaningful toString() method
            System.out.println("══════════════════════════════════════════════════════");
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

    // Generate a new unique medical record ID
    private String generateMedicalRecordId() {
        if (medicalRecords.isEmpty()) {
            return "MR1"; // Start with MR1 if no records exist
        }

        // Get the last medical record ID
        String lastRecordId = medicalRecords.get(medicalRecords.size() - 1).getMedicalRecordId();

        // Extract the numeric part and increment it
        int lastIdNumber = Integer.parseInt(lastRecordId.substring(2));
        return String.format("%s%05d", "MR", lastIdNumber + 1);
    }

    // Method to create a medical record for a patient
    private void createMedicalRecord(String patientId, String doctorId) {
        if (findPatientById(patientId) == null) {
            System.out.println("Invalid patient ID.");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         Creating Medical Record        ║");
        System.out.println("╚════════════════════════════════════════╝");

        System.out.print("Enter diagnosis (or NIL if none): ");
        String diagnosisInput = scanner.nextLine().trim();
        String diagnosis = diagnosisInput.equalsIgnoreCase("NIL") ? null : diagnosisInput; // Set to null if NIL

        System.out.print("Enter treatment (or NIL if none): ");
        String treatment = scanner.nextLine().trim();
        if (treatment.equalsIgnoreCase("NIL")) {
            treatment = "";
        }
        String medicalRecordID = generateMedicalRecordId();

        MedicalRecord newRecord = new MedicalRecord(medicalRecordID, doctorId, patientId, diagnosis, treatment);
        medicalRecords.add(newRecord);
        System.out.println("Medical record created successfully for patient ID: " + patientId);

        // Write to file
        FileUtils.writeToFile(MEDICALRECORDS_TXT, medicalRecordID + '|' + doctorId + '|' + patientId + '|' + diagnosis + '|' + treatment);

        // Display success message
        System.out.println(patientId + " Created Medical record successfully!");
    }

    // Method to update a medical record for a patient
    private void updateMedicalRecord(String patientId, String doctorId) {
        if (findPatientById(patientId) == null) {
            System.out.println("Invalid patient ID.");
            return;
        }

        List<MedicalRecord> recordsToUpdate = new ArrayList<>();
        for (MedicalRecord record : medicalRecords) {
            if (record.getPatientId().equals(patientId) && record.getDoctorId().equals(doctorId)) {
                recordsToUpdate.add(record);
            }
        }

        if (recordsToUpdate.isEmpty()) {
            System.out.println("No medical records found for Patient ID: " + patientId);
            return;
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      Medical Records for Patient       ║");
        System.out.println("╚════════════════════════════════════════╝");
        int counter = 1;
        for (MedicalRecord record : recordsToUpdate) {
            System.out.println(counter + ".");
            System.out.println(record);
            counter++;
        }

        while (true) {
            System.out.println("Select a medical record to update (0 to return): ");
            int updateChoice = scanner.nextInt();
            if (updateChoice == 0) {
                return;
            }
            if (updateChoice > recordsToUpdate.size()) {
                System.out.println("Invalid choice. Please enter a valid selection");
                continue;
            }

            MedicalRecord recordToUpdate = recordsToUpdate.get(updateChoice - 1);

            System.out.print("Enter new diagnosis (or press Enter to keep current): ");
            scanner.nextLine(); // Consume newline
            String diagnosisInput = scanner.nextLine().trim();
            if (!diagnosisInput.isEmpty()) {
                String diagnosis = diagnosisInput.equalsIgnoreCase("NIL") ? "-" : diagnosisInput; // Set to null if NIL
                recordToUpdate.setDiagnosis(diagnosis);
            }

            System.out.print("Enter new treatment (or press Enter to keep current): ");
            String treatmentInput = scanner.nextLine().trim();
            if (!treatmentInput.isEmpty()) {
                recordToUpdate.setTreatment(treatmentInput.equalsIgnoreCase("NIL") ? "-" : treatmentInput);
            }

            System.out.println("Medical record updated successfully for Patient ID: " + patientId);

            // Write updated record to file
            FileUtils.updateToFile(MEDICALRECORDS_TXT, recordToUpdate.getMedicalRecordId() + '|' + doctorId + '|' + patientId + '|' + recordToUpdate.getDiagnosis() + '|' + recordToUpdate.getTreatment(), recordToUpdate.getMedicalRecordId());
            return;
        }
    }

    // Method to delete a medical record for a patient
    private void deleteMedicalRecord(String patientId, String doctorId) {
        if (findPatientById(patientId) == null) {
            System.out.println("Invalid patient ID.");
            return;
        }

        List<MedicalRecord> recordsToDelete = new ArrayList<>();
        for (MedicalRecord record : medicalRecords) {
            if (record.getPatientId().equals(patientId) && record.getDoctorId().equals(doctorId)) {
                recordsToDelete.add(record);
            }
        }

        if (recordsToDelete.isEmpty()) {
            System.out.println("No medical records found for Patient ID: " + patientId);
            return;
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      Medical Records for Patient       ║");
        System.out.println("╚════════════════════════════════════════╝");
        int counter = 1;
        for (MedicalRecord record : recordsToDelete) {
            System.out.println(counter + ".");
            System.out.println(record);
            counter++;
        }

        while (true) {
            System.out.println("Select medical record to delete (0 to return): ");
            int medicalRecordChoice = scanner.nextInt();
            if (medicalRecordChoice == 0) {
                return;
            }
            if (medicalRecordChoice > recordsToDelete.size()) {
                System.out.println("Invalid choice. Please enter a valid selection");
                continue;
            }

            MedicalRecord recordToDelete = recordsToDelete.get(medicalRecordChoice - 1);

            System.out.print("Are you sure you want to delete this medical record? (yes/no): ");
            scanner.nextLine(); // Consume newline
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                FileUtils.deleteFromFile(MEDICALRECORDS_TXT, recordToDelete.getMedicalRecordId());
                medicalRecords.remove(recordToDelete);  // Remove the selected medical record from the list
                System.out.println("Medical record for Patient ID: " + patientId + " has been deleted successfully.");
            } else {
                System.out.println("Deletion canceled for Patient ID: " + patientId);
            }
            return;
        }
    }

    // Method to display medical records for a given patient ID
    public void displayMedicalRecords(String patientId) {
        Patient patient = findPatientById(patientId);

        // Print patient's information
        if (patient != null) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║           Patient Information          ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println(patient);
        } else {
            System.out.println("Patient with ID " + patientId + " not found.");
            return;
        }

        // Display medical records for the patient
        boolean recordFound = false;
          System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║             Medical Records            ║");
            System.out.println("╚════════════════════════════════════════╝");
        

        for (MedicalRecord record : medicalRecords) {
            if (record.getPatientId().equals(patientId)) {
                System.out.println("Diagnosis: " + record.getDiagnosis());
                System.out.println("Treatment: " + record.getTreatment());
                System.out.println("══════════════════════════════════════");
                recordFound = true;
            }
        }

        if (!recordFound) {
            System.out.println("No medical records found for patient ID: " + patientId);
        }

        PrintUtils.pause();
    }

    // Print the list of patients
    private void printPatients() {
        int i = 1;

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              Patient List              ║");
        System.out.println("╚════════════════════════════════════════╝");
        for (Patient patient : patients) {
            System.out.printf("%d. %s %s\n", i, patient.getFirstName(), patient.getLastName());
            i++;
        }
    }

    // Display medical records for a selected patient
    private void displayMedicalRecords() {
        printPatients();
        while (true) {
            System.out.println("Select a patient (0 to return): ");
            int patientChoice = scanner.nextInt();
            if (patientChoice == 0) {
                return;
            }
            if (patientChoice > patients.size()) {
                System.out.println("Invalid choice. Please enter a valid selection");
                continue;
            }
            Patient patient = patients.get(patientChoice - 1);
            displayMedicalRecords(patient.getUserId());
            return;
        }
    }

    // Create a medical record for a selected patient
    private void createMedicalRecord(String doctorId) {
        printPatients();
        while (true) {
            System.out.println("Select a patient (0 to return): ");
            int patientChoice = scanner.nextInt();
            if (patientChoice == 0) {
                return;
            }
            if (patientChoice > patients.size()) {
                System.out.println("Invalid choice. Please enter a valid selection");
                continue;
            }
            Patient patient = patients.get(patientChoice - 1);
            createMedicalRecord(patient.getUserId(), doctorId);
            return;
        }
    }

    // Update a medical record for a selected patient
    private void updateMedicalRecord(String doctorId) {
        printPatients();
        while (true) {
            System.out.println("Select a patient (0 to return): ");
            int patientChoice = scanner.nextInt();
            if (patientChoice == 0) {
                return;
            }
            if (patientChoice > patients.size()) {
                System.out.println("Invalid choice. Please enter a valid selection");
                continue;
            }
            Patient patient = patients.get(patientChoice - 1);
            updateMedicalRecord(patient.getUserId(), doctorId);
            return;
        }
    }

    // Delete a medical record for a selected patient
    private void deleteMedicalRecord(String doctorId) {
        printPatients();
        while (true) {
            System.out.println("Select a patient (0 to return): ");
            int patientChoice = scanner.nextInt();
            if (patientChoice == 0) {
                return;
            }
            if (patientChoice > patients.size()) {
                System.out.println("Invalid choice. Please enter a valid selection");
                continue;
            }
            Patient patient = patients.get(patientChoice - 1);
            deleteMedicalRecord(patient.getUserId(), doctorId);
            return;
        }
    }

    // Method to view all medical records with associated patient information
    public void viewMedicalRecords(User user) {
        if (user instanceof Patient) {
            displayMedicalRecords(user.getUserId());
        } else if (user instanceof Doctor) {
            while (true) {
                System.out.println("Select an option:");
                System.out.println("1: View medical record");
                System.out.println("2: Create medical record");
                System.out.println("3: Update medical record");
                System.out.println("4: Delete medical record");
                System.out.println("0: Return to menu");
                System.out.print("Your choice: ");
                String option = scanner.nextLine().trim();

                switch (option) {
                    case "1":
                        displayMedicalRecords();
                        break;
                    case "2":
                        createMedicalRecord(user.getUserId());
                        break;
                    case "3":
                        updateMedicalRecord(user.getUserId());
                        break;
                    case "4":
                        deleteMedicalRecord(user.getUserId());
                        break;
                    case "0":
                        System.out.println("Returning to menu...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        continue;  // Restart the loop if the choice is invalid
                }
            }
        }
    }
}
