package entity;

public class MedicalRecord {

    private String patientId;
    private String diagnosis;
    private String treatment;

    // Constructor
    public MedicalRecord(String patientId, String diagnosis, String treatment) {
        this.patientId = patientId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    // Getters
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
        return "MedicalRecord: \n" +
                "patientId='" + patientId + '\'' +
                ", diagnosis=" + diagnosis +
                ", treatment=" + treatment;
    }
}
