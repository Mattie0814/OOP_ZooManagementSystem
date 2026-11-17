import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public final class GUI_StaffRegistry extends JPanel {

    private final UserData admin;

    private final ArrayList<UserData> users = Database.loadUsers();

    private DefaultTableModel tableModel;

    public GUI_StaffRegistry(CardLayout cardLayout, JPanel mainPanel, UserData admin) {
        this.admin = admin;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Check if the current user is an admin
        if (admin.getType() != UserData.ROLE.ADMIN) {
            JOptionPane.showMessageDialog(
                    this,
                    "Access Denied. Admins Only.",
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE
            );
            cardLayout.show(mainPanel, "MAIN");
            return;
        }

        // TOP PANEL
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JLabel title = new JLabel("STAFF REGISTRY");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        JButton addButton = new JButton("Add Staff");
        JButton removeButton = new JButton("Remove Staff");
        JButton returnButton = new JButton("Return to Main");

        addButton.addActionListener(e -> addStaff());
        removeButton.addActionListener(e -> removeStaff());
        returnButton.addActionListener(e -> cardLayout.show(mainPanel, "MAIN"));

        topPanel.add(title);
        topPanel.add(addButton);
        topPanel.add(removeButton);
        topPanel.add(returnButton);

        // STAFF TABLE
        String[] columns = {"Username", "Role"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable staffTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(staffTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        updateTable();
    }

    // DISPLAY ALL STAFF
    private void updateTable() {
        tableModel.setRowCount(0);

        for (UserData user : users) {
            tableModel.addRow(new Object[]{
                    user.getUsername(),
                    user.getType()
            });
        }
    }

    // ADD STAFF
    private void addStaff() {

        // Double-check admin access
        if (admin.getType() != UserData.ROLE.ADMIN) return;

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JComboBox<String> roleBox = new JComboBox<>(
                new String[]{"STAFF", "ZOOKEEPER"}
        );

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleBox);

        int option = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Register New Staff",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) return;

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        // Check duplicate usernames
        for (UserData user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                showError("Username already exists.");
                return;
            }
        }

        UserData.ROLE role = UserData.ROLE.valueOf(
                (String) roleBox.getSelectedItem()
        );

        UserData newUser = new UserData(username, password, role);

        users.add(newUser);

        Database.saveUsers(users);

        updateTable();

        JOptionPane.showMessageDialog(
                this,
                "Staff Account Successfully Registered!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // REMOVE STAFF
    private void removeStaff() {

        // Double-check admin access
        if (admin.getType() != UserData.ROLE.ADMIN) return;

        int selectedRow = JOptionPane.showConfirmDialog(
                this,
                "Remove a staff account?",
                "Remove Staff",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (selectedRow != JOptionPane.OK_OPTION) return;

        String username = JOptionPane.showInputDialog(
                this,
                "Enter the username to remove:"
        );

        if (username == null || username.trim().isEmpty()) return;

        UserData selectedUser = null;

        for (UserData user : users) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                selectedUser = user;
                break;
            }
        }

        if (selectedUser == null) {
            showError("Staff account not found.");
            return;
        }

        // Prevent deleting the currently logged-in account
        if (selectedUser.getUsername().equals(admin.getUsername())) {
            showError("You cannot remove your own account.");
            return;
        }

        // Prevent deleting the last admin account
        if (selectedUser.getType() == UserData.ROLE.ADMIN) {
            int adminCount = 0;

            for (UserData user : users) {
                if (user.getType() == UserData.ROLE.ADMIN) {
                    adminCount++;
                }
            }

            if (adminCount <= 1) {
                showError("At least one admin account must remain.");
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove " +
                        selectedUser.getUsername() + "?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        users.remove(selectedUser);

        Database.saveUsers(users);

        updateTable();

        JOptionPane.showMessageDialog(
                this,
                "Staff Account Successfully Removed.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
