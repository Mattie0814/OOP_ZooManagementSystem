import java.util.ArrayList;

public class EnclosureData {
    private String id;
    private String name;
    private int capacity;
    private ArrayList<AnimalData> animals = new ArrayList<>();

    public EnclosureData(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    private ArrayList<AnimalData> ensureOccupantsList() {
        if (animals == null) {
            animals = new ArrayList<>();
        }
        return animals;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }

    public boolean isFull() {
        return getCurrentOccupantCount() >= capacity;
    }

    public void addOccupant(AnimalData animal) {
        if (animal == null || isFull()) return;
        ensureOccupantsList().add(animal);
    }

    public void removeOccupant(AnimalData animal) {
        ensureOccupantsList().remove(animal);
    }

    public ArrayList<AnimalData> getCurrentOccupants() {
        return ensureOccupantsList();
    }

    public int getCurrentOccupantCount() {
        return ensureOccupantsList().size();
    }
}