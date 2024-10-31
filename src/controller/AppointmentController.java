package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AppointmentController {

    private static final String APPOINTMENT_FILE = "data/appointment.txt";
    private static final String STAFF_FILE = "data/staff.txt";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    // Main method to retrieve, display, and select AVAILABLE appointments
    public void displayAndSelectAvailableAppointments(String patientId) {
        List<String> availableAppointments = getAvailableAppointments();

        if (availableAppointments.isEmpty()) {
            System.out.println("No available appointments found.");
            return;
        }

        displayAppointmentsWithIndex(availableAppointments);

        int selection = getUserSelection(availableAppointments.size());
        if (selection == 0) {
            System.out.println("Returning to main menu...");
            return;
        }

        processAppointmentSelection(selection, availableAppointments, patientId);
    }

    // Fetches available appointments
    private List<String> getAvailableAppointments() {
        List<String> availableAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length >= 6 && fields[5].equals("AVAILABLE")) {
                    availableAppointments.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return availableAppointments;
    }

    // Displays appointments with a selection index
    private void displayAppointmentsWithIndex(List<String> appointments) {
        System.out.println("\nAvailable Appointments:");
        System.out.println("-----------------------");
        int index = 1;
        for (String appointment : appointments) {
            String[] fields = appointment.split("\\|");
            LocalDate date = LocalDate.parse(fields[3], dateFormatter);
            LocalTime time = LocalTime.parse(fields[4], timeFormatter);
            System.out.printf("%d. Date: %s | Time: %s%n", index++, date.format(dateFormatter), time.format(timeFormatter));
        }
        System.out.println("0. Back to Main Menu");
        System.out.println("-----------------------");
    }

    // Get user selection for appointment
    private int getUserSelection(int maxIndex) {
        Scanner scanner = new Scanner(System.in);
        int selection;
        do {
            System.out.print("Please enter the number of the appointment you wish to select (or 0 to exit): ");
            selection = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } while (selection < 0 || selection > maxIndex);
        return selection;
    }

    // Process the user's appointment selection
    private void processAppointmentSelection(int selection, List<String> appointments, String patientId) {
        String chosenAppointment = appointments.get(selection - 1);
        String[] fields = chosenAppointment.split("\\|");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Anything you would like to tell the doctor? ");
        String requestMessage = scanner.nextLine();

        // Update fields for the selected appointment
        fields[2] = patientId;
        fields[5] = "PENDING";
        fields[6] = requestMessage;

        // Update appointment in file
        updateAppointmentInFile(chosenAppointment, String.join("|", fields));
        System.out.println("Pending request, awaiting Doctor's approval.");
    }

    // Update a specific line in the file
    private void updateAppointmentInFile(String oldLine, String newLine) {
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line.equals(oldLine) ? newLine : line);
                }
            }
            try (FileWriter writer = new FileWriter(APPOINTMENT_FILE, false)) {
                for (String line : lines) {
                    writer.write(line + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// Method to display all booked appointments for a patient and allow selection to view doctor details
    public void displayAndSelectBookedAppointments(String patientId) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            List<String[]> bookedAppointments = new ArrayList<>();
            System.out.println("\nYour Booked Appointments:");
            System.out.println("--------------------------");

            // Retrieve booked appointments
            try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
                String line;
                int index = 1;

                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\|");

                    // Check if the appointment is booked for the patient
                    if (fields.length >= 6 && fields[2].equals(patientId) && fields[5].equals("BOOKED")) {
                        bookedAppointments.add(fields);
                        LocalDate date = LocalDate.parse(fields[3], dateFormatter);
                        LocalTime time = LocalTime.parse(fields[4], timeFormatter);
                        System.out.printf("%d. Date: %s | Time: %s%n", index++, date.format(dateFormatter), time.format(timeFormatter));
                    }
                }

                // If no booked appointments found, notify and exit
                if (bookedAppointments.isEmpty()) {
                    System.out.println("You have no upcoming appointments!");
                    return;
                }

                System.out.println("0. Back to Main Menu");
                System.out.println("--------------------------");

                // Prompt user to select an appointment by index
                System.out.print("Please enter the number of the appointment to view doctor details (or 0 to exit): ");
                int selection = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                // Check for exit option
                if (selection == 0) {
                    System.out.println("Returning to main menu...");
                    return;
                }

                // Validate selection
                if (selection > 0 && selection <= bookedAppointments.size()) {
                    String[] selectedAppointment = bookedAppointments.get(selection - 1);
                    String doctorId = selectedAppointment[1];

                    // Retrieve and display doctor details
                    displayDoctorDetails(doctorId);

                } else {
                    System.out.println("Invalid selection. Please try again.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

// Method to retrieve and display doctor details from staff.txt based on doctorId
    private void displayDoctorDetails(String doctorId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(STAFF_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");

                // Check if this is the correct doctor
                if (fields[0].equals(doctorId)) {
                    // Construct and display the doctor's details using Staff's attributes
                    System.out.println("\nDoctor Details:"
                            + "\nName: " + fields[1] + " " + fields[2]
                            + "\nGender: " + fields[3]
                            + "\nContact Number: " + fields[4]
                            + "\nEmail Address: " + fields[5]);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
