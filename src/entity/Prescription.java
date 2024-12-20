package entity;

public class Prescription {

    private String prescriptionId;  // Unique identifier for the prescription
    private String medicineId;      // Links to a Medicine object by ID
    private int quantity;           // Quantity of medicine prescribed
    private Status status;          // Enum for prescription status

    // Enum for status options (PENDING or DISPENSED)
    public enum Status {
        PENDING,  // Prescription is pending
        DISPENSED // Prescription has been dispensed
    }

    // Constructor to initialize prescription details
    public Prescription(String prescriptionId, String medicineId, int quantity, Status status) {
        this.prescriptionId = prescriptionId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.status = status;
    }

    // Getters and Setters for prescription attributes
    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // Override toString method to return a formatted string of prescription details
    @Override
    public String toString() {
        return "Prescription{"
                + "prescriptionId='" + prescriptionId + '\''
                + ", medicineId='" + medicineId + '\''
                + ", quantity=" + quantity
                + ", status=" + status
                + '}';
    }
}
