import java.util.concurrent.ThreadLocalRandom;

public class ItemData {
    public enum TYPE {FOOD, MEDICINE, MISC}

    private String name;
    private final TYPE type;
    private int stock;
    private String dateProcured;
    private String ID;
    private int autoUpdate;

    public ItemData(String name, TYPE type, int stock, String dateProcured, String ID, int autoUpdate) {
        this.name = name;
        this.type = type;
        this.stock = stock;
        this.dateProcured = dateProcured;
        this.ID = ID;
        this.autoUpdate = autoUpdate;
    }

    public static String generateID(TYPE type) {
    	int length = 6;
    	StringBuilder sb = new StringBuilder(length+2);
	    for(int i = 0; i<length; i++) {
	    	int digit = ThreadLocalRandom.current().nextInt(10);
	    	sb.append(digit);
    	}
    	if(type == TYPE.FOOD) {
    		sb.insert(0, "FO");
    	}
    	else if(type == TYPE.MEDICINE) {
    		sb.insert(0, "ME");
    	}
    	else {
    		sb.insert(0, "MI");
    	}
    	String result = sb.toString();
    	return result;
    }
    
    public String getName() {return name;}
    public TYPE getType() {return type;}
    public int getStock() {return stock;}
    public String getDateProcured() {return dateProcured;}
    public String getID() {return ID;}
    public int getAutoUpdate() {return autoUpdate;}

    public void setName(String name) {this.name = name;}
    public void setStock(int stock) {
    	if(stock < 0)
    		return;
    	this.stock = stock;
    }
    public void setDateProcured(String dateProcured) {this.dateProcured = dateProcured;}
    public void setAutoUpdate(int autoUpdate) {
    	if(autoUpdate < 0)
    		return;
    	this.autoUpdate = autoUpdate;
    }

}
