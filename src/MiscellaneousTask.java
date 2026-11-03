import java.time.LocalDate;

public class MiscellaneousTask extends TaskData {
    private final String habitatLocation;
    private final String safetyRiskLevel; // Low, Medium, High (Requires Animal Relocation)
    private final String equipmentNeeded;

    public MiscellaneousTask(String taskName, String taskTime, LocalDate startDate, String recurrence,
                             String habitatLocation, String safetyRiskLevel, String equipmentNeeded) {
        super(taskName, taskTime, startDate, recurrence);
        this.habitatLocation = habitatLocation;
        this.safetyRiskLevel = safetyRiskLevel;
        this.equipmentNeeded = equipmentNeeded;
    }

    public String getHabitatLocation() { return habitatLocation; }
    public String getSafetyRiskLevel() { return safetyRiskLevel; }
    public String getEquipmentNeeded() { return equipmentNeeded; }

    @Override
    public String getType() {
        return "Habitat Maintenance";
    }

    @Override
    public String getDetails() {
        return "Location: " + habitatLocation + " | Risk Level: " + safetyRiskLevel + " | Tools: " + equipmentNeeded;
    }
}
