package entity;

public class MedicalRecord {

    private String medicalRecordId; // Unique identifier for the medical record
    private String doctorId;        // ID of the doctor associated with this record
    private String patientId;       // ID of the patient associated with this record
    private String diagnosis;       // Diagnosis details for the medical record
    private String treatment;       // Treatment details for the medical record

    // Constructor to initialize the medical record
    public MedicalRecord(String medicalRecordId, String doctorId, String patientId, String diagnosis, String treatment) {
        this.medicalRecordId = medicalRecordId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    // Getter for medical record ID
    public String getMedicalRecordId() {
        return medicalRecordId;
    }

    // Getter for doctor ID
    public String getDoctorId() {
        return doctorId;
    }

    // Getter for patient ID
    public String getPatientId() {
        return patientId;
    }

    // Getter for diagnosis details
    public String getDiagnosis() {
        return diagnosis;
    }

    // Getter for treatment details
    public String getTreatment() {
        return treatment;
    }

    // Setter for medical record ID
    public void setMedicalRecordId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    // Setter for doctor ID
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    // Setter for patient ID
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    // Setter for diagnosis details
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    // Setter for treatment details
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    // Returns a string representation of the medical record
    @Override
    public String toString() {
        return "Medical Record:\n"
                + "Medical Record ID: " + medicalRecordId + '\n'
                + "Doctor ID: " + doctorId + '\n'
                + "Patient ID: " + patientId + '\n'
                + "Diagnosis: " + diagnosis + '\n'
                + "Treatment: " + treatment + '\n';
    }
}
