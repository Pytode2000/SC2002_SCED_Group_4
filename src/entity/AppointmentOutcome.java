package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentOutcome {

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDate dateOfAppointment;
    private String serviceType;
    private List<String> prescribedMedications;
    private String consultationNotes;

    // Constructor for new appointment outcome creation
    public AppointmentOutcome(String appointmentId, String patientId, String doctorId, LocalDate dateOfAppointment, String serviceType, String consultationNotes) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateOfAppointment = dateOfAppointment;
        this.serviceType = serviceType;
        this.prescribedMedications = new ArrayList<>();
        this.consultationNotes = consultationNotes;
    }

    // Overloaded constructor to initialize with prescribed medications
    public AppointmentOutcome(String appointmentId, String patientId, String doctorId, LocalDate dateOfAppointment, String serviceType, List<String> prescribedMedications, String consultationNotes) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateOfAppointment = dateOfAppointment;
        this.serviceType = serviceType;
        this.prescribedMedications = prescribedMedications;
        this.consultationNotes = consultationNotes;
    }

    // Getter for doctor ID
    public String getDoctorId() {
        return this.doctorId;
    }

    // Getter for patient ID
    public String getPatientId() {
        return this.patientId;
    }

    // Getter for appointment ID
    public String getAppointmentId() {
        return this.appointmentId;
    }

    // Getter for date of appointment
    public LocalDate getDateOfAppointment() {
        return this.dateOfAppointment;
    }

    // Getter for service type
    public String getServiceType() {
        return this.serviceType;
    }

    // Getter for consultation notes
    public String getConsultationNotes() {
        return this.consultationNotes;
    }

    // Getter for prescribed medications
    public List<String> getPrescribedMedications() {
        return this.prescribedMedications;
    }

    // Setter for service type
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType == null || serviceType.trim().isEmpty() ? "-" : serviceType;
    }

    // Setter for consultation notes
    public void setConsultationNotes(String consultationNotes) {
        this.consultationNotes = consultationNotes == null || consultationNotes.trim().isEmpty() ? "-" : consultationNotes;
    }
}
