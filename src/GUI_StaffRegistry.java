import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public final class GUI_StaffRegistry extends JPanel {

    private final UserData admin;
    private final ArrayList<UserData> users = Database.getUserList();
    private DefaultTableModel tableModel;
    private JTable staffTable;

    public GUI_StaffRegistry(CardLayout cardLayout, JPanel mainPanel, UserData admin) {
        this.admin = admin;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (admin.getType() != UserData.ROLE.ADMIN) {
            JOptionPane.showMessageDialog(this, "Access Denied. Admins Only.",
                    "Access Error", JOptionPane.ERROR_MESSAGE);
            cardLayout.show(mainPanel, "MAIN");
            return;
        }

        assignMissingEmployeeIds();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel title = new JLabel("STAFF REGISTRY");
        title.setFont(new Font("Arial", Font.BOLD, 25));

        JButton addButton = new JButton("Add Staff");
        JButton removeButton = new JButton("Remove Staff");
        JButton profileButton = new JButton("View / Edit Profile");

        addButton.addActionListener(e -> addStaff());
        removeButton.addActionListener(e -> removeStaff());
        profileButton.addActionListener(e -> openSelectedProfile());

        topPanel.add(title);
        topPanel.add(addButton);
        topPanel.add(removeButton);
        topPanel.add(profileButton);

        String[] columns = {"Username", "Role", "Employee ID", "Employee Name"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        staffTable = new JTable(tableModel);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.setRowHeight(25);

        staffTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    openSelectedProfile();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(staffTable);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        updateTable();
    }

    private void assignMissingEmployeeIds() {
        boolean changed = false;

        for (int i = 0; i < users.size(); i++) {
            UserData user = users.get(i);

            if (user.getEmployeeId() == null || user.getEmployeeId().trim().isEmpty()) {
                user.setEmployeeId(String.format("EMP-%03d", i + 1));
                changed = true;
            }
        }

        if (changed) Database.saveUsers(users);
    }

    private void updateTable() {
        tableModel.setRowCount(0);

        for (UserData user : users) {
            tableModel.addRow(new Object[]{
                user.getUsername(),
                user.getType(),
                user.getEmployeeId(),
                user.getName() == null || user.getName().trim().isEmpty()
                        ? "Not Set" : user.getName()
            });
        }
    }

    private void openSelectedProfile() {
        if (admin.getType() != UserData.ROLE.ADMIN) return;

        int selectedRow = staffTable.getSelectedRow();

        if (selectedRow < 0) {
            showError("Please select an employee from the table first.");
            return;
        }

        UserData selectedUser = users.get(
                staffTable.convertRowIndexToModel(selectedRow)
        );

        GUI_EmployeeProfilePopup profile = new GUI_EmployeeProfilePopup(
                SwingUtilities.getWindowAncestor(this), selectedUser, users);

        profile.setVisible(true);

        updateTable();
    }

    private void addStaff() {
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
        newUser.setEmployeeId(createEmployeeId());

        users.add(newUser);

        Database.saveUsers(users);
        updateTable();

        JOptionPane.showMessageDialog(
                this,
                "Staff Account Successfully Registered!\n"
                        + "You can now select the account and complete its employee profile.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String createEmployeeId() {
        String id;
        boolean exists;

        do {
            id = "EMP-" + System.currentTimeMillis();
            exists = false;

            for (UserData user : users) {
                if (id.equalsIgnoreCase(user.getEmployeeId())) {
                    exists = true;
                    break;
                }
            }

        } while (exists);

        return id;
    }

    private void removeStaff() {
        if (admin.getType() != UserData.ROLE.ADMIN) return;

        int selectedRow = staffTable.getSelectedRow();

        if (selectedRow < 0) {
            showError("Please select a staff account to remove.");
            return;
        }

        UserData selectedUser = users.get(
                staffTable.convertRowIndexToModel(selectedRow)
        );

        if (selectedUser.getUsername().equals(admin.getUsername())) {
            showError("You cannot remove your own account.");
            return;
        }

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
                "Are you sure you want to remove " + selectedUser.getUsername() + "?",
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