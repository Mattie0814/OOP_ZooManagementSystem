import com.google.gson.*;
import java.lang.reflect.Type;

public class ItemDataAdapter implements JsonSerializer<ItemData>, JsonDeserializer<ItemData> {
    @Override
    public JsonElement serialize(ItemData item, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();

        json.addProperty("name", item.getName());
        json.addProperty("type", item.getType().name());
        json.addProperty("stock", item.getStock());
        json.addProperty("procured", item.getDateProcured());
        if (item instanceof FoodItem) {
            FoodItem food = (FoodItem) item;

            json.addProperty("animals", food.getAnimals());

        }
        json.addProperty("id", item.getID());
        json.addProperty("update", item.getAutoUpdate());

        return json;
    }

    @Override
    public ItemData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = element.getAsJsonObject();

        String name = json.get("name").getAsString();
        ItemData.TYPE itemType = ItemData.TYPE.valueOf(json.get("type").getAsString());
        int stock = json.get("stock").getAsInt();
        String dateProcured = json.get("procured").getAsString();
        int autoUpdate = json.get("update").getAsInt();

        switch (itemType) {
            case FOOD:
                return new FoodItem(name, stock, dateProcured, json.get("animals").getAsString(), json.get("id").getAsString(), autoUpdate);

            case MEDICINE:
                return new MedicineItem(name, stock, dateProcured, json.get("id").getAsString(), autoUpdate);

            case MISC:
                return new MiscellaneousItem(name, stock, dateProcured, json.get("id").getAsString(),autoUpdate);

            default:
                throw new JsonParseException("Unknown item type: " + itemType);
        }
    }
}