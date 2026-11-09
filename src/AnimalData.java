public class AnimalData {
    private final String name;
    private final String species;
    private final String gender;
    private final String birthDate;
    private final String enclosureId;
    private final String healthStatus;

    public AnimalData(String name, String species, String gender, String birthDate,
                      String enclosureId, String healthStatus) {
        this.name = name;
        this.species = species;
        this.gender = gender;
        this.birthDate = birthDate;
        this.enclosureId = enclosureId;
        this.healthStatus = healthStatus;
    }

    public String getName() { return name; }

    public String getSpecies() { return species; }

    public String getGender() { return gender; }

    public String getBirthDate() { return birthDate; }

    public String getEnclosureId() { return enclosureId; }

    public String getHealthStatus() { return healthStatus; }

    @Override
    public String toString() {
        return name;
    }
}