package entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    private String appointmentId;                  // Unique identifier for the appointment
    private String doctorId;                       // Doctor assigned to the appointment
    private String patientId = "-";                 // Patient assigned to the appointment, defaults to empty
    private LocalDate date;               // Date of the appointment
    private LocalTime time;               // Time of the appointment
    private Status status = Status.AVAILABLE;           // Default status as AVAILABLE
    private String requestMessage = "-";            // Message for additional requests, defaults to empty
    private String rescheduleDate = "-";         // Reschedule date if applicable
    private String rescheduleTime = "-";         // Reschedule time if applicable
    private String rescheduleMessage = "-";      // Reschedule message if applicable

    // Enum for appointment status
    public enum Status {
        AVAILABLE, PENDING, BOOKED, RESCHEDULE;
    }

    // Constructor to initialize an Appointment object with essential details
    public Appointment(String appointmentId, String doctorId, LocalDate date, LocalTime time) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
    }

    // String representation of the appointment object
    @Override
    public String toString() {
        return "Appointment ID: " + appointmentId
                + "\nDoctor ID: " + doctorId
                + "\nPatient ID: " + patientId
                + "\nDate: " + date
                + "\nTime: " + time
                + "\nStatus: " + status;
    }

    // Getters and setters for appointment details
    public String getAppointmentId() {
        return appointmentId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }
}
