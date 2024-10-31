package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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

        // Sort appointments by date and time
        availableAppointments.sort(Comparator.comparing((String line) -> {
            String[] fields = line.split("\\|");
            return LocalDate.parse(fields[3], dateFormatter);
        }).thenComparing(line -> {
            String[] fields = line.split("\\|");
            return LocalTime.parse(fields[4], timeFormatter);
        }));

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
        int selection = -1;
        while (true) {
            System.out.print("Please enter the number of the appointment you wish to select (or 0 to exit): ");
            String input = scanner.nextLine().trim();
            try {
                selection = Integer.parseInt(input);
                if (selection >= 0 && selection <= maxIndex) {
                    break;
                } else {
                    System.out.println("Invalid selection. Please enter a number between 0 and " + maxIndex + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return selection;
    }

    // Process the user's appointment selection
    private void processAppointmentSelection(int selection, List<String> appointments, String patientId) {
        String chosenAppointment = appointments.get(selection - 1);
        String[] fields = chosenAppointment.split("\\|");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Anything you would like to tell the doctor? ");
        String requestMessage = scanner.nextLine();
        if (requestMessage.length() == 0) {
            requestMessage = "-"; // CANNOT LET IT BE EMPTY.
        }

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
                    }
                }

                // Sort booked appointments by date and time
                bookedAppointments.sort(Comparator.comparing((String[] fields) -> LocalDate.parse(fields[3], dateFormatter))
                        .thenComparing(fields -> LocalTime.parse(fields[4], timeFormatter)));

                // Display sorted appointments
                if (bookedAppointments.isEmpty()) {
                    System.out.println("You have no upcoming appointments!");
                    return;
                }
                index = 1;
                for (String[] fields : bookedAppointments) {
                    LocalDate date = LocalDate.parse(fields[3], dateFormatter);
                    LocalTime time = LocalTime.parse(fields[4], timeFormatter);
                    System.out.printf("%d. Date: %s | Time: %s%n", index++, date.format(dateFormatter), time.format(timeFormatter));
                }

                System.out.println("0. Back to Main Menu");
                System.out.println("--------------------------");

                // Prompt user to select an appointment by index
                int selection = getUserSelection(bookedAppointments.size());
                if (selection == 0) {
                    System.out.println("Returning to main menu...");
                    return;
                }

                // Validate selection
                String[] selectedAppointment = bookedAppointments.get(selection - 1);
                String doctorId = selectedAppointment[1];

                // Retrieve and display doctor details
                displayDoctorDetails(doctorId);

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
