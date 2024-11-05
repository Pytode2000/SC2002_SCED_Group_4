package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import utility.FileUtils;
import utility.PrintUtils;

public class AppointmentController {

    private static final String APPOINTMENT_FILE = "data/appointment.txt";
    private static final String STAFF_FILE = "data/staff.txt";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public void scheduleAppointment(String patientId) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n--- Schedule an Appointment ---");
        System.out.println("1. See all available appointment slots");
        System.out.println("2. Select appointment by doctor");
        System.out.print("Choose an option (1 or 2, 0 to exit): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                displayAndSelectAvailableAppointments(patientId);
                break;
            case "2":
                displayAppointmentsByDoctor(patientId);
                break;
            case "0":
                System.out.println("Returning to main menu...");
                return;
            default:
                System.out.println("Invalid option. Please choose 1, 2, or 0.");
                scheduleAppointment(patientId);
        }
    }

    private void displayAppointmentsByDoctor(String patientId) {
        Map<String, List<String>> doctorAppointments = new HashMap<>();
        Scanner scanner = new Scanner(System.in);

        // Retrieve doctor details and group their appointments
        try (BufferedReader staffReader = new BufferedReader(new FileReader(STAFF_FILE));
                BufferedReader appointmentReader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {

            String line;
            Map<String, String[]> doctorDetails = new HashMap<>();

            // Read doctor details
            while ((line = staffReader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if ("Doctor".equalsIgnoreCase(fields[7])) {
                    doctorDetails.put(fields[0], new String[] { fields[1] + " " + fields[2], fields[5], fields[6] });
                    doctorAppointments.put(fields[0], new ArrayList<>());
                }
            }

            // Group available appointments by doctor and sort by date and time
            while ((line = appointmentReader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length >= 6 && "AVAILABLE".equalsIgnoreCase(fields[5])) {
                    String doctorId = fields[1];
                    if (doctorAppointments.containsKey(doctorId)) {
                        doctorAppointments.get(doctorId).add(line);
                    }
                }
            }

            // Sort each doctor's appointments by date and time
            doctorAppointments.forEach(
                    (doctorId, appointments) -> appointments.sort(Comparator.comparing((String appointment) -> {
                        String[] fields = appointment.split("\\|");
                        return LocalDate.parse(fields[3], dateFormatter);
                    }).thenComparing(appointment -> {
                        String[] fields = appointment.split("\\|");
                        return LocalTime.parse(fields[4], timeFormatter);
                    })));

            // Display appointments by doctor
            System.out.println("\n--- Available Appointments by Doctor ---");
            int globalIndex = 1;
            Map<Integer, String> indexToAppointment = new HashMap<>();

            for (Map.Entry<String, List<String>> entry : doctorAppointments.entrySet()) {
                String doctorId = entry.getKey();
                String[] details = doctorDetails.get(doctorId);
                List<String> appointments = entry.getValue();

                System.out.printf("Doctor: %s | Contact: %s | Email: %s%n", details[0], details[1], details[2]);
                if (appointments.isEmpty()) {
                    System.out.println("- No appointment slots available");
                } else {
                    for (String appointment : appointments) {
                        String[] fields = appointment.split("\\|");
                        String date = fields[3];
                        String time = fields[4];
                        System.out.printf("%d - Date: %s | Time: %s%n", globalIndex, date, time);
                        indexToAppointment.put(globalIndex++, appointment);
                    }
                }
                System.out.println("-----------------------------------");
            }

            // Prompt user to select an appointment
            while (true) {
                System.out.print("Enter the number of the appointment slot to select, or 0 to exit: ");
                String input = scanner.nextLine().trim();
                try {
                    int selection = Integer.parseInt(input);
                    if (selection == 0) {
                        System.out.println("Returning to main menu...");
                        return;
                    }
                    if (indexToAppointment.containsKey(selection)) {
                        String selectedAppointment = indexToAppointment.get(selection);
                        processAppointmentSelection(1, Collections.singletonList(selectedAppointment), patientId); // Simplified
                                                                                                                   // slot
                                                                                                                   // handling
                        break;
                    } else {
                        System.out.println("Invalid selection. Please enter a valid number from the list.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

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
            System.out.printf("%d. Date: %s | Time: %s%n", index++, date.format(dateFormatter),
                    time.format(timeFormatter));
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
        PrintUtils.pause();
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

    // Method to display all booked appointments for a patient and allow selection
    // to view doctor details
    public void displayAndSelectBookedAppointments(String patientId) {
        while (true) {
            List<String[]> bookedAppointments = new ArrayList<>();
            System.out.println("\nYour Appointments:");
            System.out.println("--------------------------");

            // Retrieve booked appointments
            try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
                String line;
                int index = 1;

                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\|");

                    // Check if the appointment is booked for the patient
                    if (fields.length >= 6 && fields[2].equals(patientId) && (fields[5].equals("BOOKED")
                            || fields[5].equals("PENDING") || fields[5].equals("RESCHEDULE"))) {
                        bookedAppointments.add(fields);
                    }
                }

                // Sort booked appointments by date and time
                bookedAppointments
                        .sort(Comparator.comparing((String[] fields) -> LocalDate.parse(fields[3], dateFormatter))
                                .thenComparing(fields -> LocalTime.parse(fields[4], timeFormatter)));

                // Display sorted appointments
                if (bookedAppointments.isEmpty()) {
                    System.out.println("You have no upcoming appointments!");
                    return;
                }

                System.out.println("Select index to view Doctor's details");
                index = 1;
                for (String[] fields : bookedAppointments) {
                    LocalDate date = LocalDate.parse(fields[3], dateFormatter);
                    LocalTime time = LocalTime.parse(fields[4], timeFormatter);
                    String status = fields[5];
                    String message = fields[6];

                    System.out.printf("%d. Date: %s | Time: %s | Status: %s | Message: %s%n", index++,
                            date.format(dateFormatter), time.format(timeFormatter), status, message);

                    // If the status is RESCHEDULED, display rescheduled date, time, and message
                    if ("RESCHEDULE".equalsIgnoreCase(status) && fields.length >= 10) {
                        String rescheduleDate = fields[7];
                        String rescheduleTime = fields[8];
                        String rescheduleMessage = fields[9];
                        System.out.printf("   - Rescheduling in progress to: %s at %s%n", rescheduleDate,
                                rescheduleTime);
                        System.out.printf("   - Message: %s%n", rescheduleMessage);
                    }
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

    // Method to retrieve and display doctor details from staff.txt based on
    // doctorId
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
                            + "\nContact Number: " + fields[5]
                            + "\nEmail Address: " + fields[6]);
                    PrintUtils.pause();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteBookedAppointment(String patientId) {
        List<String[]> bookedAppointments = new ArrayList<>();
        System.out.println("\nYour Booked Appointments:");
        System.out.println("--------------------------");

        // Retrieve booked appointments
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            int index = 1;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");

                // Check if the appointment is booked for the patient and has a status of BOOKED
                // or RESCHEDULE
                if (fields.length >= 6 && fields[2].equals(patientId)
                        && (fields[5].equals("BOOKED") || fields[5].equals("RESCHEDULE")
                                || fields[5].equals("PENDING"))) {
                    bookedAppointments.add(fields);
                }
            }

            // Sort booked appointments by date and time
            bookedAppointments.sort(Comparator.comparing((String[] fields) -> LocalDate.parse(fields[3], dateFormatter))
                    .thenComparing(fields -> LocalTime.parse(fields[4], timeFormatter)));

            // Display sorted appointments
            if (bookedAppointments.isEmpty()) {
                System.out.println("You have no booked appointments to delete!");
                PrintUtils.pause();
                return;
            }
            System.out.println("Select index to remove appointment/request");

            index = 1;
            for (String[] fields : bookedAppointments) {
                LocalDate date = LocalDate.parse(fields[3], dateFormatter);
                LocalTime time = LocalTime.parse(fields[4], timeFormatter);
                String status = fields[5];
                System.out.printf("%d. Date: %s | Time: %s | Status: %s%n", index++, date.format(dateFormatter),
                        time.format(timeFormatter), status);
            }

            System.out.println("0. Back to Main Menu");
            System.out.println("--------------------------");

            // Prompt user to select an appointment by index
            int selection = getUserSelection(bookedAppointments.size());
            if (selection == 0) {
                System.out.println("Returning to delete menu...");
                return;
            }

            // Validate selection and confirm deletion
            String[] selectedAppointment = bookedAppointments.get(selection - 1);
            LocalDate date = LocalDate.parse(selectedAppointment[3], dateFormatter);
            LocalTime time = LocalTime.parse(selectedAppointment[4], timeFormatter);
            System.out.printf("Are you sure you want to delete the appointment/request on %s at %s?%n",
                    date.format(dateFormatter), time.format(timeFormatter));

            // Prompt for confirmation
            Scanner scanner = new Scanner(System.in);
            String confirmation;
            while (true) {
                System.out.println("1: Confirm");
                System.out.println("0: Cancel");
                System.out.print("Enter your choice: ");
                confirmation = scanner.nextLine().trim();

                if (confirmation.equals("1")) {
                    String oldLine = String.join("|", selectedAppointment);

                    // If status is RESCHEDULE, reset specific fields
                    if (selectedAppointment[5].equals("RESCHEDULE")) {
                        selectedAppointment[2] = "-"; // Reset patientId
                        selectedAppointment[5] = "AVAILABLE"; // Set status to AVAILABLE
                        selectedAppointment[6] = "-"; // Clear requestMessage
                        selectedAppointment[7] = "-"; // Clear rescheduleDate
                        selectedAppointment[8] = "-"; // Clear rescheduleTime
                        selectedAppointment[9] = "-"; // Clear rescheduleMessage
                    } else {
                        // For BOOKED status, reset patientId, status, and request message only
                        selectedAppointment[2] = "-"; // Reset patientId
                        selectedAppointment[5] = "AVAILABLE"; // Set status to AVAILABLE
                        selectedAppointment[6] = "-"; // Clear requestMessage
                    }

                    String newLine = String.join("|", selectedAppointment);

                    // Update the file
                    updateAppointmentInFile(oldLine, newLine);
                    System.out.println("Appointment/Request has been successfully deleted.");
                    PrintUtils.pause();
                    deleteBookedAppointment(patientId); // Refresh the list after deletion
                    break;
                } else if (confirmation.equals("0")) {
                    System.out.println("Action canceled.");

                    deleteBookedAppointment(patientId); // Go back to the delete menu

                    break;
                } else {
                    System.out.println("Invalid input. Please enter 1 to confirm or 0 to exit.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestRescheduleAppointment(String patientId) {
        List<String[]> bookedAppointments = new ArrayList<>();
        System.out.println("\nYour Booked Appointments:");
        System.out.println("--------------------------");

        // Retrieve booked appointments
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");

                // Check if the appointment is booked for the patient
                if (fields.length >= 6 && fields[2].equals(patientId) && fields[5].equals("BOOKED")) {
                    bookedAppointments.add(fields);
                }
            }

            // Sort appointments by date and time
            bookedAppointments.sort(Comparator.comparing((String[] fields) -> LocalDate.parse(fields[3], dateFormatter))
                    .thenComparing(fields -> LocalTime.parse(fields[4], timeFormatter)));

            // Display appointments with index
            if (bookedAppointments.isEmpty()) {
                System.out.println("You have no appointments available to request rescheduling!");
                PrintUtils.pause();
                return;
            }
            System.out.println("Select appointment to request for a reschedule");

            int index = 1;
            for (String[] fields : bookedAppointments) {
                LocalDate date = LocalDate.parse(fields[3], dateFormatter);
                LocalTime time = LocalTime.parse(fields[4], timeFormatter);
                System.out.printf("%d. Date: %s | Time: %s%n", index++, date.format(dateFormatter),
                        time.format(timeFormatter));
            }

            System.out.println("0. Back to Main Menu");
            System.out.println("--------------------------");

            // Prompt user to select an appointment by index
            int selection = getUserSelection(bookedAppointments.size());
            if (selection == 0) {
                System.out.println("Returning to main menu...");
                return;
            }

            // Selected appointment
            String[] selectedAppointment = bookedAppointments.get(selection - 1);
            Scanner scanner = new Scanner(System.in);

            // Prompt for reschedule date with validation
            String day = promptForInput("Enter new day (DD): ", scanner, 1, 31);
            if (day.equals("0")) {
                return;
            }
            String month = promptForInput("Enter new month (MM): ", scanner, 1, 12);
            if (month.equals("0")) {
                return;
            }
            String year = promptForInput("Enter new year (YYYY): ", scanner, 1900, 2100);
            if (year.equals("0")) {
                return;
            }
            String rescheduleDate = String.format("%02d-%02d-%d", Integer.parseInt(day), Integer.parseInt(month),
                    Integer.parseInt(year));

            // Prompt for reschedule time with validation
            String hour = promptForInput("Enter new hour (HH, 24-hour format): ", scanner, 0, 23);
            if (hour.equals("0")) {
                return;
            }
            String minutes = promptForInput("Enter new minutes (MM): ", scanner, 0, 59);
            if (minutes.equals("0")) {
                return;
            }
            String rescheduleTime = String.format("%02d:%02d", Integer.parseInt(hour), Integer.parseInt(minutes));

            // Prompt for reschedule message
            System.out.print("Enter reschedule message: ");
            String rescheduleMessage = scanner.nextLine().trim();
            if (rescheduleMessage.isEmpty()) {
                rescheduleMessage = "-";
            }

            // Confirmation before updating
            System.out.println("\nConfirm reschedule:");
            System.out.println("New Date: " + rescheduleDate);
            System.out.println("New Time: " + rescheduleTime);
            System.out.println("Message: " + rescheduleMessage);
            String confirmation = promptForConfirmation(scanner);

            if (confirmation.equals("1")) {
                String oldLine = String.join("|", selectedAppointment);
                selectedAppointment[5] = "RESCHEDULE";
                selectedAppointment[7] = rescheduleDate;
                selectedAppointment[8] = rescheduleTime;
                selectedAppointment[9] = rescheduleMessage;

                // Update the appointment in the file
                String newLine = String.join("|", selectedAppointment);
                updateAppointmentInFile(oldLine, newLine);
                System.out.println("Reschedule request submitted.");
                PrintUtils.pause();
            } else {
                System.out.println("Reschedule request canceled.");
                PrintUtils.pause();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Utility method for input with validation and exit option
    private String promptForInput(String prompt, Scanner scanner, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equals("0")) {
                return "0";
            }
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return input;
                } else {
                    System.out.printf("Please enter a valid number between %d and %d.%n", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    // Utility method for confirmation with '1' to confirm and '0' to cancel
    private String promptForConfirmation(Scanner scanner) {
        while (true) {
            System.out.println("1: Confirm");
            System.out.println("0: Cancel");
            System.out.print("Enter your choice: ");
            String confirmation = scanner.nextLine().trim();
            if (confirmation.equals("1") || confirmation.equals("0")) {
                return confirmation;
            } else {
                System.out.println("Invalid input. Please enter 1 to confirm or 0 to cancel.");
            }
        }
    }

    public List<String> getPersonalSchedule(String doctorId) {
        List<String> allAppointmentSlots = getAvailableAppointments();
        List<String> currentDoctorSchedule = new ArrayList<>();

        for (String appointment : allAppointmentSlots) {
            // Assuming each slot string contains doctor ID in a specific format, check if
            // it matches
            if (appointment.contains(doctorId)) { // Modify as needed for exact matching logic
                currentDoctorSchedule.add(appointment);
            }
        }

        return currentDoctorSchedule;
    }

    // View Appointments with logged in doctor's ID
    public void viewPersonalSchedule(String doctorId) {

        List<String> currentDoctorSchedule = getPersonalSchedule(doctorId);

        // Print or return the currentDoctorSchedule if needed
        System.out.println("=== Appointments for Doctor ID: " + doctorId + " ===");

        if (currentDoctorSchedule.isEmpty()) {
            System.out.println("No appointments scheduled.");
        } else {
            int appointmentNumber = 1;
            for (String appointment : currentDoctorSchedule) {
                System.out.println(appointmentNumber + ". " + appointment);
                appointmentNumber++;
            }
        }

        System.out.println("===================================");
        PrintUtils.pause();
    }

    // Generate the next appointment ID by reading the last ID from the file and
    // incrementing it
    private String generateAppointmentId() {
        String lastId = "AP00000"; // Default ID if no entries are found

        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lastId = line.split("\\|")[0];
            }
        } catch (IOException e) {
            System.out.println("Error reading appointment file: " + e.getMessage());
        }

        int lastNum = Integer.parseInt(lastId.substring(2));
        return String.format("AP%05d", lastNum + 1);
    }

    public void createAvailability(String doctorId) {

        Scanner scanner = new Scanner(System.in);
        String appointmentId = generateAppointmentId();
        LocalDate date;
        LocalTime time;

        // Day Input
        String dayInput;
        int day;
        System.out.println("\nSet Availability for Appointment: ");
        System.out.println("--------------------------");
        while (true) {
            System.out.print("Enter day (DD): ");
            dayInput = scanner.nextLine().trim();
            if (dayInput.matches("\\d{2}") && (day = Integer.parseInt(dayInput)) >= 1 && day <= 31) {
                break;
            } else {
                System.out.println("Invalid day. Please enter a two-digit day between 01 and 31.");
            }
        }

        // Month Input
        String monthInput;
        int month;
        while (true) {
            System.out.print("Enter month (MM): ");
            monthInput = scanner.nextLine().trim();
            if (monthInput.matches("\\d{2}") && (month = Integer.parseInt(monthInput)) >= 1 && month <= 12) {
                break;
            } else {
                System.out.println("Invalid month. Please enter a valid two-digit month between 01 and 12.");
            }
        }

        // Year Input
        String yearInput;
        int year;
        while (true) {
            System.out.print("Enter year (YYYY): ");
            yearInput = scanner.nextLine().trim();
            if (yearInput.matches("\\d{4}")) {
                year = Integer.parseInt(yearInput);
                break;
            } else {
                System.out.println("Invalid year. Please enter a four-digit year (e.g., 1990, 2023).");
            }
        }

        // Date Assembly and Validation
        while (true) {
            try {
                date = LocalDate.of(year, month, day);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please check day, month, and year values.");
                return;
            }
        }

        // Hour Input
        String hourInput;
        int hour;
        while (true) {
            System.out.print("Enter hour (HH): ");
            hourInput = scanner.nextLine().trim();
            if (hourInput.matches("\\d{2}") && (hour = Integer.parseInt(hourInput)) >= 0 && hour <= 23) {
                break;
            } else {
                System.out.println("Invalid hour. Please enter a two-digit hour between 00 and 23.");
            }
        }

        // Minute Input
        String minuteInput;
        int minute;
        while (true) {
            System.out.print("Enter minute (MM): ");
            minuteInput = scanner.nextLine().trim();
            if (minuteInput.matches("\\d{2}") && (minute = Integer.parseInt(minuteInput)) >= 0 && minute <= 59) {
                break;
            } else {
                System.out.println("Invalid minute. Please enter a two-digit minute between 00 and 59.");
            }
        }

        // Time Assembly
        time = LocalTime.of(hour, minute);

        // Save the appointment to file
        String appointmentRecord = String.join("|",
                appointmentId,
                doctorId,
                "-",
                date.format(dateFormatter),
                time.format(timeFormatter),
                "AVAILABLE",
                "-",
                "-",
                "-",
                "-");

        try (FileWriter writer = new FileWriter(APPOINTMENT_FILE, true)) {
            writer.write(appointmentRecord + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error writing to appointment file: " + e.getMessage());
        }

        System.out.println("\n--- Appointment Added Successfully ---");
        System.out.println("Appointment ID  : " + appointmentId);
        System.out.println("Doctor ID       : " + doctorId);
        System.out.println("Date            : " + date.format(dateFormatter));
        System.out.println("Time            : " + time.format(timeFormatter));
        System.out.println("Status          : AVAILABLE");
        System.out.println("--------------------------------------\n");

    }

    public void deleteAvailability(String doctorId) {

        List<String> currentDoctorSchedule = getPersonalSchedule(doctorId);
        viewPersonalSchedule(doctorId);

        if (currentDoctorSchedule.isEmpty()) {
            System.out.println("No available slots found for deletion.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        String appointmentId;

        // Prompt the user to enter the appointment ID to delete
        while (true) {
            // Prompt the user to enter the appointment ID to delete
            System.out.print("Enter the ID of the slot you wish to delete (or type '0' to cancel): ");
            appointmentId = scanner.nextLine().trim();

            // Check if the user wants to cancel
            if (appointmentId.equals("0")) {
                System.out.println("Deletion canceled.");
                return;
            }

            for (String appointmentToDelete : currentDoctorSchedule) {
                if (appointmentToDelete.startsWith(appointmentId + "|")) {
                    FileUtils.deleteFromFile(APPOINTMENT_FILE, appointmentId);
                    System.out.println("Appointment ID " + appointmentId + " deleted successfully.");
                    return; // Exit the loop after successful deletion
                }
            }

            System.out.println("Invalid ID. Please check and try again.");
        }
    }

    // Set Doctor's availability
    public void setAvailability(String doctorId) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println(
                    "\nSet availability menu: \n1. Create availability slot \n2. Delete availability slot \n0. Return");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createAvailability(doctorId);
                    break;
                case "2":
                    deleteAvailability(doctorId);
                    break;
                case "0":
                    System.out.println("Action canceled. Returning to main menu.");
                    return;
                default:
                    System.out.println("Invalid input. Please select 1, 2, or 0.");
                    break;
            }
        }

    }

    public List<String> getAppointmentRequests(String doctorId) {
        List<String> appointmentsRequests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length >= 6 && (fields[5].equals("PENDING") || fields[5].equals("RESCHEDULE"))
                        && fields[1].equals(doctorId)) {
                    appointmentsRequests.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appointmentsRequests;
    }

    public void printAppointmentRequest(String doctorId) {

        // for each appointment in appointment request print the details
        List<String> appointmentRequests = getAppointmentRequests(doctorId);

        if (appointmentRequests.isEmpty()) {
            System.out.println("No pending appointment requests for doctor ID: " + doctorId);
            return;
        }

        System.out.println("Pending appointment requests for doctor ID: " + doctorId + ":");
        for (String request : appointmentRequests) {
            System.out.println(request);
        }
        return;
    }

    public void acceptDeclineAppointment(String doctorId) {
        Scanner scanner = new Scanner(System.in);
        String decision, appointmentId;
        String selectedAppointment = null;

        // Step 1: Display pending or rescheduled appointments for the doctor
        List<String> appointmentRequests = getAppointmentRequests(doctorId);
        if (appointmentRequests.isEmpty()) {
            return;
        }

        // Step 2: Get the appointment ID from the user
        while (true) {
            System.out.print("Enter the Appointment ID (or press 0 to return): ");
            appointmentId = scanner.nextLine();

            // Check if the user wants to exit
            if (appointmentId.equals("0")) {
                System.out.println("Returning to the previous menu.");
                return;
            }

            // Search for the appointment with the given ID
            for (String request : appointmentRequests) {
                if (request.startsWith(appointmentId + "|")) {
                    selectedAppointment = request;
                    break;
                }
            }

            // Check if a valid appointment was found
            if (selectedAppointment != null) {
                break; // Exit the loop if a matching appointment is found
            } else {
                System.out.println("Appointment ID not found. Please try again.");
            }
        }

        // Step 4: Prompt user to accept or decline the appointment
        while (true) {
            System.out.print(
                    "Do you want to accept or decline this appointment? (type 'accept', 'decline', or '0' to return): ");
            decision = scanner.nextLine().trim().toLowerCase();

            if (decision.equals("0")) {
                System.out.println("Returning to the previous menu.");
                return;
            } else if (decision.equals("accept") || decision.equals("decline")) {
                break; // Exit the loop if a valid choice is made
            } else {
                System.out.println("Invalid choice. Please enter 'accept', 'decline', or '0' to return.");
            }
        }

        // Step 6: Update the status and add notes in the data
        String[] fields = selectedAppointment.split("\\|");
        fields[5] = decision.equals("accept") ? "BOOKED" : "DECLINED";

        // If booked appointment replace new appointment date time with old one.
        if (fields[5].equals("BOOKED")) {
            fields[3] = fields[7];
            fields[4] = fields[8];
            fields[7] = "-";
            fields[8] = "-";
            fields[9] = "-";
        }

        String updatedData = String.join("|", fields);

        // Step 7: Update the file
        FileUtils.updateToFile(APPOINTMENT_FILE, updatedData, appointmentId);
        System.out.println("Appointment " + (decision.equals("accept") ? "accepted" : "declined") + " successfully.");
    }

    public void viewAppointmentRequest(String doctorId) {

        printAppointmentRequest(doctorId);
        acceptDeclineAppointment(doctorId);
        PrintUtils.pause();

        return;
    }

    public void viewUpcomingAppointments(String doctorId) {
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
            System.out.println("No upcoming appointments for doctor ID: " + doctorId);
            return;
        }

        // Print the list of upcoming appointments
        System.out.println("Upcoming appointments:");
        for (String appointment : upcomingAppointments) {
            System.out.println(appointment);
        }

        PrintUtils.pause();
    }

    // Display doctor appointment details
    public void displayDoctorAppointmentDetails() {
        System.out.println("\n--- View All Appointments ---");

        System.out.printf("%-16s %-12s %-12s %-12s %-8s %-12s %-32s %-18s %-18s %-32s%n",
                "Appointment ID", "Doctor ID", "Patient ID", "Date", "Time", "Status", "Request Message",
                "Reschedule Date", "Reschedule Time", "Reschedule Message");
        System.out.println(
                "-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length < 10) {
                    System.out.println("Skipping appointment with invalid number of fields: " + line);
                    continue;
                }
                String status = fields[5];
                if (!status.equals("BOOKED")) {
                    continue;
                }
                String appointmentId = fields[0];
                String doctorId = fields[1];
                String patientId = fields[2];
                LocalDate date;
                LocalTime time;
                String requestMessage;
                String rescheduleDate;
                String rescheduleTime;
                String rescheduleMessage;
                try {
                    date = LocalDate.parse(fields[3], dateFormatter);
                    time = LocalTime.parse(fields[4], timeFormatter);
                    requestMessage = fields[6];
                    rescheduleDate = fields[7];
                    rescheduleTime = fields[8];
                    rescheduleMessage = fields[9];
                } catch (DateTimeParseException e) {
                    System.out.println("Skipping appointment with invalid date/time: " + line);
                    continue;
                }
                System.out.printf("%-16s %-12s %-12s %-12s %-8s %-12s %-32s %-18s %-18s %-32s%n",
                        appointmentId, doctorId, patientId, date.format(dateFormatter), time.format(timeFormatter),
                        status, requestMessage, rescheduleDate, rescheduleTime, rescheduleMessage);
            }
        } catch (IOException e) {
            System.out.println("Error reading appointment file: " + e.getMessage());
        }

        // Prompt for patient ID to view appointment outcome details
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter patient ID to view appointment outcome details: ");
        String patientId = scanner.nextLine().trim();

        if (patientId.isEmpty()) {
            System.out.println("Please enter a valid patient ID.");
            return;
        }

        AppointmentOutcomeController appointmentOutcomeController = new AppointmentOutcomeController();
        appointmentOutcomeController.displayAppointmentOutcomesByPatientId(patientId);

        // PrintUtils.pause();
    }

}
