package entity;

public class Medicine {
    private String medicineId; // Changed from int to String
    private String name;
    private String description;
    private int stockLevel;
    private int lowStockLevel; // Threshold for low stock
    private boolean lowStockLevelAlert; // Flag for when stock is low
    private String status; // Medicine status can be "Available", "Low Stock", "Pending Replenishment"
    private String medicineType; // New attribute for type of item

    // Constructor
    public Medicine(String medicineId, String name, String description, int stockLevel, int lowStockLevel,
            String medicineType) {
        this.medicineId = medicineId;
        this.name = name;
        this.description = description;
        this.stockLevel = stockLevel;
        this.lowStockLevel = lowStockLevel;
        this.medicineType = medicineType; // Initialize medicineType
        updateLowStockAlert();
    }

    // Getters and Setters
    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
        updateLowStockAlert();
    }

    public int getLowStockLevel() {
        return lowStockLevel;
    }

    public void setLowStockLevel(int lowStockLevel) {
        this.lowStockLevel = lowStockLevel;
        updateLowStockAlert();
    }

    public boolean isLowStockLevelAlert() {
        return lowStockLevelAlert;
    }

    public String getStatus() {
        return status;
    }

    public void setPendingReplenishmentRequest() {
        this.status = "Pending Replenishment Request";
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    // Method to update the low stock alert flag and status
    private void updateLowStockAlert() {
        this.lowStockLevelAlert = this.stockLevel <= this.lowStockLevel;
        if (this.lowStockLevelAlert) {
            this.status = "Low Stock";
        } else {
            this.status = "Available";
        }
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "medicineId='" + medicineId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", stockLevel=" + stockLevel +
                ", lowStockLevel=" + lowStockLevel +
                ", lowStockLevelAlert=" + lowStockLevelAlert +
                ", status='" + status + '\'' +
                ", medicineType='" + medicineType + '\'' +
                '}';
    }
}
