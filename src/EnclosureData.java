import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class EnclosureData {
    private final String id;
    private final String type;
    private final int capacity;
    private final Map<String, AnimalData> currentOccupants = new LinkedHashMap<>();

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

    public boolean isFull() {
        return currentOccupants.size() >= capacity;
    }

    public boolean addOccupant(String id, AnimalData animal) {
        if (isFull() || currentOccupants.containsKey(id)) return false;

        currentOccupants.put(id, animal);
        return true;
    }

    public void removeOccupant(String id) {
        currentOccupants.remove(id);
    }

    public Collection<AnimalData> getCurrentOccupants() {
        return currentOccupants.values();
    }

    public int getCurrentOccupantCount() {
        return currentOccupants.size();
    }
}