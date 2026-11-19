public class FoodItem extends ItemData {
    private String animals;

    public FoodItem(String name, int stock, String dateProcured, String animals, String ID, int autoUpdate) {
        super(name, TYPE.FOOD, stock, dateProcured, ID, autoUpdate);
        this.animals = animals;
    }

    public static String foodID() {return generateID(TYPE.FOOD);}
    public String getAnimals() { return animals; }
    
    public void setAnimals(String animals) {this.animals = animals;}
}
