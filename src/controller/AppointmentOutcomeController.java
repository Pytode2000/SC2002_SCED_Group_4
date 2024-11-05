package controller;

import entity.AppointmentOutcome;
import entity.Prescription;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import utility.FileUtils;
import utility.PrintUtils;

public class AppointmentOutcomeController {

    private static final String APPOINTMENT_OUTCOME_FILE = "data/appointmentOutcome.txt";
    private static final String STAFF_FILE = "data/staff.txt";
    private static final String APPOINTMENT_FILE = "data/appointment.txt";
    private static final String PATIENT_FILE = "data/patient.txt";
    private static final String MEDICINE_FILE = "data/medicine.txt";

    private static final String PRESCRIPTION_FILE = "data/prescription.txt";

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Display appointment outcomes by patient ID with full prescription details in a table format
    public void displayAppointmentOutcomesByPatientId(String patientId) {
        List<AppointmentOutcome> outcomes = getAppointmentOutcomesByPatientId(patientId);

        if (outcomes.isEmpty()) {
            System.out.println("No appointment outcomes found for patient ID: " + patientId);
        } else {
            // Sort outcomes by date of appointment in ascending order
            outcomes.sort(Comparator.comparing(AppointmentOutcome::getDateOfAppointment));

            // Print table headers
            System.out.println("\nAppointment Outcomes for Patient ID: " + patientId + "\n");
            System.out.printf("%-15s | %-20s | %-20s | %-50s | %-30s |%n",
                    "Date", "Doctor", "Service Type",
                    "Medications", "Consultation Notes");
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");

            // Display each appointment outcome in a table row format
            for (AppointmentOutcome outcome : outcomes) {
                // Retrieve doctor name using doctorId
                String doctorName = getDoctorName(outcome.getDoctorId());

                // Retrieve each prescribed medication with quantity and status
                List<Prescription> prescriptions = getPrescriptionsByIds(outcome.getPrescribedMedications());
                if (prescriptions.isEmpty()) {
                    // No prescriptions, display "No Prescription" on the first row
                    System.out.printf("%-15s | %-20s | %-20s | %-50s | %-30s |%n",
                            outcome.getDateOfAppointment().format(dateFormatter),
                            doctorName,
                            outcome.getServiceType(),
                            "- No Prescription",
                            outcome.getConsultationNotes()); // Consultation notes at the top
                } else {
                    // Print the first row with the first prescription
                    Prescription firstPrescription = prescriptions.get(0);
                    String medicineName = getMedicineName(firstPrescription.getMedicineId());
                    System.out.printf("%-15s | %-20s | %-20s | %-50s | %-30s |%n",
                            outcome.getDateOfAppointment().format(dateFormatter),
                            doctorName,
                            outcome.getServiceType(),
                            String.format("1. %dx %s (%s)", firstPrescription.getQuantity(), medicineName, firstPrescription.getStatus()),
                            outcome.getConsultationNotes()); // Consultation notes at the top

                    // Print subsequent rows for additional prescriptions, if any
                    for (int i = 1; i < prescriptions.size(); i++) {
                        Prescription prescription = prescriptions.get(i);
                        medicineName = getMedicineName(prescription.getMedicineId());
                        System.out.printf("%-15s | %-20s | %-20s | %-50s | %-30s |%n",
                                "", "", "",
                                String.format("%d. %dx %s (%s)", i + 1, prescription.getQuantity(), medicineName, prescription.getStatus()),
                                ""); // Empty Consultation Notes column here
                    }
                }

                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
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

                    // Parse the prescription IDs from the outcomeFields[5], which is semicolon-separated
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
                prescriptionIds.add(id.trim());  // Add each prescription ID to the list
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

    //Print out menu for Doctor
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
            if (fields.length >= 3) {  // Ensure at least ID, first name, and last name are available
                String patientId = fields[0];
                String fullName = fields[1] + " " + fields[2];  // Combine first and last name
                patientMap.put(patientId, fullName);
            }
        }
        return patientMap;
    }

    public List<String> formatAppointments(List<String> appointments, Map<String, String> patientMap) {
        List<String> formattedAppointments = new ArrayList<>();

        for (String appointment : appointments) {
            String[] fields = appointment.split("\\|");
            if (fields.length >= 3) {  // Ensure that the patient ID field exists
                String patientId = fields[2];

                // Replace patient ID with full name if available
                if (patientMap.containsKey(patientId)) {
                    fields[2] = patientMap.get(patientId);  // Replace patient ID with name
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

    private void createAppointmentOutcome(String doctorId) {
        List<String> upcomingAppointments = getUpcomingAppointments(doctorId);
        if (upcomingAppointments.isEmpty()) {
            System.out.println("No upcoming appointments available.");
            return;
        }

        // Display upcoming appointments
        viewUpcomingAppointments(doctorId);

        String appointmentId = promptForAppointmentId(upcomingAppointments);
        if (appointmentId.equals("0")) {
            System.out.println("Returning to previous menu.");
            return;
        }

        String[] appointmentDetails = getAppointmentDetailsById(upcomingAppointments, appointmentId);
        if (appointmentDetails == null) {
            System.out.println("Appointment details not found. Exiting.");
            return;
        }

        String patientId = appointmentDetails[0];
        String dateOfAppointment = appointmentDetails[1];

        // Prompt for additional details
        String[] additionalDetails = promptForAppointmentDetails();
        String typeOfService = additionalDetails[0];
        String prescribedMedicine = additionalDetails[1];
        String consultationNotes = additionalDetails[2];

        // Construct the final record string
        String appointmentRecord = String.format("%s|%s|%s|%s|%s|%s|%s",
                appointmentId, patientId, doctorId, dateOfAppointment, typeOfService, prescribedMedicine, consultationNotes);

        FileUtils.writeToFile(APPOINTMENT_OUTCOME_FILE, appointmentRecord);
        System.out.println("\nAppointment outcome record created successfully.");

        FileUtils.updateEntry(APPOINTMENT_FILE, appointmentId, "CLOSED", 5);
        System.out.println("Closed appointment record.");

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

        System.out.println("Enter prescribed medicine (enter '-' if none):");
        String prescribedMedicine = scanner.nextLine().trim();

        System.out.println("Enter consultation notes:");
        String consultationNotes = scanner.nextLine().trim();

        return new String[]{typeOfService, prescribedMedicine, consultationNotes};
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
                scanner.nextLine();  // Consume newline character

                switch (choice) {
                    case 1:
                        System.out.println("Creating appointment outcome record...");
                        // Call the method to create an appointment outcome record
                        createAppointmentOutcome(doctorId);
                        break;

                    case 2:
                        System.out.println("Editing appointment outcome record...");
                        // Call the method to edit an appointment outcome record

                        break;

                    case 0:
                        System.out.println("Returning to previous menu.");
                        return;  // Exit the loop and method

                    default:
                        System.out.println("Invalid choice. Please select 1, 2, or 0.");
                }
            } else {

                System.out.println("Invalid input. Please enter a number (0, 1, or 2).");
                scanner.next();  // Consume invalid input
            }
        }
    }
}
