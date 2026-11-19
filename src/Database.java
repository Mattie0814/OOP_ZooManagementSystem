import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;

public class Database {
    private static final String USER_PATH = "src/data/users.json";
    private static final String INVENTORY_PATH = "src/data/inventory.json";
    private static final String ENCLOSURE_PATH = "src/data/enclosure.json";

    private static UserData activeUser;

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeHierarchyAdapter(TaskData.class, new TaskDataAdapter())
            .registerTypeHierarchyAdapter(ItemData.class, new ItemDataAdapter())
            .create();

    private static final Type USER_LIST_TYPE = new TypeToken<ArrayList<UserData>>() {}.getType();
    private static final Type ITEM_LIST_TYPE = new TypeToken<ArrayList<ItemData>>() {}.getType();
    private static final Type ENCLOSURE_LIST_TYPE = new TypeToken<ArrayList<EnclosureData>>() {}.getType();

    private static ArrayList<UserData> users = null;
    private static ArrayList<ItemData> inventory = null;
    private static ArrayList<EnclosureData> enclosures = null;

    public static void saveUsers(ArrayList<UserData> userList) {
        if (userList == null) {
            System.err.println("Cannot save null user list.");
            return;
        }

        users = userList;

        saveData(USER_PATH, users);
    }

    public static void loadUsers() {
        users = loadData(USER_PATH, USER_LIST_TYPE);

        activeUser = null;

        for (UserData user : users) {
            if (user.getIsActive()) {
                if (activeUser == null) {
                    activeUser = user;
                } else {
                    user.setIsActive(false);
                }
            }
        }
    }

    public static ArrayList<UserData> getUserList() {
        return users;
    }

    public static void setActiveUser(UserData user) {
        for (UserData prevUser : users) {
            prevUser.setIsActive(false);
        }

        if (user == null) {
            activeUser.setIsActive(false);
            activeUser = null;
        }
        else {
            user.setIsActive(true);
            activeUser = user;
        }

        saveUsers(users);
    }

    public static UserData getActiveUser() {
        return activeUser;
    }

    public static void saveInventory(ArrayList<ItemData> items) {
        if (items == null) {
            System.err.println("Cannot save null inventory.");
            return;
        }

        inventory = items;

        saveData(INVENTORY_PATH, inventory);
    }

    public static void loadInventory() {

        inventory = loadData(INVENTORY_PATH, ITEM_LIST_TYPE);
    }

    public static ArrayList<ItemData> getInventoryList() {
        return inventory;
    }

    public static void saveEnclosures(ArrayList<EnclosureData> enclosureList) {
        if (enclosureList == null) {
            System.err.println("Cannot save null enclosure list.");
            return;
        }

        enclosures = enclosureList;

        saveData(ENCLOSURE_PATH, enclosures);
    }

    public static void loadEnclosures() {

        enclosures = loadData(ENCLOSURE_PATH, ENCLOSURE_LIST_TYPE);
    }

    public static ArrayList<EnclosureData> getEnclosureList() {
        return enclosures;
    }

    private static <T> void saveData(String path, ArrayList<T> data) {
        try {
            File file = new File(path);

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(data, writer);
            }

            System.out.println(path + " successfully saved!");

        } catch (IOException e) {
            System.err.println("Failed to save " + path + ": " + e.getMessage());
        }
    }

    private static <T> ArrayList<T> loadData(String path, Type type) {
        try (FileReader reader = new FileReader(path)) {
            ArrayList<T> data = gson.fromJson(reader, type);
            System.out.println(path + " successfully loaded!");
            return data != null ? data : new ArrayList<>();
        } catch (IOException | JsonParseException e) {
            System.err.println(path + " not found or could not be read.");
            return new ArrayList<>();
        }
    }
}