public class FoodItem extends ItemData {
    private final String expiry;
    private final String animals;

    public FoodItem(String name, int stock, String expiry, String animals) {
        super(name, TYPE.FOOD, stock);
        this.expiry = expiry;
        this.animals = animals;
    }

    public String getExpiry() { return expiry; }
    public String getAnimals() { return animals; }
}
