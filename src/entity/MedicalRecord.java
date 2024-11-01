package entity;

import java.util.List;

public class MedicalRecord {

    private String patientId;
    private String allergy;
    private List<String> appointmentOutcomeId;
    private String notes;

    // Constructor
    public MedicalRecord(String patientId, String allergies, List<String> appointmentOutcomeId, String notes) {
        this.patientId = patientId;
        this.allergy = allergies;
        this.appointmentOutcomeId = appointmentOutcomeId;
        this.notes = notes;
    }

    // Getters
    public String getPatientId() {
        return patientId;
    }

    public String getAllergy() {
        return allergy;
    }

    public List<String> getAppointmentOutcomeId() {
        return appointmentOutcomeId;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public void setAppointmentOutcomeId(List<String> appointmentOutcomeId) {
        this.appointmentOutcomeId = appointmentOutcomeId;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // toString method
    @Override
    public String toString() {
        return "MedicalRecord: \n" +
                "patientId='" + patientId + '\'' +
                ", allergy=" + allergy +
                ", appointmentOutcomeId=" + appointmentOutcomeId +
                ", notes=" + notes ;
    }
}
