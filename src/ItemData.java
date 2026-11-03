public class ItemData {
    public enum TYPE {FOOD, MEDICINE, MISC}

    private final String name;
    private final TYPE type;
    private int stock;

    public ItemData(String name, TYPE type, int stock) {
        this.name = name;
        this.type = type;
        this.stock = stock;
    }

    public String getName() {return name;}
    public TYPE getType() {return type;}
    public int getStock() {return stock;}

    public void setStock(int stock) {this.stock = stock;}
}

class MedicineItem extends ItemData {
    private final String expiry;

    public MedicineItem(String name, int stock, String expiry) {
        super(name, TYPE.MEDICINE, stock);
        this.expiry = expiry;
    }

    public String getExpiry() {
        return expiry;
    }
}

class MiscellaneousItem extends ItemData {
    public MiscellaneousItem(String name, int stock) {
        super(name, TYPE.MISC, stock);
    }
}

class FoodItem extends ItemData {
    private final String expiry;
    private final String animals;

    public FoodItem(String name, int stock, String expiry, String animals) {
        super(name, TYPE.FOOD, stock);
        this.expiry = expiry;
        this.animals = animals;
    }

    public String getExpiry() {return expiry;}
    public String getAnimals() {
        return animals;
    }
}
