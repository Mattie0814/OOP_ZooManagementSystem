import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public final class GUI_Inventory extends JPanel {
    private static final String FOOD = "Food";
    private static final String MEDICINE = "Medicine";
    private static final String MISC = "Miscellaneous";

    private final InventoryManager manager = new InventoryManager();

    private final CardLayout tableCardLayout = new CardLayout();
    private final JPanel tableCardsPanel = new JPanel(tableCardLayout);
    private String currentCategory = FOOD;

    private final ArrayList<ItemData> inventory = Database.loadInventory();

    private final DefaultTableModel foodModel = createModel("Name", "Stock", "Expiry", "Animals");
    private final DefaultTableModel medicineModel = createModel("Name", "Stock", "Expiry");
    private final DefaultTableModel miscellaneousModel = createModel("Name", "Stock");

    public GUI_Inventory(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton addInventory = new JButton("Add Item");
        addInventory.addActionListener(e -> showAddWindow(currentCategory));

        JButton returnButton = new JButton("Return to Main");
        returnButton.addActionListener(e -> cardLayout.show(mainPanel, "MAIN"));

        leftActions.add(addInventory);
        leftActions.add(returnButton);

        JPanel categoryNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        for (String category : new String[]{FOOD, MEDICINE, MISC}) {
            JButton tabButton = new JButton(category);
            tabButton.addActionListener(e -> switchCategory(category));
            categoryNav.add(tabButton);
        }

        topPanel.add(leftActions, BorderLayout.WEST);
        topPanel.add(categoryNav, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        tableCardsPanel.add(createCategoryPanel(FOOD, foodModel), FOOD);
        tableCardsPanel.add(createCategoryPanel(MEDICINE, medicineModel), MEDICINE);
        tableCardsPanel.add(createCategoryPanel(MISC, miscellaneousModel), MISC);

        add(tableCardsPanel, BorderLayout.CENTER);

        if (inventory != null) {
            for (ItemData item : inventory) {
                manager.addItem(item);
            }
        }

        updateTables();
    }

    private static DefaultTableModel createModel(Object... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private JPanel createCategoryPanel(String category, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));

        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createTitledBorder("Search " + category + " Inventory"));
        panel.add(searchField, BorderLayout.NORTH);

        JTable table = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        addSearchListener(searchField, sorter);
        return panel;
    }

    private void switchCategory(String category) {
        currentCategory = category;
        tableCardLayout.show(tableCardsPanel, category);
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
        if (type.equals(FOOD) || type.equals(MEDICINE)) {
            JLabel expiryLabel = new JLabel("Expiry:");
            expiryField = new JTextField(20);
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
            formPanel.add(expiryLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            formPanel.add(expiryField, gbc);
        }

        JTextField animalsField = null;
        if (type.equals(FOOD)) {
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

            if (manager.getItem(name) != null) {
                JOptionPane.showMessageDialog(dialog, "An item with this name already exists.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
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

            if (type.equals(FOOD)) {
                String expiry = finalExpiryField.getText().trim();
                String animals = finalAnimalsField.getText().trim();
                if (expiry.isEmpty() || animals.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                manager.addItem(new FoodItem(name, stock, expiry, animals));
            } else if (type.equals(MEDICINE)) {
                String expiry = finalExpiryField.getText().trim();
                if (expiry.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter the expiry date.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                manager.addItem(new MedicineItem(name, stock, expiry));
            } else {
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
                miscellaneousModel.addRow(new Object[]{item.getName(), item.getStock()});
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

        manager.addItem(new MedicineItem("Multivitamin Chews (Large Animals)", 250, "2027-08-30"));
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
    private final Map<String, ItemData> inventory = new LinkedHashMap<>();

    void addItem(ItemData item) {
        inventory.put(item.getName(), item);
    }

    ItemData getItem(String name) {
        return inventory.get(name);
    }

    void removeItem(String name) {
        inventory.remove(name);
    }

    Map<String, ItemData> getInventory() {
        return inventory;
    }
}