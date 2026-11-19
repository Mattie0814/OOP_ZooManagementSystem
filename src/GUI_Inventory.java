import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

public final class GUI_Inventory extends JPanel {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    //Categories
    private static final String FOOD = "Food";
    private static final String MEDICINE = "Medicine";
    private static final String MISC = "Miscellaneous";
    private static final String ALL = "All";

    private final InventoryManager manager = new InventoryManager();

    private final CardLayout tableCardLayout = new CardLayout();
    private final JPanel tableCardsPanel = new JPanel(tableCardLayout);

    private String currentCategory = FOOD;

    private final ArrayList<ItemData> inventory = Database.getInventoryList();

    private final Map<String, JTable> tables = new LinkedHashMap<>();

    //Table models
    private final DefaultTableModel foodModel = createModel("Name", "Stock", "Date Procured", "Animals", "ID");
    private final DefaultTableModel medicineModel = createModel("Name", "Stock", "Date Procured", "ID");
    private final DefaultTableModel miscellaneousModel = createModel("Name", "Stock", "Date Procured", "ID");
    private final DefaultTableModel allModel = createModel("Name", "Stock", "Date Procured", "Animals", "ID", "Type");

    public GUI_Inventory(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel leftActions = new JPanel();
        leftActions.setLayout(new BoxLayout(leftActions, BoxLayout.Y_AXIS));

        JPanel topActions = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 0)
        );

        //Button for adding inventory
        JButton addInventory = new JButton("Add Item");
        addInventory.addActionListener(e -> {
            if (currentCategory.equals(ALL)) {
                String[] categories = {FOOD, MEDICINE, MISC};
                String selected = (String) JOptionPane.showInputDialog(
                    this,
                    "Select item category: ",
                    "Add Item",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    categories,
                    FOOD
                );
                if (selected != null) {
                    showAddWindow(selected);
                }
            }
            else {
                showAddWindow(currentCategory);
            }
        });

        topActions.add(addInventory);

        JPanel bottomActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        //Delete Button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setContentAreaFilled(true);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteItems());
        bottomActions.add(deleteButton);

        //Update Button
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateItems());
        bottomActions.add(updateButton);

        leftActions.add(topActions);
        leftActions.add(bottomActions);

        JPanel categoryNav = new JPanel();
        categoryNav.setLayout(new BoxLayout(categoryNav, BoxLayout.Y_AXIS));

        //Buttons for inventory type
        JPanel categoryButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        for (String category : new String[]{FOOD, MEDICINE, MISC}) {
            JButton tabButton = new JButton(category);
            tabButton.addActionListener(e -> switchCategory(category));
            categoryButtons.add(tabButton);
        }

        //All button
        JPanel allButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton allButton = new JButton("All");
        allButton.addActionListener(e -> switchCategory(ALL));
        allButtonPanel.add(allButton);

        categoryNav.add(categoryButtons);
        categoryNav.add(allButtonPanel);

        topPanel.add(leftActions, BorderLayout.WEST);
        topPanel.add(categoryNav, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        //table panel for each category
        tableCardsPanel.add(createCategoryPanel(FOOD, foodModel), FOOD);
        tableCardsPanel.add(createCategoryPanel(MEDICINE, medicineModel), MEDICINE);
        tableCardsPanel.add(createCategoryPanel(MISC, miscellaneousModel), MISC);
        tableCardsPanel.add(createCategoryPanel(ALL, allModel), ALL);

        add(tableCardsPanel, BorderLayout.CENTER);

        //loads data and populates table
        if (inventory != null) {
            for (ItemData item : inventory) {
                manager.addItem(item);
            }
        }

        updateTables();
    }

    //Creates table model
    private static DefaultTableModel createModel(Object... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }

            @Override
            public Class <?> getColumnClass(int column){
            	if(column == 1) {
            		return Integer.class;
            	}
            	return String.class;
            }
        };
    }

    private JPanel createCategoryPanel(String category, DefaultTableModel model) {
        //Search bar and title
    	JPanel panel = new JPanel(new BorderLayout(0, 10));
        JTextField searchField = new JTextField(20);
        JPanel searchBox = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();
        JLabel inventoryTitle = new JLabel(category + " Inventory");
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        inventoryTitle.setFont(new Font("Arial", Font.BOLD, 40));
        inventoryTitle.setForeground(Color.BLACK);
        inventoryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        inventoryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(inventoryTitle);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(searchPanel);
        panel.add(topPanel, BorderLayout.NORTH);

        //default text placeholder
        searchField.setText("Search...");
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                //removes place holder
            	if (searchField.getText().equals("Search...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
            	//restores place holder
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        searchField.setPreferredSize(new Dimension(250, 32));
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        //Clear Button
        JButton clearButton = new JButton("×");
        clearButton.setBackground(Color.WHITE);
        clearButton.setForeground(Color.BLACK);
        clearButton.setOpaque(true);
        clearButton.setContentAreaFilled(true);
        clearButton.setBorderPainted(false);
        clearButton.setFocusable(false);
        clearButton.setFont(new Font("Arial", Font.BOLD, 18));
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        clearButton.getModel().addChangeListener(e -> {
        	ButtonModel mdl = clearButton.getModel();
            if (mdl.isPressed() && mdl.isArmed()) {
            	clearButton.setBackground(Color.RED);
                clearButton.setForeground(Color.WHITE);
            } else {
            	clearButton.setBackground(Color.WHITE);
                clearButton.setForeground(Color.BLACK);
            }
        });
        clearButton.addActionListener(e -> searchField.setText(""));

        searchBox.setPreferredSize(new Dimension(280, 32));
        searchBox.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        searchBox.add(searchField, BorderLayout.CENTER);
        searchBox.add(clearButton, BorderLayout.EAST);
        searchPanel.add(searchBox);

        //Inventory table
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        tables.put(category, table);

        table.setFillsViewportHeight(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row == -1) {
                        table.clearSelection();
                    }
                }
            }
        });

        //Highlight 0 stock
        DefaultTableCellRenderer stockRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                int modelRow = table.convertRowIndexToModel(row);
                int stock = Integer.parseInt(table.getModel().getValueAt(modelRow, 1).toString());

                if (!isSelected)
                    c.setBackground(stock == 0 ? Color.PINK : Color.WHITE);

                return c;
            }
        };
        table.setDefaultRenderer(Object.class, stockRenderer);
        table.setDefaultRenderer(Integer.class, stockRenderer);

        //Sorts table
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setSortable(2, false);
        if (category.equals(FOOD)) {
            sorter.setSortable(4, false);
        }
        else if (category.equals(MEDICINE) || category.equals(MISC)) {
            sorter.setSortable(3, false);
        }
        else if (category.equals(ALL)) {
        	sorter.setSortable(4, false);
        	sorter.setSortable(5, false);
        }

        table.setRowSorter(sorter);
        installItemPopup(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        addSearchListener(searchField, sorter);
        return panel;
    }

    private void installItemPopup(JTable table) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int column = table.columnAtPoint(e.getPoint());

            //Normal right click
            if (SwingUtilities.isRightMouseButton(e)) {
                if (row == -1 || column == -1) {
                    return;
                }

                table.setRowSelectionInterval(row, row);

                int modelRow = table.convertRowIndexToModel(row);
                int idColumn = table.getColumnModel().getColumnIndex("ID");
                String ID = table.getModel().getValueAt(modelRow, idColumn).toString();
                ItemData item = manager.getItem(ID);

                //Delete and U[date Options
                JPopupMenu menu = new JPopupMenu();
                JMenuItem deleteOption = new JMenuItem("Delete");
                JMenuItem updateOption = new JMenuItem("Update");

                deleteOption.addActionListener(a -> deleteSingleItem(item));
                updateOption.addActionListener(a -> showEditWindow(item));

                menu.add(deleteOption);
                menu.add(updateOption);

                menu.show(table, e.getX(), e.getY());

                return;
            }

            // Normal left click
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (e.isControlDown() || e.isShiftDown()) {
                    return;
                }
                if (row == -1 || column == -1) {
                    return;
                }
                int modelRow = table.convertRowIndexToModel(row);
                //Item Details
                SwingUtilities.invokeLater(() -> {
                    showItemDetailsPopup(table, modelRow, e.getX(), e.getY());
                });
            }
            }
        });
    }

    //Item details pop up
    private void showItemDetailsPopup(JTable table, int modelRow, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );

        for (int column = 0; column < table.getModel().getColumnCount(); column++) {
            String columnName = table.getModel().getColumnName(column);
            Object value = table.getModel().getValueAt(modelRow, column);
            JLabel label = new JLabel(columnName + ": " + value);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (columnName.equals("Stock") && Integer.parseInt(value.toString()) == 0) {
                label.setOpaque(true);
                label.setBackground(Color.PINK);
                label.setForeground(Color.BLACK);
                label.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
            }
            detailsPanel.add(label);
        }

        JLabel updateLabel = new JLabel(
        	    "Update: " + manager.getItem(
        	        table.getModel().getValueAt(modelRow,
        	            table.getModel().getColumnCount() == 5 ? 4 :
        	            table.getModel().getColumnCount() == 6 ? 4 : 3
        	        ).toString()
        	    ).getAutoUpdate()
        	);

        	updateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        	detailsPanel.add(updateLabel);

        popup.add(detailsPanel);
        popup.show(table, x, y);
    }

    //Switches displayed table
    private void switchCategory(String category) {
        currentCategory = category;
        tableCardLayout.show(tableCardsPanel, category);
    }

    private void showAddWindow(String type) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Add " + type + " Item", Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setSize(440, 340);
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

        JLabel procureLabel = new JLabel("Date Procured (MM/DD/YYYY):");
        DateInputPanel procureDatePanel = new DateInputPanel();
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(procureLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(procureDatePanel, gbc);

        JLabel updateLabel = new JLabel("Update Amount:");
        JTextField updateField = new JTextField(20);
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(updateLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(updateField, gbc);

        JTextField animalsField = null;
        if (type.equals(FOOD)) {
            JLabel animalsLabel = new JLabel("Animals:");
            animalsField = new JTextField(20);
            gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
            formPanel.add(animalsLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            formPanel.add(animalsField, gbc);
        }

        // Cancel and Add Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton addButton = new JButton("Add");

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        cancelButton.addActionListener(e -> dialog.dispose());

        JTextField finalAnimalsField = animalsField;
        JTextField finalUpdateField = updateField;

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

            String updateText = finalUpdateField.getText().trim();
            int update;
            try {
                update = Integer.parseInt(updateText);
                if (update < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Update must be a valid non-negative number", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String dateProcured = procureDatePanel.getFormattedDate(DATE_FORMATTER);

            if (type.equals(FOOD)) {
                String animals = finalAnimalsField.getText().trim();
                if (animals.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String ID;
                do {
                    ID = FoodItem.foodID();
                } while (manager.getItem(ID) != null);
                manager.addItem(new FoodItem(name, stock, dateProcured, animals, ID, update));
            } else if (type.equals(MEDICINE)) {
                String ID;
                do {
                    ID = MedicineItem.medicineID();
                } while (manager.getItem(ID) != null);
                manager.addItem(new MedicineItem(name, stock, dateProcured, ID, update));
            } else {
                String ID;
                do {
                    ID = MiscellaneousItem.miscellaneousID();
                } while (manager.getItem(ID) != null);
                manager.addItem(new MiscellaneousItem(name, stock, dateProcured, ID, update));
            }

            Database.saveInventory(
                    new ArrayList<>(manager.getInventory().values())
            );

            updateTables();
            dialog.dispose();
        });

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    private void showEditWindow(ItemData item) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        String type;

        if (item instanceof FoodItem) {
            type = FOOD;
        } else if (item instanceof MedicineItem) {
            type = MEDICINE;
        } else {
            type = MISC;
        }

        JDialog dialog = new JDialog(
                parentWindow,
                "Update " + type + " Item",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setSize(440, 390);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setResizable(false);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Prevents ID edit
        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField(item.getID(), 20);
        idField.setEditable(false);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(idLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(idField, gbc);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(item.getName(), 20);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(nameField, gbc);

        // Stock
        JLabel stockLabel = new JLabel("Stock:");
        JTextField stockField = new JTextField(
                String.valueOf(item.getStock()), 20
        );

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(stockLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(stockField, gbc);

        // Date Procured
        JLabel dateLabel = new JLabel("Date Procured (MM/DD/YYYY):");
        DateInputPanel datePanel = new DateInputPanel(parseOrDefaultDate(item.getDateProcured()));

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(dateLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(datePanel, gbc);

        // Update Amount
        JLabel updateLabel = new JLabel("Update Amount:");
        JTextField updateField = new JTextField(
                String.valueOf(item.getAutoUpdate()), 20
        );

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(updateLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(updateField, gbc);

        // Animals (Food only)
        JTextField animalsField = null;

        if (item instanceof FoodItem) {
            FoodItem food = (FoodItem) item;

            JLabel animalsLabel = new JLabel("Animals:");
            animalsField = new JTextField(food.getAnimals(), 20);

            gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
            formPanel.add(animalsLabel, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            formPanel.add(animalsField, gbc);
        }

        // Cancel and save buttons
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save Changes");

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        cancelButton.addActionListener(e -> dialog.dispose());

        JTextField finalAnimalsField = animalsField;

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter an item name.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int stock;
            int update;

            try {
                stock = Integer.parseInt(stockField.getText().trim());
                if (stock < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Stock must be a valid non-negative number.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                update = Integer.parseInt(updateField.getText().trim());
                if (update < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Update must be a valid non-negative number.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String date = datePanel.getFormattedDate(DATE_FORMATTER);

            if (item instanceof FoodItem) {
                String animals = finalAnimalsField.getText().trim();
                if (animals.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            "Please enter the animals.",
                            "Invalid Input",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                FoodItem food = (FoodItem) item;
                food.setAnimals(animals);
            }

            item.setName(name);
            item.setStock(stock);
            item.setDateProcured(date);
            item.setAutoUpdate(update);

            Database.saveInventory(
                    new ArrayList<>(manager.getInventory().values())
            );

            updateTables();
            dialog.dispose();

            JOptionPane.showMessageDialog(
                    this,
                    "Item successfully updated!",
                    "Update Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    private LocalDate parseOrDefaultDate(String rawDate) {
        if (rawDate == null || rawDate.trim().isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(rawDate.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            return LocalDate.now();
        }
    }

    //Delete for delete button
    private void deleteItems() {
        JTable table = tables.get(currentCategory);

        if (table == null) {
            return;
        }

        int[] selectedRows = table.getSelectedRows();

        //Rows selected
        if (selectedRows.length > 0) {
        	//Confirmation for delete
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + selectedRows.length + " selected item(s)?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            ArrayList<String> IDs = new ArrayList<>();

            int idColumn = currentCategory.equals(ALL) || currentCategory.equals(FOOD) ? 4 : 3;

            for (int row : selectedRows) {
                int modelRow = table.convertRowIndexToModel(row);
                String ID = table.getModel().getValueAt(modelRow, idColumn).toString();
                IDs.add(ID);
            }

            //Removes selected items
            for (String ID : IDs) {
                manager.removeItem(ID);
            }

        }

        //When no rows selected
        else {
            ArrayList<String> IDs = new ArrayList<>();

            for (ItemData item : manager.getInventory().values()) {
                //check if item stock is 0
            	if (item.getStock() == 0) {
            		//check item type
                    boolean matchesCategory =
                        currentCategory.equals(ALL) ||
                        (currentCategory.equals(FOOD) &&
                         item instanceof FoodItem) ||
                        (currentCategory.equals(MEDICINE) &&
                         item instanceof MedicineItem) ||
                        (currentCategory.equals(MISC) &&
                         item instanceof MiscellaneousItem);

                    if (matchesCategory) {
                        IDs.add(item.getID());
                    }
                }
            }

            //Check if any zero stock
            if (IDs.isEmpty()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Nothing to delete. There are no zero-stock items.",
                    "Nothing to Delete",
                    JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            //Delete confirmation of 0 stock items
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "There are " + IDs.size()
                    + " item(s) with zero stock.\n"
                    + "Do you want to delete them?",
                "Delete Zero-Stock Items",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            //removes items
            for (String ID : IDs) {
                manager.removeItem(ID);
            }
        }

        Database.saveInventory(
            new ArrayList<>(manager.getInventory().values())
        );

        updateTables();

        table.clearSelection();

    }

    //Delete for right click delete option
    private void deleteSingleItem(ItemData item) {
        //Confirmation for item delete
    	int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this item?\n\n"
                + "Name: " + item.getName()
                + "\nID: " + item.getID(),
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        //deletes item
        manager.removeItem(item.getID());

        Database.saveInventory(
            new ArrayList<>(manager.getInventory().values())
        );

        updateTables();

        JOptionPane.showMessageDialog(
            this,
            "Item successfully deleted!",
            "Delete Complete",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void updateItems() {
        ArrayList<ItemData> itemsToUpdate = new ArrayList<>();
        //Find Item type
        for (ItemData item : manager.getInventory().values()) {

            boolean matchesCategory =
                currentCategory.equals(ALL) ||
                (currentCategory.equals(FOOD) &&
                 item instanceof FoodItem) ||
                (currentCategory.equals(MEDICINE) &&
                 item instanceof MedicineItem) ||
                (currentCategory.equals(MISC) &&
                 item instanceof MiscellaneousItem);

            if (matchesCategory) {
                itemsToUpdate.add(item);
            }
        }

        if (itemsToUpdate.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "There are no items to update.",
                "Nothing to Update",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // Update confirmation
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to update the stock of all " + currentCategory + " items?",
            "Confirm Stock Update",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        StringBuilder alreadyZero = new StringBuilder();
        StringBuilder willBeZero = new StringBuilder();

        // 0 or will be 0 items checker
        for (ItemData item : itemsToUpdate) {

            int stock = item.getStock();
            int update = item.getAutoUpdate();

            if (update == 0) {
            	continue;
            }

            if (stock == 0) {

                alreadyZero.append("Name: ")
                    .append(item.getName())
                    .append("\nID: ")
                    .append(item.getID())
                    .append("\n\n");

            }

            else if (stock - update <= 0) {

                willBeZero.append("Name: ")
                    .append(item.getName())
                    .append("\nID: ")
                    .append(item.getID())
                    .append("\n\n");
            }
        }

        // warning for 0 stock items
        if (alreadyZero.length() > 0) {

            JOptionPane.showMessageDialog(
                this,
                "The following items already have zero stock:\n\n"
                    + alreadyZero,
                "Items Already at Zero",
                JOptionPane.WARNING_MESSAGE
            );
        }

        // warning for to be 0 stock items
        if (willBeZero.length() > 0) {

            JOptionPane.showMessageDialog(
                this,
                "The following items will have zero stock after updating:\n\n"
                    + willBeZero,
                "Items Reaching Zero",
                JOptionPane.WARNING_MESSAGE
            );
        }
        boolean updated = false;

        // decrease stock
        for (ItemData item : itemsToUpdate) {

        	int stock = item.getStock();
            int update = item.getAutoUpdate();

            // Skip 0 update amounts
            if (update == 0 || stock == 0) {
                continue;
            }

            int newStock = Math.max(0, stock - update);
            item.setStock(newStock);
            updated = true;
        }

        if(!updated) {
        	JOptionPane.showMessageDialog(
                this,
                "No items were updated. All applicable items "
                    + "have zero stock or zero update amounts.",
                "Nothing to Update",
                JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        Database.saveInventory(
            new ArrayList<>(manager.getInventory().values())
        );

        updateTables();

        JOptionPane.showMessageDialog(
            this,
            "Stock successfully updated!",
            "Update Complete",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    //refreshes table
    private void updateTables() {
        foodModel.setRowCount(0);
        medicineModel.setRowCount(0);
        miscellaneousModel.setRowCount(0);
        allModel.setRowCount(0);

        for (ItemData item : manager.getInventory().values()) {
            if (item instanceof FoodItem) {
                FoodItem food = (FoodItem) item;
                foodModel.addRow(new Object[]{food.getName(), food.getStock(), food.getDateProcured(), food.getAnimals(), food.getID()});
                allModel.addRow(new Object[]{food.getName(), food.getStock(), food.getDateProcured(), food.getAnimals(), food.getID(), "Food"});
            } else if (item instanceof MedicineItem) {
                MedicineItem medicine = (MedicineItem) item;
                medicineModel.addRow(new Object[]{medicine.getName(), medicine.getStock(), medicine.getDateProcured(), medicine.getID()});
                allModel.addRow(new Object[]{medicine.getName(), medicine.getStock(), medicine.getDateProcured(), "N/A", medicine.getID(), "Medicine"});
            } else if (item instanceof MiscellaneousItem) {
                miscellaneousModel.addRow(new Object[]{item.getName(), item.getStock(), item.getDateProcured(), item.getID()});
                allModel.addRow(new Object[] {item.getName(), item.getStock(), item.getDateProcured(), "N/A", item.getID(), "Miscellaneous"});
            }
        }
    }

    private void addSearchListener(JTextField searchField, TableRowSorter<DefaultTableModel> sorter) {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void search() {
                String text = searchField.getText().trim();
                if(text.equals("Search...")) {
                	text = "";
                }
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
}

//Manages inventory
class InventoryManager {
    private final Map<String, ItemData> inventory = new LinkedHashMap<>();

    void addItem(ItemData item) {
        inventory.put(item.getID(), item);
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