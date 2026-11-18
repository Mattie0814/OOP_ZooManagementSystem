import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class GUI_Animal extends JPanel {
    private final Map<String, AnimalData> animalMap = new HashMap<>();
    private final Map<String, EnclosureData> enclosureMap = new LinkedHashMap<>();
    private final ArrayList<EnclosureData> enclosures;

    private final JTextField searchField;

    private final JTable animalTable;
    private final DefaultTableModel animalTableModel;
    private final TableRowSorter<DefaultTableModel> animalSorter;

    private final DefaultTableModel enclosureTableModel;
    private final TableRowSorter<DefaultTableModel> enclosureSorter;

    public GUI_Animal(CardLayout cardLayout, JPanel mainPanel) {
        ArrayList<EnclosureData> loaded = Database.loadEnclosures();
        this.enclosures = (loaded != null) ? loaded : new ArrayList<>();

        setLayout(new BorderLayout());

        // Top Search & Action Bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Search & Registration"));

        topPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);

        JButton clearSearchButton = new JButton("Clear");
        JButton registerAnimalButton = new JButton("Register Animal");
        JButton updateAnimalButton = new JButton("Update Animal");
        JButton addEnclosureButton = new JButton("Add Enclosure");
        JButton returnButton = new JButton("Return to Main");

        clearSearchButton.addActionListener(e -> searchField.setText(""));
        registerAnimalButton.addActionListener(e -> openAnimalRegistrationForm());
        updateAnimalButton.addActionListener(e -> openAnimalUpdateForm());
        addEnclosureButton.addActionListener(e -> openAddEnclosureForm());
        returnButton.addActionListener(e -> cardLayout.show(mainPanel, "MAIN"));

        topPanel.add(clearSearchButton);
        topPanel.add(registerAnimalButton);
        topPanel.add(updateAnimalButton);
        topPanel.add(addEnclosureButton);
        topPanel.add(returnButton);

        // Animals Table
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

        // Enclosures Table
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

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Animals", new JScrollPane(animalTable));
        tabbedPane.addTab("Enclosures", new JScrollPane(enclosureTable));

        // Live Search Listener
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }
        });

        // Populate initial data from Database without mutating loaded lists
        for (EnclosureData enc : enclosures) {
            enclosureMap.put(enc.getId(), enc);

            for (AnimalData animal : enc.getCurrentOccupants()) {
                loadAnimalToTable(animal);
            }
        }

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        updateEnclosureTable();
    }

    private void filter() {
        String text = searchField.getText().trim();
        RowFilter<DefaultTableModel, Integer> rowFilter =
                text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(text));

        animalSorter.setRowFilter(rowFilter);
        enclosureSorter.setRowFilter(rowFilter);
    }

    private void loadAnimalToTable(AnimalData animal) {
        animalMap.put(animal.getName().toLowerCase(), animal);
        animalTableModel.addRow(new Object[]{
                animal.getName(),
                animal.getSpecies(),
                animal.getGender(),
                animal.getBirthDate(),
                animal.getEnclosureId(),
                animal.getHealthStatus()
        });
    }

    private void addAnimal(AnimalData animal) {
        EnclosureData enclosure = enclosureMap.get(animal.getEnclosureId());
        if (enclosure != null) {
            enclosure.addOccupant(animal);
        }

        loadAnimalToTable(animal);
        updateEnclosureTable();
        Database.saveEnclosures(enclosures);
    }

    private void openAnimalRegistrationForm() {
        if (enclosureMap.isEmpty()) {
            showError("Please add at least one enclosure before registering an animal.");
            return;
        }

        JTextField nameField = new JTextField();
        JTextField speciesField = new JTextField();
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Unknown"});
        JTextField birthDateField = new JTextField();
        JComboBox<String> enclosureBox = new JComboBox<>(enclosureMap.keySet().toArray(new String[0]));
        JComboBox<String> healthBox = new JComboBox<>(new String[]{"Healthy", "Under Treatment", "Quarantine", "Critical"});

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

        if (animalMap.containsKey(name.toLowerCase())) {
            showError("An animal named \"" + name + "\" already exists.");
            return;
        }

        if (!birthDate.isEmpty()) {
            try {
                LocalDate.parse(birthDate);
            } catch (DateTimeParseException ex) {
                showError("Birth Date must be a valid date in YYYY-MM-DD format.");
                return;
            }
        }

        EnclosureData enclosure = enclosureMap.get(location);
        if (enclosure == null) {
            showError("Selected enclosure does not exist.");
            return;
        }

        if (enclosure.isFull()) {
            showError("Enclosure " + location + " is already at full capacity.");
            return;
        }

        addAnimal(new AnimalData(name, species, gender, birthDate, healthStatus));

        JOptionPane.showMessageDialog(
                this,
                "Animal Registered Successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void openAnimalUpdateForm() {
        int viewRow = animalTable.getSelectedRow();
        if (viewRow < 0) {
            showError("Please select an animal in the table first.");
            return;
        }

        int modelRow = animalTable.convertRowIndexToModel(viewRow);
        String name = (String) animalTableModel.getValueAt(modelRow, 0);
        AnimalData animal = animalMap.get(name.toLowerCase());
        if (animal == null) {
            showError("Could not find the selected animal.");
            return;
        }

        JTextField nameField = new JTextField(animal.getName());
        nameField.setEditable(false);
        JTextField speciesField = new JTextField(animal.getSpecies());
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Unknown"});
        genderBox.setSelectedItem(animal.getGender());
        JTextField birthDateField = new JTextField(animal.getBirthDate());
        JComboBox<String> enclosureBox = new JComboBox<>(enclosureMap.keySet().toArray(new String[0]));
        enclosureBox.setSelectedItem(animal.getEnclosureId());
        JComboBox<String> healthBox = new JComboBox<>(new String[]{"Healthy", "Under Treatment", "Quarantine", "Critical"});
        healthBox.setSelectedItem(animal.getHealthStatus());

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
                this, formPanel, "Update Animal Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return;

        String species = speciesField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String birthDate = birthDateField.getText().trim();
        String newEnclosureId = (String) enclosureBox.getSelectedItem();
        String healthStatus = (String) healthBox.getSelectedItem();

        if (species.isEmpty()) {
            showError("Species is required!");
            return;
        }

        if (!birthDate.isEmpty()) {
            try {
                LocalDate.parse(birthDate);
            } catch (DateTimeParseException ex) {
                showError("Birth Date must be a valid date in YYYY-MM-DD format.");
                return;
            }
        }

        // Handle enclosure change
        String oldEnclosureId = animal.getEnclosureId();
        if (!Objects.equals(oldEnclosureId, newEnclosureId)) {
            EnclosureData newEnc = enclosureMap.get(newEnclosureId);
            if (newEnc == null) {
                showError("Selected enclosure does not exist.");
                return;
            }
            if (newEnc.isFull()) {
                showError("Enclosure " + newEnclosureId + " is already at full capacity.");
                return;
            }
            EnclosureData oldEnc = enclosureMap.get(oldEnclosureId);
            if (oldEnc != null) oldEnc.removeOccupant(animal);
            newEnc.addOccupant(animal);
            animal.setEnclosureId(newEnclosureId);
        }

        animal.setSpecies(species);
        animal.setGender(gender);
        animal.setBirthDate(birthDate);
        animal.setHealthStatus(healthStatus);

        // Refresh table row
        animalTableModel.setValueAt(animal.getSpecies(), modelRow, 1);
        animalTableModel.setValueAt(animal.getGender(), modelRow, 2);
        animalTableModel.setValueAt(animal.getBirthDate(), modelRow, 3);
        animalTableModel.setValueAt(animal.getEnclosureId(), modelRow, 4);
        animalTableModel.setValueAt(animal.getHealthStatus(), modelRow, 5);

        updateEnclosureTable();
        Database.saveEnclosures(enclosures);

        JOptionPane.showMessageDialog(this, "Animal Updated Successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);


        // Handle enclosure change
        String oldEnclosureId = animal.getEnclosureId();
        if (!Objects.equals(oldEnclosureId, newEnclosureId)) {
            EnclosureData newEnc = enclosureMap.get(newEnclosureId);
            if (newEnc == null) {
                showError("Selected enclosure does not exist.");
                return;
            }
            if (newEnc.isFull()) {
                showError("Enclosure " + newEnclosureId + " is already at full capacity.");
                return;
            }
            EnclosureData oldEnc = enclosureMap.get(oldEnclosureId);
            if (oldEnc != null) oldEnc.removeOccupant(animal);
            newEnc.addOccupant(animal);
            animal.setEnclosureId(newEnclosureId);
        }

        animal.setSpecies(species);
        animal.setGender(gender);
        animal.setBirthDate(birthDate);
        animal.setHealthStatus(healthStatus);

        // Refresh the table row
        animalTableModel.setValueAt(animal.getSpecies(), modelRow, 1);
        animalTableModel.setValueAt(animal.getGender(), modelRow, 2);
        animalTableModel.setValueAt(animal.getBirthDate(), modelRow, 3);
        animalTableModel.setValueAt(animal.getEnclosureId(), modelRow, 4);
        animalTableModel.setValueAt(animal.getHealthStatus(), modelRow, 5);

        updateEnclosureTable();
        Database.saveEnclosures(enclosures);

        JOptionPane.showMessageDialog(
                this,
                "Animal Information Updated Successfully.",
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

        int zone;
        try {
            zone = Integer.parseInt(capacityField.getText().trim());
        } catch (NumberFormatException ex) {
            zone = 0;
        }

        if (zone <= 0) {
            showError("Capacity must be a positive whole number.");
            return;
        }

        EnclosureData newEnclosure = new EnclosureData(id, name, zone);
        enclosureMap.put(id, newEnclosure);
        enclosures.add(newEnclosure);

        Database.saveEnclosures(enclosures);
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
            String occupantNames = enc.getCurrentOccupants().stream()
                    .map(AnimalData::getName)
                    .collect(Collectors.joining(", "));

            enclosureTableModel.addRow(new Object[]{
                    enc.getId(),
                    enc.getName(),
                    enc.getCapacity(),
                    enc.getCurrentOccupantCount(),
                    occupantNames.isEmpty() ? "Empty" : occupantNames
            });
        }
    }
}