import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

class Animal {
    private String name;
    private String species;
    private String gender;
    private String birthDate;
    private String enclosureId;
    private String healthStatus;

    public Animal(String name, String species, String gender, String birthDate,
                  String enclosureId, String healthStatus) {
        this.name = name;
        this.species = species;
        this.gender = gender;
        this.birthDate = birthDate;
        this.enclosureId = enclosureId;
        this.healthStatus = healthStatus;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getEnclosureId() {
        return enclosureId;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public String getAnimalType() {
        return "Animal";
    }
}

class Lion extends Animal {
    public Lion(String name, String gender, String birthDate,
                String enclosureId, String healthStatus) {
        super(name, "African Lion", gender, birthDate, enclosureId, healthStatus);
    }

    @Override
    public String getAnimalType() {
        return "Lion";
    }
}

class Bear extends Animal {
    public Bear(String name, String gender, String birthDate,
                String enclosureId, String healthStatus) {
        super(name, "Black Bear", gender, birthDate, enclosureId, healthStatus);
    }

    @Override
    public String getAnimalType() {
        return "Bear";
    }
}

class Enclosure {
    private String id;
    private String type;
    private int capacity;
    private List<String> currentOccupants;

    public Enclosure(String id, String type, int capacity) {
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

    public List<String> getCurrentOccupants() {
        return currentOccupants;
    }

    public int getCurrentOccupantCount() {
        return currentOccupants.size();
    }
}

public class Animals extends JPanel {
    private Map<String, Animal> animalMap = new LinkedHashMap<>();
    private Map<String, Enclosure> enclosureMap = new LinkedHashMap<>();

    private JButton registerAnimalButton;
    private JButton addEnclosureButton;
    private JButton returnButton;
    private JTextField searchField;

    private DefaultTableModel animalTableModel;
    private TableRowSorter<DefaultTableModel> animalSorter;

    private DefaultTableModel enclosureTableModel;
    private TableRowSorter<DefaultTableModel> enclosureSorter;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public Animals(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        this.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Search & Registration"));

        topPanel.add(new JLabel("Search:"));

        searchField = new JTextField(20);
        topPanel.add(searchField);

        registerAnimalButton = new JButton("Register Animal");
        addEnclosureButton = new JButton("Add Enclosure");
        returnButton = new JButton("Return to Main");

        registerAnimalButton.addActionListener(e -> openAnimalRegistrationForm());
        addEnclosureButton.addActionListener(e -> openAddEnclosureForm());
        returnButton.addActionListener(e -> showMain());

        topPanel.add(registerAnimalButton);
        topPanel.add(addEnclosureButton);
        topPanel.add(returnButton);

        JTabbedPane tabbedPane = new JTabbedPane();

        String[] animalCols = {
                "Name", "Species", "Gender", "Birth Date",
                "Enclosure Location", "Health Status"
        };

        animalTableModel = new DefaultTableModel(animalCols, 0);
        JTable animalTable = new JTable(animalTableModel);

        animalSorter = new TableRowSorter<>(animalTableModel);
        animalTable.setRowSorter(animalSorter);

        String[] enclosureCols = {
                "ID", "Type", "Capacity",
                "Current Occupants Count", "Occupants List"
        };

        enclosureTableModel = new DefaultTableModel(enclosureCols, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2 || columnIndex == 3) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        JTable enclosureTable = new JTable(enclosureTableModel);
        enclosureSorter = new TableRowSorter<>(enclosureTableModel);

        Comparator<String> numericIdComparator = (s1, s2) -> {
            try {
                int n1 = Integer.parseInt(s1.replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(s2.replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
            } catch (Exception ex) {
                return s1.compareTo(s2);
            }
        };

        enclosureSorter.setComparator(0, numericIdComparator);
        enclosureSorter.setComparator(2, Comparator.naturalOrder());
        enclosureSorter.setComparator(3, Comparator.naturalOrder());

        enclosureTable.setRowSorter(enclosureSorter);

        tabbedPane.addTab("Animals", new JScrollPane(animalTable));
        tabbedPane.addTab("Enclosures", new JScrollPane(enclosureTable));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            public void changedUpdate(DocumentEvent e) {
                filter();
            }

            private void filter() {
                String text = searchField.getText().trim();

                if (text.isEmpty()) {
                    animalSorter.setRowFilter(null);
                    enclosureSorter.setRowFilter(null);
                } else {
                    animalSorter.setRowFilter(
                            RowFilter.regexFilter("(?i)" + text)
                    );

                    enclosureSorter.setRowFilter(
                            RowFilter.regexFilter("(?i)" + text)
                    );
                }
            }
        });

        loadDefaultData();

        this.add(topPanel, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadDefaultData() {
        Enclosure enc1 = new Enclosure("ENC-101", "Lion's Den", 2);
        Enclosure enc2 = new Enclosure("ENC-102", "Naturalistic Habitat", 5);

        enclosureMap.put(enc1.getId(), enc1);
        enclosureMap.put(enc2.getId(), enc2);

        Animal animal1 = new Lion(
                "Mufasa",
                "Male",
                "2019-05-12",
                "ENC-101",
                "Healthy"
        );

        Animal animal2 = new Bear(
                "Tragen",
                "Male",
                "2021-08-20",
                "ENC-102",
                "Critical"
        );

        animalMap.put(animal1.getName(), animal1);
        animalMap.put(animal2.getName(), animal2);

        enc1.getCurrentOccupants().add(animal1.getName());
        enc2.getCurrentOccupants().add(animal2.getName());

        animalTableModel.addRow(new Object[]{
                animal1.getName(),
                animal1.getSpecies(),
                animal1.getGender(),
                animal1.getBirthDate(),
                animal1.getEnclosureId(),
                animal1.getHealthStatus()
        });

        animalTableModel.addRow(new Object[]{
                animal2.getName(),
                animal2.getSpecies(),
                animal2.getGender(),
                animal2.getBirthDate(),
                animal2.getEnclosureId(),
                animal2.getHealthStatus()
        });

        updateEnclosureTable();
    }

    private void openAnimalRegistrationForm() {
        JTextField nameField = new JTextField();
        JTextField speciesField = new JTextField();

        JComboBox<String> genderBox =
                new JComboBox<>(new String[]{"Male", "Female", "Unknown"});

        JTextField birthDateField = new JTextField();

        String[] availableEnclosures =
                enclosureMap.keySet().toArray(new String[0]);

        JComboBox<String> enclosureBox =
                new JComboBox<>(
                        availableEnclosures.length > 0
                                ? availableEnclosures
                                : new String[]{"None"}
                );

        JComboBox<String> healthBox =
                new JComboBox<>(
                        new String[]{
                                "Healthy",
                                "Under Treatment",
                                "Quarantine",
                                "Critical"
                        }
                );

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Species:"));
        formPanel.add(speciesField);

        formPanel.add(new JLabel("Gender:"));
        formPanel.add(genderBox);

        formPanel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        formPanel.add(birthDateField);

        formPanel.add(new JLabel("Cage/Enclosure Location:"));
        formPanel.add(enclosureBox);

        formPanel.add(new JLabel("Health Status:"));
        formPanel.add(healthBox);

        int option = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Register Animal Details",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String species = speciesField.getText().trim();
            String gender = (String) genderBox.getSelectedItem();
            String birthDate = birthDateField.getText().trim();
            String location = (String) enclosureBox.getSelectedItem();
            String healthStatus = (String) healthBox.getSelectedItem();

            if (name.isEmpty() || species.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Name and Species are required!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Animal animal;

            if (species.equalsIgnoreCase("Lion") ||
                    species.equalsIgnoreCase("African Lion")) {

                animal = new Lion(
                        name,
                        gender,
                        birthDate,
                        location,
                        healthStatus
                );

            } else if (species.equalsIgnoreCase("Bear") ||
                    species.equalsIgnoreCase("Black Bear")) {

                animal = new Bear(
                        name,
                        gender,
                        birthDate,
                        location,
                        healthStatus
                );

            } else {

                animal = new Animal(
                        name,
                        species,
                        gender,
                        birthDate,
                        location,
                        healthStatus
                );
            }

            animalMap.put(name, animal);

            if (enclosureMap.containsKey(location)) {
                enclosureMap.get(location)
                        .getCurrentOccupants()
                        .add(name);
            }

            animalTableModel.addRow(new Object[]{
                    animal.getName(),
                    animal.getSpecies(),
                    animal.getGender(),
                    animal.getBirthDate(),
                    animal.getEnclosureId(),
                    animal.getHealthStatus()
            });

            updateEnclosureTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Animal Registered Successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void openAddEnclosureForm() {
        JTextField idField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField capacityField = new JTextField();

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        formPanel.add(new JLabel("Enclosure ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeField);

        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(capacityField);

        int option = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Add New Enclosure",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String type = typeField.getText().trim();
            int capacity;

            try {
                capacity = Integer.parseInt(
                        capacityField.getText().trim()
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Capacity must be a valid integer.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (id.isEmpty() || type.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "ID and Type are required.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Enclosure enclosure = new Enclosure(id, type, capacity);

            enclosureMap.put(id, enclosure);

            updateEnclosureTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Enclosure Added Successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void updateEnclosureTable() {
        enclosureTableModel.setRowCount(0);

        for (Enclosure enc : enclosureMap.values()) {
            enclosureTableModel.addRow(new Object[]{
                    enc.getId(),
                    enc.getType(),
                    enc.getCapacity(),
                    enc.getCurrentOccupantCount(),
                    enc.getCurrentOccupants().toString()
            });
        }
    }

    private void showMain() {
        cardLayout.show(mainPanel, "MAIN");
    }
}