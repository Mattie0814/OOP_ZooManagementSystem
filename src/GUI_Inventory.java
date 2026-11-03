import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class GUI_Inventory extends JPanel implements ActionListener {
    private JButton addInventory;
    private JButton returnButton;

    // Category navigation buttons for inner CardLayout
    private JButton foodTabBtn;
    private JButton medicineTabBtn;
    private JButton miscellaneousTabBtn;

    // ItemData manager
    private InventoryManager manager;

    // Inner CardLayout for switching inventory categories
    private CardLayout tableCardLayout;
    private JPanel tableCardsPanel;
    private String currentCategory = "FoodItem";

    // Outer CardLayout controls
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Tables
    private JTable foodTable;
    private JTable medicineTable;
    private JTable miscellaneousTable;

    // Table models
    private DefaultTableModel foodModel;
    private DefaultTableModel medicineModel;
    private DefaultTableModel miscellaneousModel;

    // Search fields
    private JTextField foodSearch;
    private JTextField medicineSearch;
    private JTextField miscellaneousSearch;

    // Table sorters
    private TableRowSorter<DefaultTableModel> foodSorter;
    private TableRowSorter<DefaultTableModel> medicineSorter;
    private TableRowSorter<DefaultTableModel> miscellaneousSorter;

    public GUI_Inventory(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.manager = new InventoryManager();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // -------------------------------------------------------------
        // TOP CONTROL PANEL (Navigation & Action Buttons)
        // -------------------------------------------------------------
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        addInventory = new JButton("Add Item");
        addInventory.addActionListener(this);

        returnButton = new JButton("Return to Main");
        returnButton.addActionListener(e -> {
            if (cardLayout != null && mainPanel != null) {
                cardLayout.show(mainPanel, "MAIN");
            }
        });

        leftActions.add(addInventory);
        leftActions.add(returnButton);

        // Category Switcher Buttons (Acts like tabs using CardLayout)
        JPanel categoryNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        foodTabBtn = new JButton("FoodItem");
        medicineTabBtn = new JButton("MedicineItem");
        miscellaneousTabBtn = new JButton("MiscellaneousItem");

        foodTabBtn.addActionListener(e -> switchCategory("FoodItem"));
        medicineTabBtn.addActionListener(e -> switchCategory("MedicineItem"));
        miscellaneousTabBtn.addActionListener(e -> switchCategory("MiscellaneousItem"));

        categoryNav.add(foodTabBtn);
        categoryNav.add(medicineTabBtn);
        categoryNav.add(miscellaneousTabBtn);

        topPanel.add(leftActions, BorderLayout.WEST);
        topPanel.add(categoryNav, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // CENTER CARDS PANEL (CardLayout for FoodItem, Meds, MiscellaneousItem)
        // -------------------------------------------------------------
        tableCardLayout = new CardLayout();
        tableCardsPanel = new JPanel(tableCardLayout);

        // 1. FoodItem Panel
        JPanel foodPanel = new JPanel(new BorderLayout(0, 10));
        foodSearch = new JTextField();
        foodSearch.setBorder(BorderFactory.createTitledBorder("Search FoodItem ItemData"));
        foodPanel.add(foodSearch, BorderLayout.NORTH);

        foodModel = new DefaultTableModel(new Object[]{"Name", "Stock", "Expiry", "GUI_Animal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        foodTable = new JTable(foodModel);
        foodSorter = new TableRowSorter<>(foodModel);
        foodTable.setRowSorter(foodSorter);
        foodPanel.add(new JScrollPane(foodTable), BorderLayout.CENTER);

        // 2. MedicineItem Panel
        JPanel medicinePanel = new JPanel(new BorderLayout(0, 10));
        medicineSearch = new JTextField();
        medicineSearch.setBorder(BorderFactory.createTitledBorder("Search MedicineItem ItemData"));
        medicinePanel.add(medicineSearch, BorderLayout.NORTH);

        medicineModel = new DefaultTableModel(new Object[]{"Name", "Stock", "Expiry"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        medicineTable = new JTable(medicineModel);
        medicineSorter = new TableRowSorter<>(medicineModel);
        medicineTable.setRowSorter(medicineSorter);
        medicinePanel.add(new JScrollPane(medicineTable), BorderLayout.CENTER);

        // 3. MiscellaneousItem Panel
        JPanel maintenancePanel = new JPanel(new BorderLayout(0, 10));
        miscellaneousSearch = new JTextField();
        miscellaneousSearch.setBorder(BorderFactory.createTitledBorder("Search MiscellaneousItem ItemData"));
        maintenancePanel.add(miscellaneousSearch, BorderLayout.NORTH);

        miscellaneousModel = new DefaultTableModel(new Object[]{"Name", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        miscellaneousTable = new JTable(miscellaneousModel);
        miscellaneousSorter = new TableRowSorter<>(miscellaneousModel);
        miscellaneousTable.setRowSorter(miscellaneousSorter);
        maintenancePanel.add(new JScrollPane(miscellaneousTable), BorderLayout.CENTER);

        // Add views to inner CardLayout
        tableCardsPanel.add(foodPanel, "FoodItem");
        tableCardsPanel.add(medicinePanel, "MedicineItem");
        tableCardsPanel.add(maintenancePanel, "MiscellaneousItem");

        add(tableCardsPanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // SEARCH LISTENERS
        // -------------------------------------------------------------
        addSearchListener(foodSearch, foodSorter);
        addSearchListener(medicineSearch, medicineSorter);
        addSearchListener(miscellaneousSearch, miscellaneousSorter);

        loadPresetInventory();
    }

    private void switchCategory(String category) {
        this.currentCategory = category;
        tableCardLayout.show(tableCardsPanel, category);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addInventory) {
            showAddWindow(currentCategory);
        }
    }

    private void showAddWindow(String type) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Add " + type + " Item", Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setResizable(false);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(nameField, gbc);

        JLabel stockLabel = new JLabel("Stock:");
        JTextField stockField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(stockLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(stockField, gbc);

        JTextField expiryField = null;
        if (type.equals("FoodItem") || type.equals("MedicineItem")) {
            JLabel expiryLabel = new JLabel("Expiry:");
            expiryField = new JTextField(20);
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
            formPanel.add(expiryLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            formPanel.add(expiryField, gbc);
        }

        JTextField animalsField = null;
        if (type.equals("FoodItem")) {
            JLabel animalsLabel = new JLabel("GUI_Animal:");
            animalsField = new JTextField(20);
            gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
            formPanel.add(animalsLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            formPanel.add(animalsField, gbc);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton addButton = new JButton("Add");

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        cancelButton.addActionListener(e -> dialog.dispose());

        JTextField finalExpiryField = expiryField;
        JTextField finalAnimalsField = animalsField;

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String stockText = stockField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter an item name.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int stock;
            try {
                stock = Integer.parseInt(stockText);
                if (stock < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Stock must be a valid non-negative number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (type.equals("FoodItem")) {
                String expiry = finalExpiryField.getText().trim();
                String animals = finalAnimalsField.getText().trim();
                if (expiry.isEmpty() || animals.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                manager.addItem(new FoodItem(name, stock, expiry, animals));
            } else if (type.equals("MedicineItem")) {
                String expiry = finalExpiryField.getText().trim();
                if (expiry.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter the expiry date.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                manager.addItem(new MedicineItem(name, stock, expiry));
            } else if (type.equals("MiscellaneousItem")) {
                manager.addItem(new MiscellaneousItem(name, stock));
            }

            updateTables();
            dialog.dispose();
        });

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    private void updateTables() {
        foodModel.setRowCount(0);
        medicineModel.setRowCount(0);
        miscellaneousModel.setRowCount(0);

        for (ItemData item : manager.getInventory().values()) {
            if (item instanceof FoodItem) {
                FoodItem food = (FoodItem) item;
                foodModel.addRow(new Object[]{food.getName(), food.getStock(), food.getExpiry(), food.getAnimals()});
            } else if (item instanceof MedicineItem) {
                MedicineItem medicine = (MedicineItem) item;
                medicineModel.addRow(new Object[]{medicine.getName(), medicine.getStock(), medicine.getExpiry()});
            } else if (item instanceof MiscellaneousItem) {
                MiscellaneousItem miscellaneous = (MiscellaneousItem) item;
                miscellaneousModel.addRow(new Object[]{miscellaneous.getName(), miscellaneous.getStock()});
            }
        }
    }

    private void addSearchListener(JTextField searchField, TableRowSorter<DefaultTableModel> sorter) {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void search() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
                }
            }

            @Override public void insertUpdate(DocumentEvent e) { search(); }
            @Override public void removeUpdate(DocumentEvent e) { search(); }
            @Override public void changedUpdate(DocumentEvent e) { search(); }
        });
    }

    private void loadPresetInventory() {
        manager.addItem(new FoodItem("Raw Meat (Beef & Poultry)", 450, "2026-11-15", "Tigers, Lions, Wolves"));
        manager.addItem(new FoodItem("Alfalfa Hay Bales", 120, "2027-03-01", "Giraffes, Zebras, Camels"));
        manager.addItem(new FoodItem("Fresh Fruit Basket (Apples/Bananas)", 85, "2026-10-25", "Monkeys, Parrots, Lemurs"));
        manager.addItem(new FoodItem("Capelin & Herrings (Frozen Fish)", 300, "2026-12-10", "Penguins, Sea Lions, Otters"));
        manager.addItem(new FoodItem("Eucalyptus Leaves", 40, "2026-10-18", "Koalas"));

        manager.addItem(new MedicineItem("Multivitamin Chews (Large AnimalData)", 250, "2027-08-30"));
        manager.addItem(new MedicineItem("Broad-Spectrum Antibiotics (100ml)", 45, "2027-01-15"));
        manager.addItem(new MedicineItem("Rabies Vaccine Doses", 60, "2028-05-20"));
        manager.addItem(new MedicineItem("Surgical Bandages & Antiseptic Wipes", 180, "2029-01-01"));
        manager.addItem(new MedicineItem("Avian Flu Injections", 100, "2027-04-12"));

        manager.addItem(new MiscellaneousItem("Heavy-Duty Containment Netting (Meters)", 75));
        manager.addItem(new MiscellaneousItem("HVAC Filter Replacements", 30));
        manager.addItem(new MiscellaneousItem("Aquarium Water Testing Kits", 15));
        manager.addItem(new MiscellaneousItem("Submersible Water Pumps", 8));
        manager.addItem(new MiscellaneousItem("High-Tension Enclosure Wire Locks", 150));

        updateTables();
    }
}

class InventoryManager {
    private HashMap<String, ItemData> inventory;

    InventoryManager() {
        inventory = new HashMap<>();
    }

    void addItem(ItemData item) {
        inventory.put(item.getName(), item);
    }

    ItemData getItem(String name) {
        return inventory.get(name);
    }

    void removeItem(String name) {
        inventory.remove(name);
    }

    HashMap<String, ItemData> getInventory() {
        return inventory;
    }
}