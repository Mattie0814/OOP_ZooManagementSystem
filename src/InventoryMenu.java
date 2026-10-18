import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class InventoryMenu extends JPanel implements ActionListener {
    private JButton addInventory;
    private JButton returnButton;

    // Category navigation buttons for inner CardLayout
    private JButton foodTabBtn;
    private JButton medicineTabBtn;
    private JButton maintenanceTabBtn;

    // Inventory manager
    private InventoryManager manager;

    // Inner CardLayout for switching inventory categories
    private CardLayout tableCardLayout;
    private JPanel tableCardsPanel;
    private String currentCategory = "Food";

    // Outer CardLayout controls
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Tables
    private JTable foodTable;
    private JTable medicineTable;
    private JTable maintenanceTable;

    // Table models
    private DefaultTableModel foodModel;
    private DefaultTableModel medicineModel;
    private DefaultTableModel maintenanceModel;

    // Search fields
    private JTextField foodSearch;
    private JTextField medicineSearch;
    private JTextField maintenanceSearch;

    // Table sorters
    private TableRowSorter<DefaultTableModel> foodSorter;
    private TableRowSorter<DefaultTableModel> medicineSorter;
    private TableRowSorter<DefaultTableModel> maintenanceSorter;

    public InventoryMenu(CardLayout cardLayout, JPanel mainPanel) {
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
        foodTabBtn = new JButton("Food");
        medicineTabBtn = new JButton("Medicine");
        maintenanceTabBtn = new JButton("Maintenance");

        foodTabBtn.addActionListener(e -> switchCategory("Food"));
        medicineTabBtn.addActionListener(e -> switchCategory("Medicine"));
        maintenanceTabBtn.addActionListener(e -> switchCategory("Maintenance"));

        categoryNav.add(foodTabBtn);
        categoryNav.add(medicineTabBtn);
        categoryNav.add(maintenanceTabBtn);

        topPanel.add(leftActions, BorderLayout.WEST);
        topPanel.add(categoryNav, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // CENTER CARDS PANEL (CardLayout for Food, Meds, Maintenance)
        // -------------------------------------------------------------
        tableCardLayout = new CardLayout();
        tableCardsPanel = new JPanel(tableCardLayout);

        // 1. Food Panel
        JPanel foodPanel = new JPanel(new BorderLayout(0, 10));
        foodSearch = new JTextField();
        foodSearch.setBorder(BorderFactory.createTitledBorder("Search Food Inventory"));
        foodPanel.add(foodSearch, BorderLayout.NORTH);

        foodModel = new DefaultTableModel(new Object[]{"Name", "Stock", "Expiry", "Animals"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        foodTable = new JTable(foodModel);
        foodSorter = new TableRowSorter<>(foodModel);
        foodTable.setRowSorter(foodSorter);
        foodPanel.add(new JScrollPane(foodTable), BorderLayout.CENTER);

        // 2. Medicine Panel
        JPanel medicinePanel = new JPanel(new BorderLayout(0, 10));
        medicineSearch = new JTextField();
        medicineSearch.setBorder(BorderFactory.createTitledBorder("Search Medicine Inventory"));
        medicinePanel.add(medicineSearch, BorderLayout.NORTH);

        medicineModel = new DefaultTableModel(new Object[]{"Name", "Stock", "Expiry"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        medicineTable = new JTable(medicineModel);
        medicineSorter = new TableRowSorter<>(medicineModel);
        medicineTable.setRowSorter(medicineSorter);
        medicinePanel.add(new JScrollPane(medicineTable), BorderLayout.CENTER);

        // 3. Maintenance Panel
        JPanel maintenancePanel = new JPanel(new BorderLayout(0, 10));
        maintenanceSearch = new JTextField();
        maintenanceSearch.setBorder(BorderFactory.createTitledBorder("Search Maintenance Inventory"));
        maintenancePanel.add(maintenanceSearch, BorderLayout.NORTH);

        maintenanceModel = new DefaultTableModel(new Object[]{"Name", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        maintenanceTable = new JTable(maintenanceModel);
        maintenanceSorter = new TableRowSorter<>(maintenanceModel);
        maintenanceTable.setRowSorter(maintenanceSorter);
        maintenancePanel.add(new JScrollPane(maintenanceTable), BorderLayout.CENTER);

        // Add views to inner CardLayout
        tableCardsPanel.add(foodPanel, "Food");
        tableCardsPanel.add(medicinePanel, "Medicine");
        tableCardsPanel.add(maintenancePanel, "Maintenance");

        add(tableCardsPanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // SEARCH LISTENERS
        // -------------------------------------------------------------
        addSearchListener(foodSearch, foodSorter);
        addSearchListener(medicineSearch, medicineSorter);
        addSearchListener(maintenanceSearch, maintenanceSorter);

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
        if (type.equals("Food") || type.equals("Medicine")) {
            JLabel expiryLabel = new JLabel("Expiry:");
            expiryField = new JTextField(20);
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
            formPanel.add(expiryLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            formPanel.add(expiryField, gbc);
        }

        JTextField animalsField = null;
        if (type.equals("Food")) {
            JLabel animalsLabel = new JLabel("Animals:");
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

            if (type.equals("Food")) {
                String expiry = finalExpiryField.getText().trim();
                String animals = finalAnimalsField.getText().trim();
                if (expiry.isEmpty() || animals.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                manager.addItem(new Food(name, stock, expiry, animals));
            } else if (type.equals("Medicine")) {
                String expiry = finalExpiryField.getText().trim();
                if (expiry.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter the expiry date.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                manager.addItem(new Medicine(name, stock, expiry));
            } else if (type.equals("Maintenance")) {
                manager.addItem(new Maintenance(name, stock));
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
        maintenanceModel.setRowCount(0);

        for (Inventory item : manager.getInventory().values()) {
            if (item instanceof Food) {
                Food food = (Food) item;
                foodModel.addRow(new Object[]{food.getName(), food.getStock(), food.getExpiry(), food.getAnimals()});
            } else if (item instanceof Medicine) {
                Medicine medicine = (Medicine) item;
                medicineModel.addRow(new Object[]{medicine.getName(), medicine.getStock(), medicine.getExpiry()});
            } else if (item instanceof Maintenance) {
                Maintenance maintenance = (Maintenance) item;
                maintenanceModel.addRow(new Object[]{maintenance.getName(), maintenance.getStock()});
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
        manager.addItem(new Food("Raw Meat (Beef & Poultry)", 450, "2026-11-15", "Tigers, Lions, Wolves"));
        manager.addItem(new Food("Alfalfa Hay Bales", 120, "2027-03-01", "Giraffes, Zebras, Camels"));
        manager.addItem(new Food("Fresh Fruit Basket (Apples/Bananas)", 85, "2026-10-25", "Monkeys, Parrots, Lemurs"));
        manager.addItem(new Food("Capelin & Herrings (Frozen Fish)", 300, "2026-12-10", "Penguins, Sea Lions, Otters"));
        manager.addItem(new Food("Eucalyptus Leaves", 40, "2026-10-18", "Koalas"));

        manager.addItem(new Medicine("Multivitamin Chews (Large Animal)", 250, "2027-08-30"));
        manager.addItem(new Medicine("Broad-Spectrum Antibiotics (100ml)", 45, "2027-01-15"));
        manager.addItem(new Medicine("Rabies Vaccine Doses", 60, "2028-05-20"));
        manager.addItem(new Medicine("Surgical Bandages & Antiseptic Wipes", 180, "2029-01-01"));
        manager.addItem(new Medicine("Avian Flu Injections", 100, "2027-04-12"));

        manager.addItem(new Maintenance("Heavy-Duty Containment Netting (Meters)", 75));
        manager.addItem(new Maintenance("HVAC Filter Replacements", 30));
        manager.addItem(new Maintenance("Aquarium Water Testing Kits", 15));
        manager.addItem(new Maintenance("Submersible Water Pumps", 8));
        manager.addItem(new Maintenance("High-Tension Enclosure Wire Locks", 150));

        updateTables();
    }
}

// -------------------------------------------------------------
// DOMAIN MODELS & MANAGER
// -------------------------------------------------------------
class Inventory {
    private String name;
    private int stock;

    Inventory(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    String getName() { return name; }
    int getStock() { return stock; }
    void setStock(int stock) { this.stock = stock; }
}

class Medicine extends Inventory {
    private String expiry;

    Medicine(String name, int stock, String expiry) {
        super(name, stock);
        this.expiry = expiry;
    }

    String getExpiry() { return expiry; }
}

class Maintenance extends Inventory {
    Maintenance(String name, int stock) {
        super(name, stock);
    }
}

class Food extends Inventory {
    private String expiry;
    private String animals;

    Food(String name, int stock, String expiry, String animals) {
        super(name, stock);
        this.expiry = expiry;
        this.animals = animals;
    }

    String getExpiry() { return expiry; }
    String getAnimals() { return animals; }
}

class InventoryManager {
    private HashMap<String, Inventory> inventory;

    InventoryManager() {
        inventory = new HashMap<>();
    }

    void addItem(Inventory item) {
        inventory.put(item.getName(), item);
    }

    Inventory getItem(String name) {
        return inventory.get(name);
    }

    void removeItem(String name) {
        inventory.remove(name);
    }

    HashMap<String, Inventory> getInventory() {
        return inventory;
    }
}