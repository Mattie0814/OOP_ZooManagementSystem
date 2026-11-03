import com.google.gson.*;
import java.lang.reflect.Type;

public class ItemDataAdapter implements JsonSerializer<ItemData>, JsonDeserializer<ItemData> {
    @Override
    public JsonElement serialize(ItemData item, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();

        json.addProperty("name", item.getName());
        json.addProperty("type", item.getType().name());
        json.addProperty("stock", item.getStock());

        if (item instanceof FoodItem) {
            FoodItem food = (FoodItem) item;

            json.addProperty("expiry", food.getExpiry());
            json.addProperty("animals", food.getAnimals());

        } else if (item instanceof MedicineItem) {
            MedicineItem medicine = (MedicineItem) item;

            json.addProperty("expiry", medicine.getExpiry());

        } else if (item instanceof MiscellaneousItem) {
            // No additional properties
        }

        return json;
    }

    @Override
    public ItemData deserialize(
            JsonElement element,
            Type type,
            JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject json = element.getAsJsonObject();

        String name = json.get("name").getAsString();
        ItemData.TYPE itemType = ItemData.TYPE.valueOf(json.get("type").getAsString());
        int stock = json.get("stock").getAsInt();

        switch (itemType) {
            case FOOD:
                return new FoodItem(name, stock, json.get("expiry").getAsString(), json.get("animals").getAsString());

            case MEDICINE:
                return new MedicineItem(name, stock, json.get("expiry").getAsString());

            case MISC:
                return new MiscellaneousItem(name, stock);

            default:
                throw new JsonParseException("Unknown item type: " + itemType);
        }
    }
}