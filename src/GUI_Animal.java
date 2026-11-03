import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class GUI_Animal extends JPanel {
    private Map<String, AnimalData> animalMap = new LinkedHashMap<>();
    private Map<String, EnclosureData> enclosureMap = new LinkedHashMap<>();

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

    public GUI_Animal(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        this.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Search & Registration"));

        topPanel.add(new JLabel("Search:"));

        searchField = new JTextField(20);
        topPanel.add(searchField);

        registerAnimalButton = new JButton("Register AnimalData");
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

        tabbedPane.addTab("GUI_Animal", new JScrollPane(animalTable));
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
        EnclosureData enc1 = new EnclosureData("ENC-101", "Lion's Den", 2);
        EnclosureData enc2 = new EnclosureData("ENC-102", "Naturalistic Habitat", 5);

        enclosureMap.put(enc1.getId(), enc1);
        enclosureMap.put(enc2.getId(), enc2);

        AnimalData animalData1 = new AnimalData(
                "Mufasa",
                "Lion",
                "Male",
                "2019-05-12",
                "ENC-101",
                "Healthy"
        );

        AnimalData animalData2 = new AnimalData(
                "Tragen",
                "Bear",
                "Male",
                "2021-08-20",
                "ENC-102",
                "Critical"
        );

        animalMap.put(animalData1.getName(), animalData1);
        animalMap.put(animalData2.getName(), animalData2);


        enc1.addOccupant(animalData1.getName(), animalData1);
        enc2.addOccupant(animalData2.getName(), animalData2);

        animalTableModel.addRow(new Object[]{
                animalData1.getName(),
                animalData1.getSpecies(),
                animalData1.getGender(),
                animalData1.getBirthDate(),
                animalData1.getType(),
                animalData1.getHealthStatus()
        });

        animalTableModel.addRow(new Object[]{
                animalData2.getName(),
                animalData2.getSpecies(),
                animalData2.getGender(),
                animalData2.getBirthDate(),
                animalData2.getType(),
                animalData2.getHealthStatus()
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
                "Register AnimalData Details",
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

            AnimalData animalData;

            if (species.equalsIgnoreCase("Lion") ||
                    species.equalsIgnoreCase("African Lion")) {

                animalData = new AnimalData(
                        name,
                        "Lion",
                        gender,
                        birthDate,
                        location,
                        healthStatus
                );

            } else if (species.equalsIgnoreCase("Bear") ||
                    species.equalsIgnoreCase("Black Bear")) {

                animalData = new AnimalData(
                        name,
                        "Bear",
                        gender,
                        birthDate,
                        location,
                        healthStatus
                );

            } else {

                animalData = new AnimalData(
                        name,
                        species,
                        gender,
                        birthDate,
                        location,
                        healthStatus
                );
            }

            animalMap.put(name, animalData);

            if (enclosureMap.containsKey(location)) {
                enclosureMap.get(location).addOccupant(name, animalData);
            }

            animalTableModel.addRow(new Object[]{
                    animalData.getName(),
                    animalData.getSpecies(),
                    animalData.getGender(),
                    animalData.getBirthDate(),
                    animalData.getType(),
                    animalData.getHealthStatus()
            });

            updateEnclosureTable();

            JOptionPane.showMessageDialog(
                    this,
                    "AnimalData Registered Successfully.",
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

            EnclosureData enclosure = new EnclosureData(id, type, capacity);

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

    private void showMain() {
        cardLayout.show(mainPanel, "MAIN");
    }
}