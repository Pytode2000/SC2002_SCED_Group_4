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

    /**
     * Schedules an appointment for a patient.
     *
     * This method presents the patient with options to either view all available
     * appointment slots or select an appointment by doctor. The patient can also
     * choose to exit the scheduling process. Based on the patient's choice, the
     * method will call the appropriate function to display and select appointments.
     *
     * @param patientId the ID of the patient for whom the appointment is being
     *                  scheduled
     */
    public void scheduleAppointment(String patientId) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         Schedule an Appointment        ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("1. See all available appointment slots");
        System.out.println("2. Select appointment by doctor");
        System.out.println("══════════════════════════════════════════");
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

    /**
     * Displays all available appointments by doctor, sorted by date and time.
     *
     * The method first reads the doctor details from the STAFF_TXT file and
     * groups the available appointments by doctor ID. Then, it sorts each
     * doctor's appointments by date and time. Finally, it displays the
     * appointments to the user, prompting the user to select an appointment.
     *
     * @param patientId the ID of the patient for whom the appointment is being
     *                  scheduled
     */
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
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║    Available Appointments by Doctor    ║");
            System.out.println("╚════════════════════════════════════════╝");

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
                System.out.println("══════════════════════════════════════════");
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

    /**
     * Displays all available appointments, sorted by date and time, and
     * prompts the user to select one. If no available appointments are found,
     * the method returns without doing anything else.
     *
     * @param patientId the ID of the patient for whom the appointment is being
     *                  scheduled
     */
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

    /**
     * Retrieves a list of available appointments from the appointment file.
     * An appointment is considered available if its status is "AVAILABLE".
     * 
     * @return a list of strings representing available appointments, where each
     *         string
     *         contains the appointment details separated by the '|' character.
     */
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

    /**
     * Displays a list of available appointments with their respective indices.
     *
     * This method prints the appointment details in a formatted manner, displaying
     * the date and time of each appointment. The appointments are indexed for easy
     * selection. An option to return to the main menu is also provided.
     *
     * @param appointments a list of strings, where each string contains the
     *                     details of an appointment separated by the '|' character.
     */
    private void displayAppointmentsWithIndex(List<String> appointments) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          Available Appointments        ║");
        System.out.println("╚════════════════════════════════════════╝");

        int index = 1;
        for (String appointment : appointments) {
            String[] fields = appointment.split("\\|");
            LocalDate date = LocalDate.parse(fields[3], dateFormatter);
            LocalTime time = LocalTime.parse(fields[4], timeFormatter);
            System.out.printf("%d. Date: %s | Time: %s%n", index++, date.format(dateFormatter),
                    time.format(timeFormatter));
        }
        System.out.println("0. Back to Main Menu");
        System.out.println("══════════════════════════════════════════");
    }

    /**
     * Retrieves a user's selection of an appointment from a list of available
     * appointments.
     * 
     * This method repeatedly prompts the user to enter a valid number of an
     * appointment until
     * a valid selection is made. The user is given the option to return to the main
     * menu by
     * entering 0.
     * 
     * @param maxIndex the maximum index of the available appointments
     * @return the index of the selected appointment
     */
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

    /**
     * Processes the selection of an appointment slot by a patient.
     *
     * This method updates the selected appointment with the patient's ID,
     * changes its status to "PENDING", and adds any request message from
     * the patient. The updated appointment is then saved back to the file.
     * Finally, it informs the user that the appointment is pending approval
     * from the doctor.
     *
     * @param selection    the index of the selected appointment in the list
     * @param appointments a list of available appointment slots
     * @param patientId    the ID of the patient scheduling the appointment
     */
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

    /**
     * Updates the appointment file by replacing a specific line with a new line.
     *
     * This method reads the appointment file and searches for the specified
     * old line. If found, it replaces the old line with the provided new line
     * in the file. The updated contents are then written back to the file.
     *
     * @param oldLine the existing line in the file to be replaced
     * @param newLine the new line to replace the old line in the file
     */
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

    /**
     * Displays a list of booked appointments for a patient and allows them to
     * select
     * an appointment to view the doctor's details.
     * 
     * This method reads the appointment file, filters out the booked appointments
     * for the patient, sorts them by date and time, and displays them in a table
     * format. The user is then prompted to select an appointment by index. If the
     * selected appointment is valid, the method displays the doctor's details.
     * 
     * @param patientId the ID of the patient whose booked appointments are to be
     *                  displayed
     */
    public void displayAndSelectBookedAppointments(String patientId) {
        while (true) {
            List<String[]> bookedAppointments = new ArrayList<>();
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║            Your Appointments           ║");
            System.out.println("╚════════════════════════════════════════╝");

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
                    PrintUtils.pause();
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
                System.out.println("══════════════════════════");

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

    /**
     * Prints out doctor details in a formatted table, given a doctor ID.
     * 
     * The method reads the STAFF_TXT file line by line, splits each line into
     * fields, and checks if the doctor ID matches the ID in the line. If it
     * does, the method prints out the doctor's name, gender, contact number,
     * and email address in a formatted table.
     * 
     * The method also prints a header with the column names, and a footer with
     * a horizontal line. Finally, the method calls {@link PrintUtils#pause()} to
     * pause the console output.
     * 
     * @param doctorId the ID of the doctor whose details should be displayed
     */
    private void displayDoctorDetails(String doctorId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(STAFF_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");

                // Check if this is the correct doctor
                if (fields[0].equals(doctorId)) {
                    System.out.println("\n╔════════════════════════════════════════╗");
                    System.out.println("║             Doctor Details             ║");
                    System.out.println("╚════════════════════════════════════════╝");
                    System.out.println("\nName: " + fields[1] + " " + fields[2]
                            + "\nGender: " + fields[3]
                            + "\nContact Number: " + fields[5]
                            + "\nEmail Address: " + fields[6]);
                    System.out.println("══════════════════════════════════════════");
                    PrintUtils.pause();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a booked appointment for a given patient.
     * 
     * This method retrieves all booked appointments for the specified patient from
     * the
     * appointment file, sorts them by date and time, and displays them in a
     * formatted
     * manner. The patient is prompted to select an appointment to delete. Upon
     * confirmation,
     * the appointment is marked as "AVAILABLE" by resetting the patient ID, status,
     * and any
     * request messages. The changes are then saved back to the file. If the
     * appointment status
     * is "RESCHEDULE", additional fields such as reschedule date and time are also
     * reset.
     * 
     * @param patientId the ID of the patient whose booked appointment is to be
     *                  deleted
     */
    public void deleteBookedAppointment(String patientId) {
        List<String[]> bookedAppointments = new ArrayList<>();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        Your Booked Appointments        ║");
        System.out.println("╚════════════════════════════════════════╝");

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
            System.out.println("══════════════════════════════════════════");

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

    /**
     * Allows a patient to request rescheduling of their booked appointments.
     *
     * This method retrieves all booked appointments for a given patient ID,
     * displays them,
     * and allows the patient to select one for rescheduling. After selecting an
     * appointment,
     * the patient can specify a new date, time, and an optional message for the
     * reschedule request.
     * The updated appointment details are then saved to the appointment file.
     *
     * @param patientId the ID of the patient requesting the reschedule
     */
    public void requestRescheduleAppointment(String patientId) {
        List<String[]> bookedAppointments = new ArrayList<>();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        Your Booked Appointments        ║");
        System.out.println("╚════════════════════════════════════════╝");

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
            System.out.println("══════════════════════════════════════════");

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

    /**
     * Prompts the user for input and validates it within a specified range.
     *
     * This method continuously prompts the user to enter a number within
     * the specified minimum and maximum range until a valid input is provided.
     * It returns the input as a string. If the user enters "0", the method
     * returns "0" immediately.
     *
     * @param prompt  the message to display to the user
     * @param scanner the Scanner object used to read the user's input
     * @param min     the minimum valid value (inclusive)
     * @param max     the maximum valid value (inclusive)
     * @return the validated input as a string or "0" if the operation is canceled
     */
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

    /**
     * Prompts the user to confirm or cancel an action.
     * 
     * This method prints a confirmation prompt to the user, asking them to enter
     * 1 to confirm or 0 to cancel. It then reads the user's input and continues
     * to prompt until a valid input is provided. If the user enters 1, the
     * method returns "1". If the user enters 0, the method returns "0".
     * 
     * @param scanner the Scanner object used to read the user's input
     * @return "1" if the user confirmed, or "0" if the user canceled
     */
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

    /**
     * Retrieves a list of appointments for a specific doctor that are either
     * available or booked.
     *
     * This method reads from the appointment file, filtering the records based on
     * the provided doctor ID
     * and the status of the appointment. It collects appointments with a status of
     * "AVAILABLE" or "BOOKED".
     *
     * @param doctorId the ID of the doctor whose appointments are being retrieved
     * @return a list of appointment strings that are either available or booked for
     *         the specified doctor
     */
    public List<String> getAvailableAndBookedAppointment(String doctorId) {
        List<String> doctorAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length >= 6 && (fields[5].equals("AVAILABLE")) || fields[5].equals("BOOKED")) {
                    doctorAppointments.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doctorAppointments;
    }

    /**
     * Retrieves the personal schedule of a doctor by filtering appointments.
     *
     * This method fetches all available and booked appointment slots for a given
     * doctor ID, then filters and returns only the appointments that are associated
     * with the specified doctor.
     *
     * @param doctorId the ID of the doctor whose personal schedule is being
     *                 retrieved
     * @return a list of appointment strings that belong to the specified doctor
     */
    public List<String> getPersonalSchedule(String doctorId) {
        List<String> allAppointmentSlots = getAvailableAndBookedAppointment(doctorId);
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

    /**
     * Displays the personal schedule of a doctor by printing all their
     * appointments.
     *
     * This method retrieves the current schedule for the specified doctor by
     * calling
     * `getPersonalSchedule` and displays each appointment's details, including
     * date,
     * time, status, and any consultation notes, in a formatted manner. If no
     * appointments
     * are scheduled, it informs the user.
     *
     * @param doctorId the ID of the doctor whose schedule is being viewed
     */
    public void viewPersonalSchedule(String doctorId) {

        List<String> currentDoctorSchedule = getPersonalSchedule(doctorId);

        // Print or return the currentDoctorSchedule if needed
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           Doctor Appointments          ║");
        System.out.println("╚════════════════════════════════════════╝");

        if (currentDoctorSchedule.isEmpty()) {
            System.out.println("No appointments scheduled.");
        } else {
            int appointmentNumber = 1;
            for (String appointment : currentDoctorSchedule) {
                String[] fields = appointment.split("\\|");
                LocalDate date = LocalDate.parse(fields[3], dateFormatter);
                LocalTime time = LocalTime.parse(fields[4], timeFormatter);
                String status = fields[5];
                String message = fields[6];

                // Format the date and time
                String formattedDate = date.format(dateFormatter);
                String formattedTime = time.format(timeFormatter);

                // Only show consultation notes if they are not "-"
                String consultationNotes = message.equals("-") ? "" : " - " + message;

                // Print appointment details in the desired format
                System.out.printf("%d. %s %s (%s)%s%n", appointmentNumber++, formattedDate, formattedTime, status,
                        consultationNotes);
            }
        }

        System.out.println("══════════════════════════════════════════");
        PrintUtils.pause();
    }

    /**
     * Generates a unique appointment ID based on the last ID found in the
     * appointment file. If no entries are found, defaults to "AP00000".
     *
     * @return a unique appointment ID
     */
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

    /**
     * Allows a doctor to add a new availability to their schedule.
     *
     * Prompts the doctor to enter a day, month, year, hour, and minute for the
     * new availability. The entered values are validated and then written to the
     * appointment file in the format
     * "AP00000|doctor_id|patient_id|date|time|status|request_message|reschedule_date|reschedule_time|reschedule_message".
     *
     * If any of the entered values are invalid, the user is prompted to re-enter
     * the value until a valid input is given.
     *
     * The doctor is displayed a confirmation message after the appointment has
     * been successfully added.
     *
     * @param doctorId the doctor ID to associate with the new availability
     */
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

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     Appointment Added Successfully     ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Appointment ID  : " + appointmentId);
        System.out.println("Doctor ID       : " + doctorId);
        System.out.println("Date            : " + date.format(dateFormatter));
        System.out.println("Time            : " + time.format(timeFormatter));
        System.out.println("Status          : AVAILABLE");
        System.out.println("══════════════════════════════════════════\n");
    }

    /**
     * Prompts the user to enter the appointment ID to delete from the doctor's
     * schedule.
     * The user can enter '0' to cancel the deletion.
     * The function will remove the selected appointment from the doctor's schedule
     * and remove the corresponding
     * record from the appointment file.
     * 
     * @param doctorId the doctor ID to delete the appointment from
     */
    public void deleteAvailability(String doctorId) {

        List<String> currentDoctorSchedule = getPersonalSchedule(doctorId);
        viewPersonalSchedule(doctorId);

        if (currentDoctorSchedule.isEmpty()) {
            System.out.println("No available slots found for deletion.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int appointmentId;

        // Prompt the user to enter the appointment ID to delete
        while (true) {
            // Prompt the user to enter the appointment ID to delete
            System.out.print("Enter the slot you wish to delete (or type '0' to cancel): ");
            appointmentId = scanner.nextInt();
            scanner.nextLine();

            // Check if the user wants to cancel
            if (appointmentId == 0) {
                System.out.println("Deletion canceled.");
                return;
            }

            if (appointmentId > currentDoctorSchedule.size()) {
                System.out.println("Invalid choice. Please re-enter.");
                continue;
            }
            String appointmentToDelete = currentDoctorSchedule.get(appointmentId - 1);
            String[] fields = appointmentToDelete.split("\\|");

            FileUtils.deleteFromFile(APPOINTMENT_FILE, fields[0]);
            System.out.println("Appointment ID " + appointmentId + " deleted successfully.");
            break;
        }
    }

    /**
     * Menu for doctor to set availability.
     * The doctor can create or delete their availability slots.
     * The doctor can also return to the main menu.
     * 
     * @param doctorId the doctor ID to set availability for
     */
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

    /**
     * Retrieve all appointment requests for a given doctor ID.
     * 
     * @param doctorId the doctor ID to retrieve appointment requests for
     * @return a list of appointment requests for the given doctor ID. Each element
     *         in the list is a string
     *         containing the appointment details in the following format:
     *         "appointmentId|doctorId|patientId|dateOfAppointment|timeOfAppointment|status|patientName"
     */
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

    /**
     * Prints out all the pending appointment requests for a given doctor ID.
     * For each appointment, it prints the appointment ID, patient ID, date, time,
     * status, consultation notes (if the status is PENDING), and reschedule details
     * (if the status is RESCHEDULE).
     * 
     * @param doctorId the doctor ID to print appointment requests for
     */
    public void printAppointmentRequest(String doctorId) {

        // For each appointment in the appointment request, print the details
        List<String> appointmentRequests = getAppointmentRequests(doctorId);
        int counter = 1;

        if (appointmentRequests.isEmpty()) {
            System.out.println("No pending appointment requests for doctor ID: " + doctorId);
            return;
        }

        System.out.println("Pending appointment requests for doctor ID: " + doctorId + ":");
        for (String request : appointmentRequests) {
            String[] fields = request.split("\\|");

            // Extract date, time, status, consultation notes, and reschedule details
            LocalDate date = LocalDate.parse(fields[3], dateFormatter);
            LocalTime time = LocalTime.parse(fields[4], timeFormatter);
            String status = fields[5];
            String patientId = fields[2];
            String consultationNotes = fields[6]; // Notes for PENDING status
            String rescheduleDate = fields.length > 7 ? fields[7] : "-";
            String rescheduleTime = fields.length > 8 ? fields[8] : "-";
            String rescheduleMessage = fields.length > 9 ? fields[9] : "-";

            // Format the date and time
            String formattedDate = date.format(dateFormatter);
            String formattedTime = time.format(timeFormatter);

            // Display basic appointment details
            System.out.printf("%d. %s / %s: %s %s (%s)", counter++, fields[0], patientId, formattedDate, formattedTime,
                    status);

            // Display consultation notes if the status is PENDING
            if ("PENDING".equalsIgnoreCase(status) && !consultationNotes.equals("-")) {
                System.out.printf(" - Consultation Notes: %s", consultationNotes);
            }

            // Display reschedule details if the status is RESCHEDULE
            if ("RESCHEDULE".equalsIgnoreCase(status)) {
                System.out.printf(" - Rescheduled to: %s at %s | Message: %s", rescheduleDate, rescheduleTime,
                        rescheduleMessage);
            }

            System.out.println(); // Newline after each appointment
        }

        return;
    }

    /**
     * Prompts the user to accept or decline an appointment for the given doctor ID.
     * It displays the pending or rescheduled appointments for the doctor, asks the
     * user
     * to select an appointment, prompts the user to accept or decline the
     * appointment,
     * and then updates the status and adds notes in the data.
     * 
     * @param doctorId the doctor ID to accept or decline appointments for
     */
    public void acceptDeclineAppointment(String doctorId) {
        Scanner scanner = new Scanner(System.in);
        String decision, appointmentId, choice;
        String selectedAppointment = null;

        // Step 1: Display pending or rescheduled appointments for the doctor
        List<String> appointmentRequests = getAppointmentRequests(doctorId);
        if (appointmentRequests.isEmpty()) {
            return;
        }

        // Step 2: Get the appointment ID from the user
        System.out.println("Select an appointment to accept/decline (press 0 to return): ");

        while (true) {
            choice = scanner.nextLine();

            // Check if the user wants to exit
            if (choice.equals("0")) {
                System.out.println("Returning to the previous menu.");
                return;
            } else if (Integer.parseInt(choice) > appointmentRequests.size()) {
                System.out.println("Invalid option. Please try again");
                continue;
            }

            selectedAppointment = appointmentRequests.get(Integer.parseInt(choice) - 1);
            break;
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

        if (decision.equals("accept")) {
            if (fields[5].equals("RESCHEDULE")) {
                fields[5] = "BOOKED";
                fields[3] = fields[7];
                fields[4] = fields[8];
                fields[7] = "-";
                fields[8] = "-";
                fields[9] = "-";
            } else {
                fields[5] = "BOOKED";
            }
        } else {
            if (fields[5].equals("RESCHEDULE")) {
                fields[5] = "BOOKED";
                fields[6] = "Reschdule appointment declined";
                fields[7] = "-";
                fields[8] = "-";
                fields[9] = "-";
            } else {
                fields[2] = "-";
                fields[6] = "Appointment request declined";
                fields[5] = "AVAILABLE";
            }

        }

        String updatedData = String.join("|", fields);

        // Step 7: Update the file
        FileUtils.updateToFile(APPOINTMENT_FILE, updatedData, fields[0]);
        System.out.println("Appointment " + (decision.equals("accept") ? "accepted" : "declined") + " successfully.");
    }

    /**
     * Displays the pending or rescheduled appointment requests for a given doctor,
     * allows the user to accept or decline the appointments,
     * and updates the appointment status and notes accordingly.
     *
     * This method retrieves pending appointment requests for the specified doctor
     * and presents them to the user for review. The user can then choose to accept
     * or decline each appointment, and the appointment data is updated based on
     * the user's decision. After processing, the method pauses the output.
     *
     * @param doctorId the ID of the doctor whose appointment requests are being
     *                 viewed
     */
    public void viewAppointmentRequest(String doctorId) {

        printAppointmentRequest(doctorId);
        acceptDeclineAppointment(doctorId);
        PrintUtils.pause();

        return;
    }

    /**
     * Displays the upcoming appointments for a given doctor.
     *
     * This method retrieves the list of upcoming booked appointments for the
     * specified
     * doctor from the appointment file. It displays each appointment's details,
     * including appointment ID, patient ID, date, time, status, and consultation
     * notes
     * if available. If there are no upcoming appointments, it informs the user.
     *
     * @param doctorId the ID of the doctor whose upcoming appointments are being
     *                 viewed
     */
    public void viewUpcomingAppointments(String doctorId) {
        List<String> upcomingAppointments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");

                // Check if the appointment is booked and belongs to the specified doctor
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
        System.out.println("Upcoming appointments for Doctor ID: " + doctorId + ":");
        for (String appointment : upcomingAppointments) {
            String[] fields = appointment.split("\\|");

            // Extract details
            String appointmentId = fields[0];
            String patientId = fields[2];
            LocalDate date = LocalDate.parse(fields[3], dateFormatter);
            LocalTime time = LocalTime.parse(fields[4], timeFormatter);
            String status = fields[5];
            String consultationNotes = fields[6];

            // Format the date and time
            String formattedDate = date.format(dateFormatter);
            String formattedTime = time.format(timeFormatter);

            // Display appointment details
            System.out.printf("%s / %s: %s %s (%s)", appointmentId, patientId, formattedDate, formattedTime, status);

            // Show consultation notes if the status is BOOKED and notes are available
            if ("BOOKED".equalsIgnoreCase(status) && !consultationNotes.equals("-")) {
                System.out.printf(" - %s", consultationNotes);
            }

            System.out.println(); // Newline after each appointment
        }

        PrintUtils.pause();
    }

    /**
     * Displays all appointments in the appointment file.
     *
     * This method retrieves the list of all appointments from the appointment file
     * and displays each appointment's details, including appointment ID, doctor ID,
     * patient ID, date, time, status, request message, reschedule date, reschedule
     * time, and reschedule message. Appointments with invalid or missing details
     * are
     * skipped. After displaying all appointments, the method prompts the user for a
     * patient ID to view the completed appointment outcome details.
     */
    public void displayDoctorAppointmentDetails() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          View All Appointments         ║");
        System.out.println("╚════════════════════════════════════════╝");

        System.out.printf("%-16s %-12s %-12s %-12s %-8s %-12s %-32s %-18s %-18s %-32s%n",
                "Appointment ID", "Doctor ID", "Patient ID", "Date", "Time", "Status", "Request Message",
                "Reschedule Date", "Reschedule Time", "Reschedule Message");
        System.out.println(
                "═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length < 10) {
                    System.out.println("Skipping appointment with invalid number of fields: " + line);
                    continue;
                }
                String appointmentId = fields[0];
                String doctorId = fields[1];
                String patientId = fields[2];
                LocalDate date;
                LocalTime time;
                String status;
                String requestMessage;
                String rescheduleDate;
                String rescheduleTime;
                String rescheduleMessage;
                try {
                    date = LocalDate.parse(fields[3], dateFormatter);
                    time = LocalTime.parse(fields[4], timeFormatter);
                    status = fields[5];
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
        System.out.print(
                "\nEnter Patient ID to view completed (Status: CLOSED) appointment outcome details (or 0 to exit): ");
        String patientId = scanner.nextLine().trim();

        if (patientId.equals("0")) {
            System.out.println("Exiting to main menu...");
            return;
        }

        if (patientId.isEmpty()) {
            System.out.println("Please enter a valid patient ID.");
            return;
        }

        AppointmentOutcomeController appointmentOutcomeController = new AppointmentOutcomeController();
        appointmentOutcomeController.displayAppointmentOutcomesByPatientId(patientId);
    }

}
