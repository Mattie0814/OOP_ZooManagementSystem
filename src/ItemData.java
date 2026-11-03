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
