import java.time.LocalDate;

class TaskData {
    private String taskName;
    private String taskTime;
    private LocalDate startDate;
    private String recurrence; // NONE, DAILY, WEEKLY, MONTHLY, INTERVAL

    public TaskData(String taskName, String taskTime, LocalDate startDate, String recurrence) {
        this.taskName = taskName;
        this.taskTime = taskTime;
        this.startDate = startDate;
        this.recurrence = recurrence;
    }

    public String getTaskName() { return taskName; }
    public String getTaskTime() { return taskTime; }
    public LocalDate getStartDate() { return startDate; }
    public String getRecurrence() { return recurrence; }

    public String getType() {
        return "General";
    }

    public String getDetails() {
        return "No additional details.";
    }

    public boolean occursOn(LocalDate date) {
        if (date.isBefore(startDate)) return false;
        switch (recurrence) {
            case "DAILY":
                return true;
            case "WEEKLY":
                return startDate.getDayOfWeek() == date.getDayOfWeek();
            case "MONTHLY":
                return startDate.getDayOfMonth() == date.getDayOfMonth();
            default:
                return startDate.equals(date);
        }
    }

    @Override
    public String toString() {
        return "[" + getType() + "] " + taskName + " (" + taskTime + ")";
    }
}

class FoodTask extends TaskData {
    private String animalSpecies;
    private String enclosureZone;
    private String dietNotes;

    public FoodTask(String taskName, String taskTime, LocalDate startDate, String recurrence,
                    String animalSpecies, String enclosureZone, String dietNotes) {
        super(taskName, taskTime, startDate, recurrence);
        this.animalSpecies = animalSpecies;
        this.enclosureZone = enclosureZone;
        this.dietNotes = dietNotes;
    }

    public String getAnimalSpecies() { return animalSpecies; }
    public String getEnclosureZone() { return enclosureZone; }
    public String getDietNotes() { return dietNotes; }

    @Override
    public String getType() {
        return "Feeding & Diet";
    }

    @Override
    public String getDetails() {
        return "Species: " + animalSpecies + " | Zone: " + enclosureZone + " | Diet: " + dietNotes;
    }
}

class MedicineTask extends TaskData {
    private String animalID;
    private String dosage;
    private String administeredBy;

    public MedicineTask(String taskName, String taskTime, LocalDate startDate, String recurrence,
                        String animalID, String dosage, String administeredBy) {
        super(taskName, taskTime, startDate, recurrence);
        this.animalID = animalID;
        this.dosage = dosage;
        this.administeredBy = administeredBy;
    }

    public String getAnimalID() { return animalID; }
    public String getDosage() { return dosage; }
    public String getAdministeredBy() { return administeredBy; }

    @Override
    public String getType() {
        return "Vet Care & Meds";
    }

    @Override
    public String getDetails() {return "AnimalData ID: " + animalID + " | Dosage: " + dosage + " | Staff: " + administeredBy;}
}

class MiscellaneousTask extends TaskData {
    private String habitatLocation;
    private String safetyRiskLevel; // Low, Medium, High (Requires AnimalData Relocation)
    private String equipmentNeeded;

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
        return "Habitat MiscellaneousItem";
    }

    @Override
    public String getDetails() {return "Location: " + habitatLocation + " | Risk Level: " + safetyRiskLevel + " | Tools: " + equipmentNeeded;}
}