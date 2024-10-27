package entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment { // tentative

    private static int idCounter = 1;        // Static counter for auto-incrementing ID
    private String appointmentId;            // Unique identifier for the appointment
    private String doctorId;                 // Doctor assigned to the appointment
    private LocalDate scheduledDate;         // Date of the appointment
    private LocalTime scheduledTime;         // Time of the appointment
    private String status;                   // Current status, e.g., "Scheduled", "Completed", "Cancelled"
    private String roomNumber;               // Room or location where the appointment is scheduled

    // Constructor (with auto-generated appointment ID)
    public Appointment(String doctorId, LocalDate scheduledDate, LocalTime scheduledTime, String status, String roomNumber) {
        this.appointmentId = generateAppointmentId(); // Auto-generate ID
        this.doctorId = doctorId;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.roomNumber = roomNumber;
    }

    // Method to generate appointment ID
    private static String generateAppointmentId() {
        return String.format("AP%05d", idCounter++);  // AP followed by a zero-padded 5-digit number
    }
    /*
    // Example toString method for displaying appointment details
    @Override
    public String toString() {
        return "Appointment ID: " + appointmentId
                + "\nDoctor: " + doctor.getName() dwadaw dwad wa
                + "\nScheduled Date: " + scheduledDate
                + "\nScheduled Time: " + scheduledTime
                + "\nStatus: " + status
                + "\nRoom Number: " + roomNumber;
    }*/
}
