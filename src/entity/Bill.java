package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Bill {

    private String appointmentId;
    private String patientId;
    private Status status;
    private double cost;
    private String datetime;

    public enum Status {
        PROCESSING,
        BILLED,
        PAID
    }

    // Constructor
    public Bill(String appointmentId, String patientId) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.cost = 0;
        this.status = Status.PROCESSING;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        this.datetime = LocalDateTime.now().format(formatter); // Formatted datetime
    }

    // Getters and Setters
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getDatetime() {
        return datetime;
    }

    @Override
    public String toString() {
        return "Bill{"
                + "appointmentId='" + appointmentId + '\''
                + ", patientId='" + patientId + '\''
                + ", status=" + status
                + ", cost=" + cost
                + ", datetime=" + datetime
                + '}';
    }
}
