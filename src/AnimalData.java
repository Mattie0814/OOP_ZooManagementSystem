public class AnimalData {
    private String name;
    private String species;
    private String gender;
    private String birthDate;
    private String healthStatus;
    private String imagePath;

    public AnimalData(String name, String species, String gender, String birthDate, String healthStatus) {
        this.name = name;
        this.species = species;
        this.gender = gender;
        this.birthDate = birthDate;
        this.healthStatus = healthStatus;
    }

    public void setName(String name){
        if (name.isEmpty()){
            return;
        }

        this.name = name;
    }

    public String getName() { return name; }

    public void setSpecies(String species){
        if (species.isEmpty()){
            return;
        }

        this.species = species;
    }

    public String getSpecies() { return species; }

    public void setGender(String gender){
        if (gender.isEmpty()){
            return;
        }

        this.gender = gender;
    }

    public String getGender() { return gender; }

    public void setBirthDate(String birthDate){
        if (birthDate.isEmpty()){
            return;
        }

        this.birthDate = birthDate;
    }

    public String getBirthDate() { return birthDate; }

    public void setHealthStatus(String healthStatus){
        if (healthStatus.isEmpty()){
            return;
        }

        this.healthStatus = healthStatus;
    }

    public String getHealthStatus() { return healthStatus; }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }


}