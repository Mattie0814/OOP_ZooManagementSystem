class AnimalData {
    private String name;
    private String species;
    private String gender;
    private String birthDate;
    private String healthStatus;
    private String type;

    public AnimalData(String name, String species, String gender, String birthDate,
                      String type, String healthStatus) {
        this.name = name;
        this.species = species;
        this.gender = gender;
        this.type = type;
        this.birthDate = birthDate;
        this.healthStatus = healthStatus;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getType() {return type;}

    public String getHealthStatus() {
        return healthStatus;
    }

    public String getAnimalType() {
        return "AnimalData";
    }
}