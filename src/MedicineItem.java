public class MedicineItem extends ItemData {
    public MedicineItem(String name, int stock, String dateProcured, String ID, int autoUpdate) {
        super(name, TYPE.MEDICINE, stock, dateProcured, ID, autoUpdate);
    }

    public static String medicineID() {return generateID(TYPE.MEDICINE);}
}
