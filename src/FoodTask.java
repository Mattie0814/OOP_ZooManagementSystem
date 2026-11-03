import java.time.LocalDate;

public class FoodTask extends TaskData {
    private final String animalSpecies;
    private final String enclosureZone;
    private final String dietNotes;

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
