package controller;

import entity.Appointment.Status;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AppointmentController {

    private static final String APPOINTMENT_FILE = "data/appointment.txt";

    // Method to retrieve, display, and allow selection of AVAILABLE appointments
    public void displayAndSelectAvailableAppointments() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<String> availableAppointments = new ArrayList<>();
        System.out.println("\nAvailable Appointments:");
        System.out.println("-----------------------");

        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENT_FILE))) {
            String line;
            int index = 1;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");

                // Check if the appointment status is AVAILABLE
                if (fields.length >= 6 && fields[5].equals("AVAILABLE")) {
                    LocalDate date = LocalDate.parse(fields[3], dateFormatter);
                    LocalTime time = LocalTime.parse(fields[4], timeFormatter);

                    // Display date, time with an index for selection
                    System.out.printf("%d. Date: %s | Time: %s%n", index, date.format(dateFormatter), time.format(timeFormatter));

                    // Store the line for reference by index
                    availableAppointments.add(line);
                    index++;
                }
            }

            // If no available appointments, notify and exit
            if (availableAppointments.isEmpty()) {
                System.out.println("No available appointments found.");
                return;
            }

            System.out.println("0. Back to Main Menu");
            System.out.println("-----------------------");

            // Prompt user to select an appointment by index
            Scanner scanner = new Scanner(System.in);
            System.out.print("Please enter the number of the appointment you wish to select (or 0 to exit): ");
            int selection = scanner.nextInt();

            // Check for exit option
            if (selection == 0) {
                System.out.println("Returning to main menu...");
                return;
            }

            // Validate selection and display chosen appointment
            if (selection > 0 && selection <= availableAppointments.size()) {
                String chosenAppointment = availableAppointments.get(selection - 1);
                System.out.println("You have selected the following appointment:");
                System.out.println(chosenAppointment);
            } else {
                System.out.println("Invalid selection. Please try again.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error processing appointments, exiting back to main menu...");
        }
    }
}
