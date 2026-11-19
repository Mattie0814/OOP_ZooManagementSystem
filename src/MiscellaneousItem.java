public class MiscellaneousItem extends ItemData {
    public MiscellaneousItem(String name, int stock, String dateProcured, String ID, int autoUpdate) {
        super(name, TYPE.MISC, stock, dateProcured, ID, autoUpdate);
    }
    
    public static String miscellaneousID() {return generateID(TYPE.MISC);}
}
