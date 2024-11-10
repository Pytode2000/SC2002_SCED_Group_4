package controller;

import entity.AppointmentOutcome;
import entity.Prescription;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import utility.FileUtils;
import utility.PrintUtils;
//testZL

public class AppointmentOutcomeController {

    private static final String APPOINTMENT_OUTCOME_FILE = "data/appointmentOutcome.txt";
    private static final String STAFF_FILE = "data/staff.txt";
    private static final String APPOINTMENT_FILE = "data/appointment.txt";
    private static final String PATIENT_FILE = "data/patient.txt";
    private static final String MEDICINE_FILE = "data/medicine.txt";

    private static final String PRESCRIPTION_FILE = "data/prescription.txt";

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    //testZL
    public void displayAllPendingAppointmentOutcomes() {

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       Appointment Outcome Records      ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("\n══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");

        try {
            // Step 1: Load all pending prescription IDs
            Set<String> pendingPrescriptionIds = getPendingPrescriptionIds();

            // Step 2: Read and filter appointment outcomes with pending prescriptions
            BufferedReader br = new BufferedReader(new FileReader(APPOINTMENT_OUTCOME_FILE));
            String line;
            List<String[]> pendingAppointments = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                String appointmentId = data[0].trim();
                String patientId = data[1].trim();
                String doctorId = data[2].trim();
                String date = data[3].trim();
                String serviceType = data[4].trim();
                String prescriptionIds = data[5].trim();
                String consultationNotes = data[6].trim();

                // Check if any of the prescription IDs in this appointment are pending
                String[] prescriptionIdArray = prescriptionIds.split(";");
                List<String> medications = new ArrayList<>();
                boolean hasPendingPrescription = false;

                for (String prescriptionId : prescriptionIdArray) {
                    if (pendingPrescriptionIds.contains(prescriptionId.trim())) {
                        hasPendingPrescription = true;
                    }
                    // Get full prescription details
                    Prescription prescription = getPrescriptionDetails(prescriptionId.trim());
                    if (prescription != null) {
                        String medicineName = getMedicineName(prescription.getMedicineId());
                        medications.add(String.format("%dx %s (%s)", prescription.getQuantity(), medicineName, prescription.getStatus()));
                    }
                }

                // Add to list if it has at least one pending prescription
                if (hasPendingPrescription) {
                    pendingAppointments.add(new String[]{
                        appointmentId,
                        date,
                        getDoctorName(doctorId),
                        serviceType,
                        medications.isEmpty() ? "- No Prescription" : String.join("\n", medications),
                        consultationNotes.isEmpty() ? "-" : consultationNotes
                    });
                }
            }
            br.close();

// Step 3: Display the results in table format
            if (pendingAppointments.isEmpty()) {
                System.out.println("No appointment outcomes with pending prescriptions found.");
            } else {
                // Adjust column widths to make Service Type wider by 1.5x (adding space)
                System.out.printf("%-15s | %-12s | %-18s | %-20s | %-40s | %-40s |%n", // Adjusted Service Type width
                        "Appointment ID", "Date", "Doctor", "Service Type", "Medications", "Consultation Notes");
                System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------");

                for (String[] appointment : pendingAppointments) {
                    // Print the first line with all data, including consultation notes
                    System.out.printf("%-15s | %-12s | %-18s | %-20s | %-40s | %-40s |%n", // Adjusted Service Type width
                            appointment[0], appointment[1], appointment[2], appointment[3], appointment[4].split("\n")[0], appointment[5]);

                    // Print additional lines for each medication if there are multiple
                    String[] medications = appointment[4].split("\n");
                    for (int i = 1; i < medications.length; i++) {
                        System.out.printf("%-15s | %-12s | %-18s | %-20s | %-40s | %-40s |%n", // Adjusted Service Type width
                                "", "", "", "", medications[i], "");
                    }
                    System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading files: " + e.getMessage());
        }
        PrintUtils.pause();
    }

    // Helper method to retrieve prescription details
    private Prescription getPrescriptionDetails(String prescriptionId) {
        try (BufferedReader br = new BufferedReader(new FileReader(PRESCRIPTION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
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

    // Helper method to retrieve all pending prescription IDs from prescription file
    private Set<String> getPendingPrescriptionIds() {
        Set<String> pendingPrescriptionIds = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PRESCRIPTION_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\\|");
                String prescriptionId = fields[0].trim();
                String status = fields[3].trim();

                if (status.equalsIgnoreCase("PENDING")) {
                    pendingPrescriptionIds.add(prescriptionId);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading prescription file: " + e.getMessage());
        }
        return pendingPrescriptionIds;
    }
    //testZL

    // Display appointment outcomes by patient ID with full prescription details in
    // a table format
    public void displayAppointmentOutcomesByPatientId(String patientId) {
        List<AppointmentOutcome> outcomes = getAppointmentOutcomesByPatientId(patientId);

        if (outcomes.isEmpty()) {
            System.out.println("No appointment outcomes found for patient ID: " + patientId);
        } else {
            // Sort outcomes by date of appointment in ascending order
            outcomes.sort(Comparator.comparing(AppointmentOutcome::getDateOfAppointment));

            // Print table headers
            System.out.println("\nAppointment Outcomes for Patient ID: " + patientId + "\n");
            System.out.printf("%-15s | %-20s | %-20s | %-50s | %-51s |%n",
                    "Date", "Doctor", "Service Type",
                    "Medications", "Consultation Notes");
            System.out.println(
                    "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

            // Display each appointment outcome in a table row format
            for (AppointmentOutcome outcome : outcomes) {
                // Retrieve doctor name using doctorId
                String doctorName = getDoctorName(outcome.getDoctorId());

                // Retrieve each prescribed medication with quantity and status
                List<Prescription> prescriptions = getPrescriptionsByIds(outcome.getPrescribedMedications());
                if (prescriptions.isEmpty()) {
                    // No prescriptions, display "No Prescription" on the first row
                    System.out.printf("%-15s | %-20s | %-20s | %-50s | %-51s |%n",
                            outcome.getDateOfAppointment().format(dateFormatter),
                            doctorName,
                            outcome.getServiceType(),
                            "- No Prescription",
                            outcome.getConsultationNotes()); // Consultation notes at the top
                } else {
                    // Print the first row with the first prescription
                    Prescription firstPrescription = prescriptions.get(0);
                    String medicineName = getMedicineName(firstPrescription.getMedicineId());
                    System.out.printf("%-15s | %-20s | %-20s | %-50s | %-51s |%n",
                            outcome.getDateOfAppointment().format(dateFormatter),
                            doctorName,
                            outcome.getServiceType(),
                            String.format("1. %dx %s (%s)", firstPrescription.getQuantity(), medicineName,
                                    firstPrescription.getStatus()),
                            outcome.getConsultationNotes()); // Consultation notes at the top

                    // Print subsequent rows for additional prescriptions, if any
                    for (int i = 1; i < prescriptions.size(); i++) {
                        Prescription prescription = prescriptions.get(i);
                        medicineName = getMedicineName(prescription.getMedicineId());
                        System.out.printf("%-15s | %-20s | %-20s | %-50s | %-51s |%n",
                                "", "", "",
                                String.format("%d. %dx %s (%s)", i + 1, prescription.getQuantity(), medicineName,
                                        prescription.getStatus()),
                                ""); // Empty Consultation Notes column here
                    }
                }

                System.out.println(
                        "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            }
        }
        PrintUtils.pause();
    }

    // Retrieve a list of Prescription objects by prescription IDs
    private List<Prescription> getPrescriptionsByIds(List<String> prescriptionIds) {
        List<Prescription> prescriptions = new ArrayList<>();

        try (BufferedReader prescriptionReader = new BufferedReader(new FileReader(PRESCRIPTION_FILE))) {
            String prescriptionLine;
            while ((prescriptionLine = prescriptionReader.readLine()) != null) {
                String[] fields = prescriptionLine.split("\\|");
                if (fields.length >= 4 && prescriptionIds.contains(fields[0])) {
                    String prescriptionId = fields[0];
                    String medicineId = fields[1];
                    int quantity = Integer.parseInt(fields[2]);
                    Prescription.Status status = Prescription.Status.valueOf(fields[3].toUpperCase());

                    prescriptions.add(new Prescription(prescriptionId, medicineId, quantity, status));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading prescription file: " + e.getMessage());
        }
        return prescriptions;
    }

    // Retrieve medicine name by medicineId from medicine file
    private String getMedicineName(String medicineId) {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/medicine.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].equals(medicineId)) {
                    return fields[1]; // Return the medicine name (assumed to be at index 1)
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading medicine file: " + e.getMessage());
        }
        return "Unknown Medicine";
    }

    // Retrieve appointment outcomes by patient ID from file
    public List<AppointmentOutcome> getAppointmentOutcomesByPatientId(String patientId) {
        List<AppointmentOutcome> outcomes = new ArrayList<>();

        try (BufferedReader outcomeReader = new BufferedReader(new FileReader(APPOINTMENT_OUTCOME_FILE))) {
            String outcomeLine;
            while ((outcomeLine = outcomeReader.readLine()) != null) {
                String[] outcomeFields = outcomeLine.split("\\|");
                if (outcomeFields.length >= 7 && outcomeFields[1].equals(patientId)) {
                    LocalDate dateOfAppointment = LocalDate.parse(outcomeFields[3], dateFormatter);

                    // Parse the prescription IDs from the outcomeFields[5], which is
                    // semicolon-separated
                    List<String> prescriptionIds = parsePrescriptionIds(outcomeFields[5]);

                    // Create appointment outcome with prescription IDs
                    AppointmentOutcome outcome = new AppointmentOutcome(
                            outcomeFields[0], // appointmentId
                            outcomeFields[1], // patientId
                            outcomeFields[2], // doctorId
                            dateOfAppointment,
                            outcomeFields[4], // serviceType
                            prescriptionIds, // List of prescription IDs
                            outcomeFields[6] // consultationNotes
                    );
                    outcomes.add(outcome);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading appointment outcome file: " + e.getMessage());
        }
        return outcomes;
    }

    // Helper method to parse prescription IDs from a semicolon-separated string
    private List<String> parsePrescriptionIds(String prescriptionField) {
        List<String> prescriptionIds = new ArrayList<>();
        if (prescriptionField.equals("-")) {
            return prescriptionIds;
        }
        if (prescriptionField != null && !prescriptionField.isEmpty()) {
            String[] idsArray = prescriptionField.split(";");
            for (String id : idsArray) {
                prescriptionIds.add(id.trim()); // Add each prescription ID to the list
            }
        }
        return prescriptionIds;
    }

    // Helper method to retrieve doctor name from staff file
    private String getDoctorName(String doctorId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(STAFF_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].equals(doctorId)) {
                    return fields[1] + " " + fields[2]; // Concatenates first and last name
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading staff file: " + e.getMessage());
        }
        return "Doctor not found.";
    }

    // Print out menu for Doctor
    private List<String> getUpcomingAppointments(String doctorId) {

        List<String> upcomingAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length >= 6 && fields[5].equals("BOOKED") && fields[1].equals(doctorId)) {
                    upcomingAppointments.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (upcomingAppointments.isEmpty()) {
            return null;
        } else {
            return upcomingAppointments;
        }

    }

    private List<String> getPatientList() {

        List<String> patientList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PATIENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length > 8) {
                    patientList.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (patientList.isEmpty()) {
            return null;
        } else {
            return patientList;
        }
    }

    public Map<String, String> getPatientMap(List<String> patients) {
        Map<String, String> patientMap = new HashMap<>();

        for (String patient : patients) {
            String[] fields = patient.split("\\|");
            if (fields.length >= 3) { // Ensure at least ID, first name, and last name are available
                String patientId = fields[0];
                String fullName = fields[1] + " " + fields[2]; // Combine first and last name
                patientMap.put(patientId, fullName);
            }
        }
        return patientMap;
    }

    public List<String> formatAppointments(List<String> appointments, Map<String, String> patientMap) {
        List<String> formattedAppointments = new ArrayList<>();

        for (String appointment : appointments) {
            String[] fields = appointment.split("\\|");
            if (fields.length >= 3) { // Ensure that the patient ID field exists
                String patientId = fields[2];

                // Replace patient ID with full name if available
                if (patientMap.containsKey(patientId)) {
                    fields[2] = patientMap.get(patientId); // Replace patient ID with name
                }

                // Reconstruct the appointment entry as a formatted string
                formattedAppointments.add(String.join("|", fields));
            }
        }
        return formattedAppointments;
    }

    private void viewUpcomingAppointments(String doctorId) {

        List<String> upcomingAppointments = getUpcomingAppointments(doctorId);

        List<String> patientList = getPatientList();

        Map<String, String> patientMap = getPatientMap(patientList);

        List<String> formattedAppointments = formatAppointments(upcomingAppointments, patientMap);

        for (String appointment : formattedAppointments) {
            System.out.println(appointment);
        }
    }

    private List<String[]> loadMedicines() {
        List<String[]> medicines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                medicines.add(line.split("\\|"));
            }
        } catch (IOException e) {
            System.out.println("Error loading medicines: " + e.getMessage());
        }
        return medicines;
    }

    private void createAppointmentOutcome(String doctorId) {
        List<String> upcomingAppointments = getUpcomingAppointments(doctorId);
        if (upcomingAppointments == null || upcomingAppointments.isEmpty()) {
            System.out.println("No upcoming appointments available.");
            return;
        }

        // Sort appointments by date and time
        upcomingAppointments.sort(Comparator.comparing(this::extractDateTimeFromAppointment));

        // Display sorted upcoming appointments with index starting from 1
        System.out.println("\nUpcoming Appointments:");
        System.out.println("-------------------------------------------");

        List<String[]> appointmentDetailsList = new ArrayList<>();

        for (int i = 0; i < upcomingAppointments.size(); i++) {
            String[] fields = upcomingAppointments.get(i).split("\\|");
            String patientId = fields[2];
            String patientName = getPatientNameById(patientId);
            String date = fields[3];
            String time = fields[4];
            appointmentDetailsList.add(new String[]{fields[0], patientId, date, time});
            System.out.printf("%d. %s (%s %s)\n", i + 1, patientName, date, time);
        }

        Scanner scanner = new Scanner(System.in);
        int selectedIndex;

        // Get the last ID once and keep incrementing in memory
        String currentPrescriptionId = getLastPrescriptionId();

        // Prompt user to select an appointment by index
        while (true) {
            System.out.print("\nEnter the index of the appointment to create a record, or press 0 to return: ");
            if (scanner.hasNextInt()) {
                selectedIndex = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (selectedIndex == 0) {
                    System.out.println("Returning to previous menu.");
                    return;
                } else if (selectedIndex > 0 && selectedIndex <= appointmentDetailsList.size()) {
                    String[] selectedAppointment = appointmentDetailsList.get(selectedIndex - 1);
                    String appointmentId = selectedAppointment[0];
                    String patientId = selectedAppointment[1];
                    String dateOfAppointment = selectedAppointment[2];
                    String timeOfAppointment = selectedAppointment[3];

                    List<String> pendingPrescriptions = new ArrayList<>();
                    List<String> prescriptionIds = new ArrayList<>();

                    while (true) {
                        List<String[]> medicines = loadMedicines();

                        System.out.println("\nAvailable Medicines:");
                        for (int i = 0; i < medicines.size(); i++) {
                            System.out.printf("%d. %s (%s)\n", i + 1, medicines.get(i)[1], medicines.get(i)[2]);
                        }
                        System.out.print("Enter the index of the medicine to prescribe, or 0 to finish: ");
                        int medicineIndex = scanner.nextInt();
                        scanner.nextLine();

                        if (medicineIndex == 0) {
                            break;
                        } else if (medicineIndex > 0 && medicineIndex <= medicines.size()) {
                            String[] selectedMedicine = medicines.get(medicineIndex - 1);
                            String medicineId = selectedMedicine[0];
                            System.out.print("Enter quantity: ");
                            int quantity = scanner.nextInt();
                            scanner.nextLine();

                            // Generate a unique prescription ID by incrementing in memory
                            currentPrescriptionId = incrementPrescriptionId(currentPrescriptionId);
                            prescriptionIds.add(currentPrescriptionId);

                            String prescriptionEntry = String.format("%s|%s|%d|PENDING", currentPrescriptionId, medicineId, quantity);
                            pendingPrescriptions.add(prescriptionEntry);
                        } else {
                            System.out.println("Invalid index. Please select a valid medicine index.");
                        }
                    }

                    String prescribedMedicine = prescriptionIds.isEmpty() ? "-" : String.join(",", prescriptionIds);

                    String[] additionalDetails = promptForAppointmentDetails();
                    String typeOfService = additionalDetails[0].isEmpty() ? "-" : additionalDetails[0];
                    String consultationNotes = additionalDetails[1].isEmpty() ? "-" : additionalDetails[1];

                    for (String prescriptionEntry : pendingPrescriptions) {
                        FileUtils.writeToFile(PRESCRIPTION_FILE, prescriptionEntry);
                    }

                    String appointmentRecord = String.format("%s|%s|%s|%s|%s|%s|%s",
                            appointmentId, patientId, doctorId, dateOfAppointment, typeOfService,
                            prescribedMedicine, consultationNotes);
                    FileUtils.writeToFile(APPOINTMENT_OUTCOME_FILE, appointmentRecord);

                    FileUtils.updateEntry(APPOINTMENT_FILE, appointmentId, "CLOSED", 5);
                    System.out.println("\nAppointment outcome record created successfully.");
                    return;
                } else {
                    System.out.println("Invalid index. Please select a valid appointment index.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume invalid input
            }
        }
    }

    // Helper to get the last ID from the prescription file
    private String getLastPrescriptionId() {
        String lastId = "PR00000";
        try (BufferedReader reader = new BufferedReader(new FileReader(PRESCRIPTION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lastId = line.split("\\|")[0];
            }
        } catch (IOException e) {
            System.out.println("Error reading prescription file: " + e.getMessage());
        }
        return lastId;
    }

    // Increment function for prescription ID in memory
    private String incrementPrescriptionId(String lastId) {
        int lastNum = Integer.parseInt(lastId.substring(2));
        return String.format("PR%05d", lastNum + 1);
    }

// Extract date and time for sorting appointments
    private LocalDateTime extractDateTimeFromAppointment(String appointment) {
        String[] fields = appointment.split("\\|");
        String dateTimeString = fields[3] + " " + fields[4]; // Combine date and time fields
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    // Method to select medicines and create prescription entries
    private String selectMedicinesForPrescription(Scanner scanner) {
        List<String> selectedMedicineIds = new ArrayList<>();
        List<String[]> medicineList = loadMedicinesFromFile();
        int medicineIndex;

        System.out.println("\nAvailable Medicines:");
        for (int i = 0; i < medicineList.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, medicineList.get(i)[1]); // Display medicine names
        }

        // Keep prompting the doctor to select medicines
        while (true) {
            System.out.print("Enter the index of the medicine to add (or press 0 to finish): ");
            if (scanner.hasNextInt()) {
                medicineIndex = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (medicineIndex == 0) {
                    break; // Exit if doctor finishes adding medicines
                } else if (medicineIndex > 0 && medicineIndex <= medicineList.size()) {
                    String[] selectedMedicine = medicineList.get(medicineIndex - 1);
                    String medicineId = selectedMedicine[0];

                    System.out.print("Enter quantity for " + selectedMedicine[1] + ": ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    // Generate a new prescription ID
                    String prescriptionId = generatePrescriptionId();

                    // Write prescription to file
                    String prescriptionRecord = String.format("%s|%s|%d|PENDING",
                            prescriptionId, medicineId, quantity);
                    FileUtils.writeToFile(PRESCRIPTION_FILE, prescriptionRecord);

                    // Add prescription ID to the list
                    selectedMedicineIds.add(prescriptionId);
                    System.out.println("Added " + selectedMedicine[1] + " with quantity " + quantity);
                } else {
                    System.out.println("Invalid index. Please select a valid medicine index.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume invalid input
            }
        }

        // Join all prescription IDs with commas for the appointment outcome
        return String.join(",", selectedMedicineIds);
    }

    // Load medicines from medicine.txt
    private List<String[]> loadMedicinesFromFile() {
        List<String[]> medicines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                medicines.add(data);
            }
        } catch (IOException e) {
            System.out.println("Error loading medicines: " + e.getMessage());
        }
        return medicines;
    }

    // Generate a unique prescription ID
    private String generatePrescriptionId() {
        String lastId = "PR00000"; // Default ID if no entries are found

        try (BufferedReader reader = new BufferedReader(new FileReader("data/prescription.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lastId = line.split("\\|")[0]; // Get the ID from each line
            }
        } catch (IOException e) {
            System.out.println("Error reading prescription file: " + e.getMessage());
        }

        // Extract the numeric part of the last ID and increment it
        int lastNum = Integer.parseInt(lastId.substring(2));
        String newId = String.format("PR%05d", lastNum + 1); // Generate new ID with leading zeros

        return newId;
    }

    // Helper method to retrieve patient name by ID
    private String getPatientNameById(String patientId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PATIENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].equals(patientId)) {
                    return fields[1] + " " + fields[2]; // Assuming first name at index 1, last name at index 2
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading patient file: " + e.getMessage());
        }
        return "Unknown Patient";
    }

    private String promptForAppointmentId(List<String> upcomingAppointments) {
        Scanner scanner = new Scanner(System.in);
        String inputAppointmentId;

        while (true) {
            System.out.println("\nEnter the Appointment ID to create a record, or press 0 to return:");
            inputAppointmentId = scanner.nextLine().trim();

            if (inputAppointmentId.equals("0")) {
                return "0";
            }

            if (checkAppointmentId(inputAppointmentId, upcomingAppointments)) {
                System.out.println("Valid Appointment ID selected.");
                return inputAppointmentId;
            } else {
                System.out.println("Invalid Appointment ID. Please try again.");
            }
        }
    }

    // Retrieves patientId and dateOfAppointment for a specific appointmentId
    private String[] getAppointmentDetailsById(List<String> appointments, String appointmentId) {
        for (String appointment : appointments) {
            String[] fields = appointment.split("\\|");
            if (fields[0].equals(appointmentId)) {
                return new String[]{fields[2], fields[3]}; // {patientId, dateOfAppointment}
            }
        }
        return null;
    }

    // Prompts user for additional appointment details
    private String[] promptForAppointmentDetails() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter type of service:");
        String typeOfService = scanner.nextLine().trim();

        System.out.println("Enter consultation notes:");
        String consultationNotes = scanner.nextLine().trim();

        return new String[]{typeOfService, consultationNotes};
    }

    // Checks if the given appointmentId exists in the list of upcoming appointments
    private boolean checkAppointmentId(String appointmentId, List<String> appointments) {
        for (String appointment : appointments) {
            String[] fields = appointment.split("\\|");
            if (fields[0].equals(appointmentId)) {
                return true; // Appointment ID found
            }
        }
        return false; // Appointment ID not found
    }

    public void viewDoctorMenu(String doctorId) {

        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("\nPlease select an option:");
            System.out.println("1. Create appointment outcome record");
            System.out.println("2. Edit appointment outcome record");
            System.out.println("0. Return");

            // Check for valid integer input
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                switch (choice) {
                    case 1:
                        System.out.println("Creating appointment outcome record...");
                        // Call the method to create an appointment outcome record
                        createAppointmentOutcome(doctorId);
                        PrintUtils.pause();
                        break;

                    case 2:
                        System.out.println("Editing appointment outcome record...");
                        updateAppointmentOutcomeRecord(doctorId);
                        PrintUtils.pause();

                        break;

                    case 0:
                        System.out.println("Returning to previous menu.");
                        return; // Exit the loop and method

                    default:
                        System.out.println("Invalid choice. Please select 1, 2, or 0.");
                }
            } else {

                System.out.println("Invalid input. Please enter a number (0, 1, or 2).");
                scanner.next(); // Consume invalid input
            }
        }
    }

    // Method to update an appointment outcome record
    private void updateAppointmentOutcomeRecord(String doctorId) {
        List<AppointmentOutcome> outcomes = getAppointmentOutcomesByDoctorId(doctorId);

        // Sort outcomes by date
        outcomes.sort(Comparator.comparing(AppointmentOutcome::getDateOfAppointment));

        // Display sorted outcomes with index
        System.out.println("\nAppointment Outcome Records:");
        System.out.println("-------------------------------------------");
        for (int i = 0; i < outcomes.size(); i++) {
            AppointmentOutcome outcome = outcomes.get(i);
            System.out.printf("%d. Appointment ID: %s | Date: %s | Service Type: %s | Notes: %s\n",
                    i + 1, outcome.getAppointmentId(),
                    outcome.getDateOfAppointment().format(dateFormatter),
                    outcome.getServiceType(),
                    outcome.getConsultationNotes());
        }

        Scanner scanner = new Scanner(System.in);
        int selectedIndex;

        // Prompt user to select a record to edit
        while (true) {
            System.out.print("\nWould you like to edit an Appointment Outcome Record? Enter index (0 to exit): ");
            if (scanner.hasNextInt()) {
                selectedIndex = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (selectedIndex == 0) {
                    System.out.println("Exiting update menu.");
                    return;
                } else if (selectedIndex > 0 && selectedIndex <= outcomes.size()) {
                    AppointmentOutcome selectedOutcome = outcomes.get(selectedIndex - 1);

                    // Prompt for new service type and consultation notes
                    System.out.print("Enter new Service Type (leave blank to keep current): ");
                    String serviceType = scanner.nextLine().trim();
                    if (serviceType.isEmpty()) {
                        serviceType = selectedOutcome.getServiceType();
                    }

                    System.out.print("Enter new Consultation Notes (leave blank to keep current): ");
                    String consultationNotes = scanner.nextLine().trim();
                    if (consultationNotes.isEmpty()) {
                        consultationNotes = selectedOutcome.getConsultationNotes();
                    }

                    // Update the outcome and save changes
                    selectedOutcome.setServiceType(serviceType.equals("") ? "-" : serviceType);
                    selectedOutcome.setConsultationNotes(consultationNotes.equals("") ? "-" : consultationNotes);

                    // Write updated data to file
                    updateAppointmentOutcomeInFile(selectedOutcome);
                    System.out.println("Appointment Outcome Record updated successfully.");
                    return;
                } else {
                    System.out.println("Invalid index. Please select a valid record index.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume invalid input
            }
        }
    }

// Helper method to get appointment outcomes by doctor ID
    private List<AppointmentOutcome> getAppointmentOutcomesByDoctorId(String doctorId) {
        List<AppointmentOutcome> outcomes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_OUTCOME_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[2].equals(doctorId)) { // Match the doctor ID
                    LocalDate dateOfAppointment = LocalDate.parse(fields[3], dateFormatter);
                    List<String> prescriptionIds = parsePrescriptionIds(fields[5]);
                    AppointmentOutcome outcome = new AppointmentOutcome(
                            fields[0], fields[1], doctorId, dateOfAppointment,
                            fields[4], prescriptionIds, fields[6]);
                    outcomes.add(outcome);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading appointment outcomes: " + e.getMessage());
        }
        return outcomes;
    }

// Helper method to update a single appointment outcome in file
    private void updateAppointmentOutcomeInFile(AppointmentOutcome outcome) {
        List<String> fileContents = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_OUTCOME_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields[0].equals(outcome.getAppointmentId())) {
                    // Update the relevant line with new details
                    line = String.join("|",
                            outcome.getAppointmentId(), outcome.getPatientId(), outcome.getDoctorId(),
                            outcome.getDateOfAppointment().format(dateFormatter),
                            outcome.getServiceType(), String.join(",", outcome.getPrescribedMedications()),
                            outcome.getConsultationNotes());
                }
                fileContents.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading appointment outcome file: " + e.getMessage());
            return;
        }

        // Write updated contents back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPOINTMENT_OUTCOME_FILE))) {
            for (String contentLine : fileContents) {
                writer.write(contentLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing to appointment outcome file: " + e.getMessage());
        }
    }

}
