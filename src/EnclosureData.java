import java.util.*;

public class EnclosureData {
    private String id;
    private String type;
    private int capacity;
    private ArrayList<AnimalData> currentOccupants;

    public EnclosureData(String id, String type, int capacity) {
        this.id = id;
        this.type = type;
        this.capacity = capacity;
        this.currentOccupants = new ArrayList<>();
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

    public void addOccupant(AnimalData animal) {
        if (isFull()) return;

        currentOccupants.add(animal);
    }

    public void removeOccupant(AnimalData animal) {
        currentOccupants.remove(animal);
    }

    public ArrayList<AnimalData> getCurrentOccupants() {
        return currentOccupants;
    }

    public int getCurrentOccupantCount() {
        return currentOccupants.size();
    }
}