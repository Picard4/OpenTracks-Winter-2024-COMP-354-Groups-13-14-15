package de.dennisguse.opentracks;
import java.time.LocalDate;

//public class UserMaintenanceData {
//    private int sharpeningInterval;
//    private double baseAngle;
//    private double edgeAngle;
//    private LocalDate lastSharpeningDate;
//    private int waxingInterval;
//    private String waxType;
//    private LocalDate lastWaxingDate;
//
//    // default constructor
//    public UserMaintenanceData() {
//        this.sharpeningInterval = 0;
//        this.baseAngle = 0.0;
//        this.edgeAngle = 0.0;
//        this.lastSharpeningDate = lastSharpeningDate;
//        this.waxingInterval = waxingInterval;
//        this.waxType = waxType;
//        this.lastWaxingDate = lastWaxingDate;
//    }

    // Constructor
    public UserMaintenanceData(int sharpeningInterval, double baseAngle, double edgeAngle, LocalDate lastSharpeningDate, int waxingInterval, String waxType, LocalDate lastWaxingDate) {
        this.sharpeningInterval = sharpeningInterval;
        this.baseAngle = baseAngle;
        this.edgeAngle = edgeAngle;
        this.lastSharpeningDate = lastSharpeningDate;
        this.waxingInterval = waxingInterval;
        this.waxType = waxType;
        this.lastWaxingDate = lastWaxingDate;
    }

    // Getters and Setters
    public int getSharpeningInterval() {
        return sharpeningInterval;
    }

    public void setSharpeningInterval(int sharpeningInterval) {
        this.sharpeningInterval = sharpeningInterval;
    }

    public double getBaseAngle() {
        return baseAngle;
    }

    public void setBaseAngle(double baseAngle) {
        this.baseAngle = baseAngle;
    }

    public double getEdgeAngle() {
        return edgeAngle;
    }

    public void setEdgeAngle(double edgeAngle) {
        this.edgeAngle = edgeAngle;
    }

    public LocalDate getLastSharpeningDate() {
        return lastSharpeningDate;
    }

    public void setLastSharpeningDate(LocalDate lastSharpeningDate) {
        this.lastSharpeningDate = lastSharpeningDate;
    }

    public int getWaxingInterval() {
        return waxingInterval;
    }

    public void setWaxingInterval(int waxingInterval) {
        this.waxingInterval = waxingInterval;
    }
public String getWaxType() {
        return waxType;
    }

    public void setWaxType(String waxType) {
        this.waxType = waxType;
    }

    public LocalDate getLastWaxingDate() {
        return lastWaxingDate;
    }

    public void setLastWaxingDate(LocalDate lastWaxingDate) {
        this.lastWaxingDate = lastWaxingDate;
    }

}
