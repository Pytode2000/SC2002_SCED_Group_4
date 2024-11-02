package controller;

import entity.AppointmentOutcome;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentOutcomeController {

    private static final String APPOINTMENT_OUTCOME_FILE = "data/appointmentOutcome.txt";
    private static final String STAFF_FILE = "data/staff.txt";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Retrieve and display appointment outcomes by patient ID
    public void displayAppointmentOutcomesByPatientId(String patientId) {
        List<AppointmentOutcome> outcomes = getAppointmentOutcomesByPatientId(patientId);

        if (outcomes.isEmpty()) {
            System.out.println("No appointment outcomes found for patient ID: " + patientId);
        } else {
            for (AppointmentOutcome outcome : outcomes) {
                // Retrieve doctor name using doctorId
                String doctorName = getDoctorName(outcome.getDoctorId());

                System.out.println("\n--- Appointment Outcome ---");
                System.out.println("Date of Appointment : " + outcome.getDateOfAppointment().format(dateFormatter));
                System.out.println("Doctor              : " + doctorName);
                System.out.println("Service Type        : " + outcome.getServiceType());
                System.out.println("Prescribed Medications:");
                
                // Display formatted medications with index
                List<String> medications = outcome.getPrescribedMedications();
                int index = 1;
                for (String medication : medications) {
                    System.out.println(index++ + ". " + medication); // Print each medication and status
                }
                
                System.out.println("Consultation Notes  : " + outcome.getConsultationNotes());
                System.out.println("----------------------------\n");
            }
        }
    }

    // Retrieve appointment outcomes by patient ID from file
    public List<AppointmentOutcome> getAppointmentOutcomesByPatientId(String patientId) {
        List<AppointmentOutcome> outcomes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_OUTCOME_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length >= 7 && fields[1].equals(patientId)) {
                    LocalDate dateOfAppointment = LocalDate.parse(fields[3], dateFormatter);

                    // Parse medications as a list of strings
                    List<String> medications = parseMedications(fields[5]);

                    // Create appointment outcome
                    AppointmentOutcome outcome = new AppointmentOutcome(
                            fields[0], // appointmentId
                            fields[1], // patientId
                            fields[2], // doctorId
                            dateOfAppointment,
                            fields[4], // serviceType
                            medications, // Parsed medications list
                            fields[6] // consultationNotes
                    );
                    outcomes.add(outcome);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading appointment outcome file: " + e.getMessage());
        }
        return outcomes;
    }

    // Helper method to parse medications into a formatted list of strings
    private List<String> parseMedications(String medicationsField) {
        List<String> medications = new ArrayList<>();
        String[] medsArray = medicationsField.split(";");

        for (String med : medsArray) {
            String[] parts = med.split(",");
            if (parts.length == 2) {
                String name = parts[0];
                String status = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase(); // Capitalize status
                medications.add(name + " (" + status + ")");  // Format as "Name (Status)"
            }
        }

        return medications;
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
}
