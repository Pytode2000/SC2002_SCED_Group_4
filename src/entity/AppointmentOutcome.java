package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentOutcome {

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDate dateOfAppointment;       // Date of the appointment
    private String serviceType;                // Type of service provided (e.g., consultation, X-ray, blood test)
    private List<String> prescribedMedications;  // List of prescribed medications
    private String consultationNotes;          // Consultation notes

    // Constructor
    public AppointmentOutcome(String appointmentId, String patientId, String doctorId, LocalDate dateOfAppointment, String serviceType, String consultationNotes) {
        this.appointmentId = appointmentId; // get from appointment
        this.patientId = patientId; // get from appointment
        this.doctorId = doctorId; // get from appointment
        this.dateOfAppointment = dateOfAppointment; // get from appointment
        this.serviceType = serviceType;
        this.prescribedMedications = new ArrayList<>(); // empty medicine
        this.consultationNotes = consultationNotes;
    }

    // another constructor for when we pull from appointmentOutcome.txt
    // Overloaded Constructor with prescribedMedications
    public AppointmentOutcome(String appointmentId, String patientId, String doctorId, LocalDate dateOfAppointment, String serviceType, List<String> prescribedMedications, String consultationNotes) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateOfAppointment = dateOfAppointment;
        this.serviceType = serviceType;
        this.prescribedMedications = prescribedMedications;
        this.consultationNotes = consultationNotes;
    }

    public String getDoctorId() {
        return this.doctorId;
    }

    public String getPatientId() {
        return this.patientId;
    }

    public String getAppointmentId() {
        return this.appointmentId;
    }

    public LocalDate getDateOfAppointment() {
        return this.dateOfAppointment;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public String getConsultationNotes() {
        return this.consultationNotes;
    }

    public List<String> getPrescribedMedications() {
        return this.prescribedMedications;
    }

    // Setter for Service Type
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType == null || serviceType.trim().isEmpty() ? "-" : serviceType;
    }

    // Setter for Consultation Notes
    public void setConsultationNotes(String consultationNotes) {
        this.consultationNotes = consultationNotes == null || consultationNotes.trim().isEmpty() ? "-" : consultationNotes;
    }

}
