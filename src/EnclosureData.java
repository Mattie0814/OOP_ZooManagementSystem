import java.util.Collection;
import java.util.HashMap;

public class EnclosureData {
    private final String id;
    private final String type;
    private final int capacity;
    private HashMap<String, AnimalData> currentOccupants;

    public EnclosureData(String id, String type, int capacity) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public int getCapacity() {
        return capacity;
    }

    public void addOccupant(String id, AnimalData animal) {
        if (currentOccupants.containsValue(animal)) { return; }

        currentOccupants.put(id, animal);
    }

    public void removeOccupant(String id) {
        if (!currentOccupants.containsValue(id)) { return; }

        currentOccupants.remove(id);
    }

    public Collection<AnimalData> getCurrentOccupants() {
        return currentOccupants.values();
    }

    public int getCurrentOccupantCount() {
        return currentOccupants.size();
    }
}