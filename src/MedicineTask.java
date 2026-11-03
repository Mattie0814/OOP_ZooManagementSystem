import java.time.LocalDate;

public class MedicineTask extends TaskData {
    private final String animalID;
    private final String dosage;
    private final String administeredBy;

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
    public String getDetails() {
        return "Animal ID: " + animalID + " | Dosage: " + dosage + " | Staff: " + administeredBy;
    }
}
