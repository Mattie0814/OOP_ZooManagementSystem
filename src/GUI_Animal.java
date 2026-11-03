import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class GUI_Animal extends JPanel {
    private final Map<String, AnimalData> animalMap = new LinkedHashMap<>();
    private final Map<String, EnclosureData> enclosureMap = new LinkedHashMap<>();

    private final JTextField searchField;

    private final DefaultTableModel animalTableModel;
    private final TableRowSorter<DefaultTableModel> animalSorter;

    private final DefaultTableModel enclosureTableModel;
    private final TableRowSorter<DefaultTableModel> enclosureSorter;

    public GUI_Animal(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Search & Registration"));

        topPanel.add(new JLabel("Search:"));

        searchField = new JTextField(20);
        topPanel.add(searchField);

        JButton registerAnimalButton = new JButton("Register Animal");
        JButton addEnclosureButton = new JButton("Add Enclosure");
        JButton returnButton = new JButton("Return to Main");

        registerAnimalButton.addActionListener(e -> openAnimalRegistrationForm());
        addEnclosureButton.addActionListener(e -> openAddEnclosureForm());
        returnButton.addActionListener(e -> cardLayout.show(mainPanel, "MAIN"));

        topPanel.add(registerAnimalButton);
        topPanel.add(addEnclosureButton);
        topPanel.add(returnButton);

        JTabbedPane tabbedPane = new JTabbedPane();

        String[] animalCols = {
                "Name", "Species", "Gender", "Birth Date",
                "Enclosure Location", "Health Status"
        };

        animalTableModel = new DefaultTableModel(animalCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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
                return (columnIndex == 2 || columnIndex == 3) ? Integer.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable enclosureTable = new JTable(enclosureTableModel);
        enclosureSorter = new TableRowSorter<>(enclosureTableModel);

        Comparator<String> numericIdComparator = (s1, s2) -> {
            try {
                int n1 = Integer.parseInt(s1.replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(s2.replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
            } catch (NumberFormatException ex) {
                return s1.compareTo(s2);
            }
        };

        enclosureSorter.setComparator(0, numericIdComparator);
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
        });

        loadDefaultData();

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void filter() {
        String text = searchField.getText().trim();
        RowFilter<DefaultTableModel, Integer> rowFilter =
                text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(text));

        animalSorter.setRowFilter(rowFilter);
        enclosureSorter.setRowFilter(rowFilter);
    }

    private void loadDefaultData() {
        EnclosureData enc1 = new EnclosureData("ENC-101", "Lion's Den", 2);
        EnclosureData enc2 = new EnclosureData("ENC-102", "Naturalistic Habitat", 5);

        enclosureMap.put(enc1.getId(), enc1);
        enclosureMap.put(enc2.getId(), enc2);

        addAnimal(new AnimalData("Mufasa", "Lion", "Male", "2019-05-12", "ENC-101", "Healthy"));
        addAnimal(new AnimalData("Tragen", "Bear", "Male", "2021-08-20", "ENC-102", "Critical"));
    }

    private void addAnimal(AnimalData animal) {
        animalMap.put(animal.getName(), animal);

        EnclosureData enclosure = enclosureMap.get(animal.getEnclosureId());
        if (enclosure != null) {
            enclosure.addOccupant(animal.getName(), animal);
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

        if (option != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        String species = speciesField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String birthDate = birthDateField.getText().trim();
        String location = (String) enclosureBox.getSelectedItem();
        String healthStatus = (String) healthBox.getSelectedItem();

        if (name.isEmpty() || species.isEmpty()) {
            showError("Name and Species are required!");
            return;
        }

        if (animalMap.containsKey(name)) {
            showError("An animal named \"" + name + "\" already exists.");
            return;
        }

        EnclosureData enclosure = enclosureMap.get(location);
        if (enclosure != null && enclosure.isFull()) {
            showError("Enclosure " + location + " is already at full capacity.");
            return;
        }

        addAnimal(new AnimalData(name, species, gender, birthDate, location, healthStatus));

        JOptionPane.showMessageDialog(
                this,
                "Animal Registered Successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
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

        if (option != JOptionPane.OK_OPTION) return;

        String id = idField.getText().trim();
        String type = typeField.getText().trim();

        if (id.isEmpty() || type.isEmpty()) {
            showError("ID and Type are required.");
            return;
        }

        if (enclosureMap.containsKey(id)) {
            showError("An enclosure with ID " + id + " already exists.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityField.getText().trim());
        } catch (NumberFormatException ex) {
            capacity = 0;
        }

        if (capacity <= 0) {
            showError("Capacity must be a positive whole number.");
            return;
        }

        enclosureMap.put(id, new EnclosureData(id, type, capacity));
        updateEnclosureTable();

        JOptionPane.showMessageDialog(
                this,
                "Enclosure Added Successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateEnclosureTable() {
        enclosureTableModel.setRowCount(0);

        for (EnclosureData enc : enclosureMap.values()) {
            enclosureTableModel.addRow(new Object[]{
                    enc.getId(),
                    enc.getType(),
                    enc.getCapacity(),
                    enc.getCurrentOccupantCount(),
                    enc.getCurrentOccupants().toString()
            });
        }
    }
}