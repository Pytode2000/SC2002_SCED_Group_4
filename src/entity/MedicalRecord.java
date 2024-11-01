package entity;

public class MedicalRecord {

    private String medicalRecordId;
    private String patientId;
    private String diagnosis;
    private String treatment;

    // Constructor
    public MedicalRecord(String medicalRecordId, String patientId, String diagnosis, String treatment) {
        this.medicalRecordId = medicalRecordId;
        this.patientId = patientId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    // Getters
    public String getMedicalRecordId() {
        return medicalRecordId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    // Setters
    public void medicalRecordIdId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    // toString method
    @Override
    public String toString() {
        return "MedicalRecord : " +
                "medicalRecordId=" + medicalRecordId + '\n' +
                "patientId=" + patientId + 
                ", diagnosis=" + diagnosis +
                ", treatment=" + treatment;
    }
}
