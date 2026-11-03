import java.util.ArrayList;

public class SaveData {
    private ArrayList<UserData> users;
    private ArrayList<EnclosureData> enclosures;
    private ArrayList<ItemData> items;

    public SaveData() {
        users = new ArrayList<>();
        enclosures = new ArrayList<>();
        items = new ArrayList<>();
    }

    //User List
    public ArrayList<UserData> getUsers() {
        return users;
    }

    public void addUser(UserData user) {
        users.add(user);
    }

    public void removeUser(UserData user) {
        users.remove(user);
    }

    //Enclosure List
    public ArrayList<EnclosureData> getEnclosures() {
        return enclosures;
    }

    public void addEnclosure(EnclosureData enclosure) {
        enclosures.add(enclosure);
    }

    public void removeEnclosure(EnclosureData enclosure) {
        enclosures.remove(enclosure);
    }

    //Item List
    public ArrayList<ItemData> getItems() {
        return items;
    }

    public void addItem(ItemData item) {
        items.add(item);
    }

    public void removeItem(ItemData item) {
        items.remove(item);
    }
}