public class MedicineItem extends ItemData {
    private final String expiry;

    public MedicineItem(String name, int stock, String expiry) {
        super(name, TYPE.MEDICINE, stock);
        this.expiry = expiry;
    }

    public String getExpiry() { return expiry; }
}
