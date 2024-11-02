package entity;

public class MedicalRecord {

    private String medicalRecordId;
    private String doctorId;
    private String patientId;
    private String diagnosis;
    private String treatment;

    // Constructor
    public MedicalRecord(String medicalRecordId, String doctorId, String patientId, String diagnosis, String treatment) {
        this.medicalRecordId = medicalRecordId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    // Getters
    public String getMedicalRecordId() {
        return medicalRecordId;
    }

    public String getDoctorId() {
        return doctorId;
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
    public void setMedicalRecordId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
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
        return "Medical Record : \n" +
                "Medical Record Id: " + medicalRecordId + '\n' +
                "Doctor Id: " + patientId+ '\n' + 
                "Patient Id: " + patientId + '\n' + 
                "Diagnosis:" + diagnosis + '\n' + 
                "Treatment:" + treatment +'\n'  ;
    }
}
