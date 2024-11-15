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

public class AppointmentOutcomeController {

    private static final String APPOINTMENT_OUTCOME_FILE = "data/appointmentOutcome.txt";
    private static final String STAFF_FILE = "data/staff.txt";
    private static final String APPOINTMENT_FILE = "data/appointment.txt";
    private static final String PATIENT_FILE = "data/patient.txt";
    private static final String MEDICINE_FILE = "data/medicine.txt";
    private static final String PRESCRIPTION_FILE = "data/prescription.txt";
    private static final String BILL_FILE = "data/bill.txt";

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Displays all appointment outcomes with at least one pending prescription.
     *
     * The method reads all appointment outcomes from the file and filters those
     * with
     * at least one pending prescription. It then displays the results in a table
     * format, with each row representing an appointment outcome and columns for
     * appointment ID, date, doctor, service type, medications, and consultation
     * notes. The medications column is formatted to show each medication on a new
     * line, indented 4 spaces. If there are no appointment outcomes with pending
     * prescriptions, the method informs the user.
     */
    public void displayAllPendingAppointmentOutcomes() {

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       Appointment Outcome Records      ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println(
                "\n══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");

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
                String[] prescriptionIdArray = prescriptionIds.split(",");
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
                        medications.add(String.format("%dx %s (%s)", prescription.getQuantity(), medicineName,
                                prescription.getStatus()));
                    }
                }

                // Add to list if it has at least one pending prescription
                if (hasPendingPrescription) {
                    pendingAppointments.add(new String[] {
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
                System.out.printf("%-15s ║ %-12s ║ %-18s ║ %-20s ║ %-40s ║ %-40s ║%n", // Adjusted Service Type width
                        "Appointment ID", "Date", "Doctor", "Service Type", "Medications", "Consultation Notes");
                System.out.println(
                        "══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");

                for (String[] appointment : pendingAppointments) {
                    // Print the first line with all data, including consultation notes
                    System.out.printf("%-15s ║ %-12s ║ %-18s ║ %-20s ║ %-40s ║ %-40s ║%n", // Adjusted Service Type
                                                                                           // width
                            appointment[0], appointment[1], appointment[2], appointment[3],
                            appointment[4].split("\n")[0], appointment[5]);

                    // Print additional lines for each medication if there are multiple
                    String[] medications = appointment[4].split("\n");
                    for (int i = 1; i < medications.length; i++) {
                        System.out.printf("%-15s ║ %-12s ║ %-18s ║ %-20s ║ %-40s ║ %-40s ║%n", // Adjusted Service Type
                                                                                               // width
                                "", "", "", "", medications[i], "");
                    }
                    System.out.println(
                            "══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading files: " + e.getMessage());
        }
        PrintUtils.pause();
    }

    /**
     * Retrieves details of a specific prescription by its ID.
     *
     * This method reads from the prescription file to find the prescription
     * with the specified ID. If found, it returns a Prescription object
     * containing the prescription ID, associated medicine ID, quantity, and
     * status. If the prescription is not found, it returns null.
     *
     * @param prescriptionId the ID of the prescription to be retrieved
     * @return a Prescription object with the specified ID, or null if not found
     */
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

    /**
     * Retrieves a set of prescription IDs with a status of "PENDING".
     *
     * This method reads from the prescription file, collecting the IDs of all
     * prescriptions with a status of "PENDING". If an error occurs while
     * reading the file, it prints an error message and continues. The set of
     * pending prescription IDs is then returned.
     *
     * @return a set of prescription IDs with a status of "PENDING"
     */
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

    /**
     * Displays appointment outcomes for the specified patient ID, sorted by date
     * of appointment in ascending order. Each appointment outcome is displayed
     * in a table row format, with columns for date, doctor, service type,
     * medications, and consultation notes. If no appointment outcomes are
     * found, an appropriate error message is displayed.
     *
     * @param patientId the ID of the patient for whom to display appointment
     *                  outcomes
     */
    public void displayAppointmentOutcomesByPatientId(String patientId) {
        List<AppointmentOutcome> outcomes = getAppointmentOutcomesByPatientId(patientId);

        if (outcomes.isEmpty()) {
            System.out.println("No appointment outcomes found for patient ID: " + patientId);
        } else {
            // Sort outcomes by date of appointment in ascending order
            outcomes.sort(Comparator.comparing(AppointmentOutcome::getDateOfAppointment));

            // Print table headers
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║          Appointment Outcomes          ║");
            System.out.println("╚════════════════════════════════════════╝");

            System.out.println(
                    "══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");

            System.out.printf("%-15s ║ %-20s ║ %-20s ║ %-50s ║ %-51s ║%n",
                    "Date", "Doctor", "Service Type",
                    "Medications", "Consultation Notes");
            System.out.println(
                    "══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");

            // Display each appointment outcome in a table row format
            for (AppointmentOutcome outcome : outcomes) {
                // Retrieve doctor name using doctorId
                String doctorName = getDoctorName(outcome.getDoctorId());

                // Retrieve each prescribed medication with quantity and status
                List<Prescription> prescriptions = getPrescriptionsByIds(outcome.getPrescribedMedications());
                if (prescriptions.isEmpty()) {
                    // No prescriptions, display "No Prescription" on the first row
                    System.out.printf("%-15s ║ %-20s ║ %-20s ║ %-50s ║ %-51s ║%n",
                            outcome.getDateOfAppointment().format(dateFormatter),
                            doctorName,
                            outcome.getServiceType(),
                            "- No Prescription",
                            outcome.getConsultationNotes()); // Consultation notes at the top
                } else {
                    // Print the first row with the first prescription
                    Prescription firstPrescription = prescriptions.get(0);
                    String medicineName = getMedicineName(firstPrescription.getMedicineId());
                    System.out.printf("%-15s ║ %-20s ║ %-20s ║ %-50s ║ %-51s ║%n",
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
                        System.out.printf("%-15s ║ %-20s ║ %-20s ║ %-50s ║ %-51s ║%n",
                                "", "", "",
                                String.format("%d. %dx %s (%s)", i + 1, prescription.getQuantity(), medicineName,
                                        prescription.getStatus()),
                                ""); // Empty Consultation Notes column here
                    }
                }
                System.out.println(
                        "══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");

            }
        }
        PrintUtils.pause();
    }

    /**
     * Retrieves a list of prescriptions based on a list of prescription IDs.
     *
     * This method reads the prescription data from the prescription file and
     * matches each prescription ID from the provided list with the records in
     * the file. If a match is found, it creates a Prescription object and adds
     * it to the result list.
     *
     * The prescription file is expected to have records in the format:
     * "prescriptionId|medicineId|quantity|status".
     *
     * @param prescriptionIds a list of prescription IDs to be retrieved
     * @return a list of Prescription objects corresponding to the provided IDs;
     *         an empty list if no prescriptions match the given IDs
     */
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

    /**
     * Retrieves the name of a specific medicine by its ID.
     * 
     * The method reads the medicine data from the medicine file and matches the
     * provided medicine ID with the records in the file. If a match is found, it
     * returns the medicine name (assumed to be at index 1). If no match is found
     * or if there is an error reading the file, the method returns "Unknown
     * Medicine".
     * 
     * @param medicineId the ID of the medicine to be retrieved
     * @return the name of the medicine with the given ID, or "Unknown Medicine"
     *         if no match is found
     */
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

    /**
     * Retrieves a list of appointment outcomes for a specific patient ID.
     *
     * This method reads the appointment outcome data from a file and filters
     * the records by the provided patient ID. For each matching record, it
     * parses the data to create an AppointmentOutcome object, which includes
     * details such as appointment ID, doctor ID, date of appointment, service
     * type, prescribed medications, and consultation notes. The list of
     * AppointmentOutcome objects is then returned.
     *
     * @param patientId the ID of the patient for whom to retrieve appointment
     *                  outcomes
     * @return a list of AppointmentOutcome objects for the specified patient;
     *         an empty list if no matching outcomes are found
     */
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

    /**
     * Parse the prescription IDs from the given string, which is
     * semicolon-separated.
     * If the string is empty or equals to "-", return an empty list.
     * Otherwise, split the string into an array of IDs and add them to the result
     * list.
     * 
     * @param prescriptionField the string to parse
     * @return a list of prescription IDs
     */
    private List<String> parsePrescriptionIds(String prescriptionField) {
        List<String> prescriptionIds = new ArrayList<>();
        if (prescriptionField.equals("-")) {
            return prescriptionIds;
        }
        if (prescriptionField != null && !prescriptionField.isEmpty()) {
            String[] idsArray = prescriptionField.split(",");
            for (String id : idsArray) {
                prescriptionIds.add(id.trim()); // Add each prescription ID to the list
            }
        }
        return prescriptionIds;
    }

    /**
     * Retrieves the full name of a doctor given their ID.
     *
     * This method reads the staff file to find the doctor with the specified ID
     * and returns their full name, which is a concatenation of their first and
     * last names. If the doctor ID is not found or there is an error reading
     * the file, it returns "Doctor not found.".
     *
     * @param doctorId the ID of the doctor whose name is to be retrieved
     * @return the full name of the doctor or "Doctor not found." if not found
     */
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

    /**
     * Retrieves a list of upcoming booked appointments for a given doctor ID.
     *
     * This method reads the appointment file and filters the records by the
     * provided doctor ID and a status of "BOOKED". It collects all matching
     * records and returns them as a list of strings. If no matching records are
     * found or there is an error reading the file, the method returns null.
     *
     * @param doctorId the ID of the doctor for whom to retrieve upcoming
     *                 appointments
     * @return a list of upcoming booked appointments for the given doctor,
     *         or null if none are found
     */
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

    /**
     * Retrieves a list of patients from the patient file.
     *
     * This method reads the patient file, parsing each line into fields
     * and collecting those entries that have more than 8 fields. It returns
     * a list of strings where each string represents a patient record with
     * patient details separated by the '|' character. If no valid records
     * are found or an error occurs during file reading, it returns null.
     *
     * @return a list of strings representing patient records; null if no records
     *         are found or an error occurs
     */
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

    /**
     * Converts a list of patient records into a map of patient IDs to full names.
     *
     * This method takes a list of patient strings, where each string contains
     * patient details separated by the '|' character. It parses each string to
     * extract the patient ID, first name, and last name, and combines the first
     * and last name into a full name. The resulting map associates each patient ID
     * with the corresponding full name. Only entries with at least three fields
     * (ID, first name, last name) are added to the map.
     *
     * @param patients a list of strings representing patient records, with details
     *                 separated by the '|' character
     * @return a map where each key is a patient ID and each value is the full name
     *         of the patient
     */
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

    /**
     * Takes a list of appointments in string format and a map of patient IDs to
     * full names, and returns a new list of strings where each string is an
     * appointment entry with the patient ID replaced by the full name if the
     * patient ID is present in the map.
     *
     * @param appointments a list of strings representing appointments, with
     *                     details separated by the '|' character
     * @param patientMap   a map where each key is a patient ID and each value is
     *                     the full name of the patient
     * @return a list of strings where each string is an appointment entry with
     *         the patient ID replaced by the full name if available
     */
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

    /**
     * Displays the upcoming appointments for a doctor, with the patient ID
     * replaced by the full name of the patient if available.
     *
     * @param doctorId the ID of the doctor whose upcoming appointments are
     *                 being viewed
     */
    private void viewUpcomingAppointments(String doctorId) {

        List<String> upcomingAppointments = getUpcomingAppointments(doctorId);

        List<String> patientList = getPatientList();

        Map<String, String> patientMap = getPatientMap(patientList);

        List<String> formattedAppointments = formatAppointments(upcomingAppointments, patientMap);

        for (String appointment : formattedAppointments) {
            System.out.println(appointment);
        }
    }

    /**
     * Loads the list of medicines from the medicine file.
     *
     * The file is expected to have the format:
     * medicine_id|medicine_name|medicine_price|medicine_stock
     *
     * The method reads the file line by line and splits each line into
     * an array of strings using the "|" as the delimiter. The resulting
     * list of arrays is then returned.
     *
     * @return the list of medicines
     */
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

    /**
     * Prompts the user to select an upcoming appointment to create a record.
     *
     * The method first loads the list of upcoming appointments for the given
     * doctor ID and sorts them by date and time. It then displays the sorted
     * appointments to the user, prompting the user to select an appointment
     * by index. Once the user selects an appointment, the method prompts the
     * user to select medicines to prescribe and record additional details.
     * Finally, it creates a record in the appointment outcome file and updates
     * the appointment status to "CLOSED" in the appointment file.
     *
     * @param doctorId the doctor ID to create the appointment outcome record for
     */
    private void createAppointmentOutcome(String doctorId) {
        List<String> upcomingAppointments = getUpcomingAppointments(doctorId);
        if (upcomingAppointments == null || upcomingAppointments.isEmpty()) {
            System.out.println("No upcoming appointments available.");
            return;
        }

        // Sort appointments by date and time
        upcomingAppointments.sort(Comparator.comparing(this::extractDateTimeFromAppointment));

        // Display sorted upcoming appointments with index starting from 1
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          Upcoming Appointment          ║");
        System.out.println("╚════════════════════════════════════════╝");

        List<String[]> appointmentDetailsList = new ArrayList<>();

        for (int i = 0; i < upcomingAppointments.size(); i++) {
            String[] fields = upcomingAppointments.get(i).split("\\|");
            String patientId = fields[2];
            String patientName = getPatientNameById(patientId);
            String date = fields[3];
            String time = fields[4];
            appointmentDetailsList.add(new String[] { fields[0], patientId, date, time });
            System.out.printf("%d. %s (%s %s)\n", i + 1, patientName, date, time);
        }
        System.out.println("══════════════════════════════════════════");

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

                            String prescriptionEntry = String.format("%s|%s|%d|PENDING", currentPrescriptionId,
                                    medicineId, quantity);
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
                    BillController.createBill(BILL_FILE, appointmentId, patientId);

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

    /**
     * Retrieves the last prescription ID from the prescription file.
     *
     * This method reads the prescription file line by line, extracting the
     * prescription ID from each line, and updates the lastId with the most
     * recent prescription ID found. If the file is empty or an error occurs
     * during reading, it defaults to "PR00000".
     *
     * @return the last prescription ID found in the prescription file, or
     *         "PR00000" if no entries are found or an error occurs.
     */
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

    /**
     * Generates a new prescription ID by incrementing the last prescription ID
     * found in the prescription file.
     *
     * This method takes the last prescription ID as a parameter, extracts the
     * numerical part of the ID, increments it by one, and formats the new ID
     * with the same prefix ("PR") and padding (5 digits).
     *
     * @param lastId the last prescription ID found in the prescription file
     * @return the new prescription ID
     */
    private String incrementPrescriptionId(String lastId) {
        int lastNum = Integer.parseInt(lastId.substring(2));
        return String.format("PR%05d", lastNum + 1);
    }

    /**
     * Extracts the date and time from an appointment string and returns it as a
     * LocalDateTime object.
     *
     * This method takes an appointment string as input, splits it into fields,
     * and combines the date and time fields into a single string. It then parses
     * this string into a LocalDateTime object using the DateTimeFormatter
     * with the pattern "dd-MM-yyyy HH:mm".
     *
     * @param appointment the appointment string from which to extract the date and
     *                    time
     * @return the extracted date and time as a LocalDateTime object
     */
    private LocalDateTime extractDateTimeFromAppointment(String appointment) {
        String[] fields = appointment.split("\\|");
        String dateTimeString = fields[3] + " " + fields[4]; // Combine date and time fields
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    /**
     * Allows the doctor to select medicines for a prescription.
     *
     * Prompts the doctor to select medicines by displaying the list of available
     * medicines and their indices. For each selected medicine, prompts for the
     * quantity and generates a new prescription ID. Writes the prescription to
     * the file and adds the prescription ID to the list. Finally, joins the
     * prescription IDs with commas and returns the string for the appointment
     * outcome.
     *
     * @param scanner the scanner to read user input
     * @return a string of prescription IDs joined by commas
     */
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

    /**
     * Load the list of medicines from the medicine file.
     * 
     * This method reads the medicine file line by line and splits each line into
     * an array of strings using the "|" character as the delimiter. Each array
     * represents a medicine with its ID, name, description, stock level, low
     * stock level, and type. The method returns a list of these arrays.
     * 
     * @return A list of strings arrays, each representing a medicine.
     */
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

    /**
     * Generates a new unique prescription ID based on the last ID found in the
     * prescription file. If no entries are found, defaults to "PR00000".
     *
     * @return a unique prescription ID
     */
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

    /**
     * Retrieve the full name of a patient by their ID from the patient file.
     * 
     * @param patientId The ID of the patient to look up.
     * @return The full name of the patient, or "Unknown Patient" if not found.
     */
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

    /**
     * Prompt the user to select an appointment ID from a given list of upcoming
     * appointments.
     * 
     * The user is repeatedly asked to enter an appointment ID until a valid one is
     * selected, or the user enters '0' to return to the previous menu.
     * 
     * @param upcomingAppointments A list of strings representing the upcoming
     *                             appointments, where each string contains the
     *                             appointment details separated by the '|'
     *                             character.
     * @return The selected appointment ID, or "0" if the user chose to return.
     */
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

    /**
     * Returns the patient ID and date of appointment for a given appointment ID, or
     * null if the appointment ID is not found.
     * 
     * @param appointments  A list of strings representing the upcoming
     *                      appointments, where each string contains the appointment
     *                      details separated by the '|'
     *                      character.
     * @param appointmentId The appointment ID to search for.
     * @return An array of two strings containing the patient ID and date of
     *         appointment, or null if the appointment ID is not found.
     */
    private String[] getAppointmentDetailsById(List<String> appointments, String appointmentId) {
        for (String appointment : appointments) {
            String[] fields = appointment.split("\\|");
            if (fields[0].equals(appointmentId)) {
                return new String[] { fields[2], fields[3] }; // {patientId, dateOfAppointment}
            }
        }
        return null;
    }

    /**
     * Prompts the user to enter details related to an appointment outcome.
     *
     * This method asks the user to input the type of service provided during the
     * appointment and any consultation notes that should be recorded.
     *
     * @return An array of two strings containing the type of service and
     *         consultation notes.
     */
    private String[] promptForAppointmentDetails() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter type of service:");
        String typeOfService = scanner.nextLine().trim();

        System.out.println("Enter consultation notes:");
        String consultationNotes = scanner.nextLine().trim();

        return new String[] { typeOfService, consultationNotes };
    }

    /**
     * Checks if an appointment ID is found in the given list of appointments.
     * 
     * This method iterates through the list of appointments and checks if the
     * appointment ID is equal to the first field of each appointment string.
     * If a match is found, the method returns true. Otherwise, it returns false.
     * 
     * @param appointmentId The appointment ID to search for.
     * @param appointments  A list of strings representing the upcoming
     *                      appointments, where each string contains the
     *                      appointment details separated by the '|'
     *                      character.
     * @return true if the appointment ID is found, false otherwise.
     */
    private boolean checkAppointmentId(String appointmentId, List<String> appointments) {
        for (String appointment : appointments) {
            String[] fields = appointment.split("\\|");
            if (fields[0].equals(appointmentId)) {
                return true; // Appointment ID found
            }
        }
        return false; // Appointment ID not found
    }

    /**
     * Displays a menu for the doctor to manage appointment outcome records.
     * 
     * This method provides the doctor with options to create a new appointment
     * outcome record, edit an existing one, or return to the previous menu.
     * The doctor is prompted to enter their choice, and the appropriate action
     * is performed based on the selected option. If the input is invalid, the
     * doctor is informed and prompted to try again.
     * 
     * @param doctorId the ID of the doctor using the menu
     */
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

    /**
     * Displays a list of appointment outcome records for the given doctor ID,
     * allows the user to select a record to edit, prompts the user to enter new
     * service type and consultation notes, and then updates the selected record
     * in the data file.
     *
     * @param doctorId the doctor ID to update the appointment outcome record for
     */
    private void updateAppointmentOutcomeRecord(String doctorId) {
        List<AppointmentOutcome> outcomes = getAppointmentOutcomesByDoctorId(doctorId);

        // Sort outcomes by date
        outcomes.sort(Comparator.comparing(AppointmentOutcome::getDateOfAppointment));

        // Display sorted outcomes with index
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       Appointment Outcome Records      ║");
        System.out.println("╚════════════════════════════════════════╝");
        for (int i = 0; i < outcomes.size(); i++) {
            AppointmentOutcome outcome = outcomes.get(i);
            System.out.printf("%d. Appointment ID: %s ║ Date: %s ║ Service Type: %s ║ Notes: %s\n",
                    i + 1, outcome.getAppointmentId(),
                    outcome.getDateOfAppointment().format(dateFormatter),
                    outcome.getServiceType(),
                    outcome.getConsultationNotes());
        }
        System.out.println("══════════════════════════════════════════");

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

    /**
     * Returns a list of appointment outcomes for the given doctor ID.
     * 
     * This method reads the appointment outcomes from the data file and
     * filters the outcomes by the given doctor ID. The outcomes are then
     * parsed and returned as a list of AppointmentOutcome objects.
     * 
     * @param doctorId the doctor ID to filter the appointment outcomes by
     * @return a list of appointment outcomes for the given doctor ID
     */
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

    /**
     * Updates the appointment outcome file with the provided AppointmentOutcome
     * details.
     *
     * This method reads the appointment outcome file and searches for the line with
     * the matching appointment ID. If found, it updates the line with the new
     * details
     * from the AppointmentOutcome object. The updated contents are then written
     * back
     * to the file.
     *
     * @param outcome the AppointmentOutcome object containing updated details to be
     *                saved
     */
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
