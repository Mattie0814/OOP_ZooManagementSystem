import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class GUI_Animal extends JPanel {
    private final Map<EnclosureData, ArrayList<AnimalData>> enclosureMap = new HashMap<>();
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

        animalTable = new JTable(animalTableModel);
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
            public void insertUpdate(DocumentEvent e) { filter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        // Initialize Enclosure Map & Tables
        for (EnclosureData enc : enclosures) {
            ArrayList<AnimalData> animals = new ArrayList<>(enc.getCurrentOccupants());
            enclosureMap.put(enc, animals);
            for (AnimalData animal : animals) {
                loadAnimalToTable(animal, enc);
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

    private void loadAnimalToTable(AnimalData animal, EnclosureData enclosure) {
        animalTableModel.addRow(new Object[]{
                animal.getName(),
                animal.getSpecies(),
                animal.getGender(),
                animal.getBirthDate(),
                enclosure != null ? enclosure.getId() : "Unassigned",
                animal.getHealthStatus()
        });
    }

    private void addAnimalToEnclosure(EnclosureData enclosure, AnimalData animal) {
        if (enclosure == null || animal == null) return;

        ArrayList<AnimalData> occupants = enclosureMap.computeIfAbsent(enclosure, k -> new ArrayList<>());
        if (occupants.contains(animal)) return;

        occupants.add(animal);
        enclosure.addOccupant(animal);
        loadAnimalToTable(animal, enclosure);
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
        JTextField imagePathField = new JTextField();
        imagePathField.setEditable(false);
        JButton browseImageButton = new JButton("Browse...");
        browseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                imagePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(browseImageButton, BorderLayout.EAST);

        JComboBox<EnclosureData> enclosureBox = new JComboBox<>(enclosureMap.keySet().toArray(new EnclosureData[0]));
        enclosureBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof EnclosureData) {
                    setText(((EnclosureData) value).getId() + " - " + ((EnclosureData) value).getName());
                }
                return this;
            }
        });

        JComboBox<String> healthBox = new JComboBox<>(new String[]{"Healthy", "Under Treatment", "Quarantine", "Critical"});

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Species:"));
        formPanel.add(speciesField);
        formPanel.add(new JLabel("Gender:"));
        formPanel.add(genderBox);
        formPanel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        formPanel.add(birthDateField);
        formPanel.add(new JLabel("Animal Photo:"));
        formPanel.add(imagePanel);
        formPanel.add(new JLabel("Enclosure Location:"));
        formPanel.add(enclosureBox);
        formPanel.add(new JLabel("Health Status:"));
        formPanel.add(healthBox);

        int option = JOptionPane.showConfirmDialog(
                this, formPanel, "Register Animal Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        String species = speciesField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String birthDate = birthDateField.getText().trim();
        EnclosureData selectedEnclosure = (EnclosureData) enclosureBox.getSelectedItem();
        String healthStatus = (String) healthBox.getSelectedItem();

        if (name.isEmpty() || species.isEmpty()) {
            showError("Name and Species are required!");
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

        if (selectedEnclosure == null) {
            showError("Please select a valid enclosure.");
            return;
        }

        if (selectedEnclosure.isFull()) {
            showError("Enclosure " + selectedEnclosure.getId() + " is already at full capacity.");
            return;
        }

        AnimalData newAnimal = new AnimalData(name, species, gender, birthDate, healthStatus);
        newAnimal.setImagePath(imagePathField.getText().trim());
        addAnimalToEnclosure(selectedEnclosure, newAnimal);

        JOptionPane.showMessageDialog(this, "Animal Registered Successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openAnimalUpdateForm() {
        int viewRow = animalTable.getSelectedRow();
        if (viewRow < 0) {
            showError("Please select an animal in the table first.");
            return;
        }

        int modelRow = animalTable.convertRowIndexToModel(viewRow);
        String animalName = (String) animalTableModel.getValueAt(modelRow, 0);

        AnimalData targetAnimal = null;
        EnclosureData currentEnclosure = null;

        for (Map.Entry<EnclosureData, ArrayList<AnimalData>> entry : enclosureMap.entrySet()) {
            for (AnimalData animal : entry.getValue()) {
                if (animal.getName().equalsIgnoreCase(animalName)) {
                    targetAnimal = animal;
                    currentEnclosure = entry.getKey();
                    break;
                }
            }
            if (targetAnimal != null) break;
        }

        if (targetAnimal == null || currentEnclosure == null) {
            showError("Could not find the selected animal data.");
            return;
        }

        JTextField nameField = new JTextField(targetAnimal.getName());
        nameField.setEditable(false);
        JTextField speciesField = new JTextField(targetAnimal.getSpecies());
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Unknown"});
        genderBox.setSelectedItem(targetAnimal.getGender());
        JTextField birthDateField = new JTextField(targetAnimal.getBirthDate());
        JTextField imagePathField = new JTextField(targetAnimal.getImagePath() != null ? targetAnimal.getImagePath() : "");
        imagePathField.setEditable(false);
        JButton browseImageButton = new JButton("Browse...");
        browseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                imagePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(browseImageButton, BorderLayout.EAST);

        JComboBox<EnclosureData> enclosureBox = new JComboBox<>(enclosureMap.keySet().toArray(new EnclosureData[0]));
        enclosureBox.setSelectedItem(currentEnclosure);
        enclosureBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof EnclosureData) {
                    setText(((EnclosureData) value).getId() + " - " + ((EnclosureData) value).getName());
                }
                return this;
            }
        });

        JComboBox<String> healthBox = new JComboBox<>(new String[]{"Healthy", "Under Treatment", "Quarantine", "Critical"});
        healthBox.setSelectedItem(targetAnimal.getHealthStatus());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Species:"));
        formPanel.add(speciesField);
        formPanel.add(new JLabel("Gender:"));
        formPanel.add(genderBox);
        formPanel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        formPanel.add(birthDateField);
        formPanel.add(new JLabel("Animal Photo:"));
        formPanel.add(imagePanel);
        formPanel.add(new JLabel("Enclosure Location:"));
        formPanel.add(enclosureBox);
        formPanel.add(new JLabel("Health Status:"));
        formPanel.add(healthBox);

        int option = JOptionPane.showConfirmDialog(
                this, formPanel, "Update Animal Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (option != JOptionPane.OK_OPTION) return;

        String species = speciesField.getText().trim();
        String gender = (String) genderBox.getSelectedItem();
        String birthDate = birthDateField.getText().trim();
        EnclosureData newEnclosure = (EnclosureData) enclosureBox.getSelectedItem();
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

        if (newEnclosure == null) {
            showError("Selected enclosure does not exist.");
            return;
        }

        // Handle enclosure migration if changed
        if (!currentEnclosure.equals(newEnclosure)) {
            if (newEnclosure.isFull()) {
                showError("Enclosure " + newEnclosure.getId() + " is already at full capacity.");
                return;
            }
            currentEnclosure.removeOccupant(targetAnimal);
            enclosureMap.get(currentEnclosure).remove(targetAnimal);

            newEnclosure.addOccupant(targetAnimal);
            enclosureMap.computeIfAbsent(newEnclosure, k -> new ArrayList<>()).add(targetAnimal);
        }

        targetAnimal.setSpecies(species);
        targetAnimal.setGender(gender);
        targetAnimal.setBirthDate(birthDate);
        targetAnimal.setImagePath(imagePathField.getText().trim());
        targetAnimal.setHealthStatus(healthStatus);

        // Refresh table row
        animalTableModel.setValueAt(targetAnimal.getSpecies(), modelRow, 1);
        animalTableModel.setValueAt(targetAnimal.getGender(), modelRow, 2);
        animalTableModel.setValueAt(targetAnimal.getBirthDate(), modelRow, 3);
        animalTableModel.setValueAt(newEnclosure.getId(), modelRow, 4);
        animalTableModel.setValueAt(targetAnimal.getHealthStatus(), modelRow, 5);

        updateEnclosureTable();
        Database.saveEnclosures(enclosures);

        JOptionPane.showMessageDialog(this, "Animal Information Updated Successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openAddEnclosureForm() {
        JTextField idField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField capacityField = new JTextField();

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.add(new JLabel("Enclosure ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Type / Name:"));
        formPanel.add(typeField);
        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(capacityField);

        int option = JOptionPane.showConfirmDialog(
                this, formPanel, "Add New Enclosure",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) return;

        String id = idField.getText().trim();
        String type = typeField.getText().trim();

        if (id.isEmpty() || type.isEmpty()) {
            showError("ID and Type are required.");
            return;
        }

        boolean exists = enclosureMap.keySet().stream().anyMatch(e -> e.getId().equalsIgnoreCase(id));
        if (exists) {
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

        EnclosureData newEnclosure = new EnclosureData(id, type, capacity);
        enclosureMap.put(newEnclosure, new ArrayList<>());
        enclosures.add(newEnclosure);

        Database.saveEnclosures(enclosures);
        updateEnclosureTable();

        JOptionPane.showMessageDialog(this, "Enclosure Added Successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateEnclosureTable() {
        enclosureTableModel.setRowCount(0);

        for (Map.Entry<EnclosureData, ArrayList<AnimalData>> entry : enclosureMap.entrySet()) {
            EnclosureData enc = entry.getKey();
            ArrayList<AnimalData> occupants = entry.getValue();

            String occupantNames = occupants.stream()
                    .map(AnimalData::getName)
                    .collect(Collectors.joining(", "));

            enclosureTableModel.addRow(new Object[]{
                    enc.getId(),
                    enc.getName(),
                    enc.getCapacity(),
                    occupants.size(),
                    occupantNames.isEmpty() ? "Empty" : occupantNames
            });
        }
    }
}