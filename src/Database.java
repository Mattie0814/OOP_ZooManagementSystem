import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Database {
    private static final String USER_PATH = "data/users.json";
    private static final String INVENTORY_PATH = "data/inventory.json";
    private static final String ENCLOSURE_PATH = "data/enclosure.json";

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TaskData.class, new TaskDataAdapter())
            .registerTypeAdapter(ItemData.class, new ItemDataAdapter())
            .create();


    private static final Type USER_LIST_TYPE = new TypeToken<ArrayList<UserData>>() {}.getType();
    private static final Type ITEM_LIST_TYPE = new TypeToken<ArrayList<ItemData>>() {}.getType();
    private static final Type ENCLOSURE_LIST_TYPE = new TypeToken<ArrayList<EnclosureData>>() {}.getType();

    // USER DATA
    public static void saveUsers(ArrayList<UserData> users) {
        saveData(USER_PATH, users);
    }

    public static ArrayList<UserData> loadUsers() {
        return loadData(USER_PATH, USER_LIST_TYPE);
    }

    // INVENTORY DATA
    public static void saveInventory(ArrayList<ItemData> items) {
        saveData(INVENTORY_PATH, items);
    }

    public static ArrayList<ItemData> loadInventory() {
        return loadData(INVENTORY_PATH, ITEM_LIST_TYPE);
    }

    // ENCLOSURE DATA
    public static void saveEnclosures(ArrayList<EnclosureData> enclosures) {
        saveData(ENCLOSURE_PATH, enclosures);
    }

    public static ArrayList<EnclosureData> loadEnclosures() {
        return loadData(ENCLOSURE_PATH, ENCLOSURE_LIST_TYPE);
    }

    private static <T> void saveData(String path, ArrayList<T> data) {
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(data, writer);

            System.out.println(path + " successfully saved!");

        } catch (IOException e) {

            System.out.println("Failed to save " + path);
            e.printStackTrace();
        }
    }

    private static <T> ArrayList<T> loadData(String path, Type type) {
        try (FileReader reader = new FileReader(path)) {
            ArrayList<T> data = gson.fromJson(reader, type);

            System.out.println(path + " successfully loaded!");

            return data != null ? data : new ArrayList<>();

        } catch (IOException e) {
            System.out.println(path + " not found or could not be read.");

            return new ArrayList<>();
        }
    }
}