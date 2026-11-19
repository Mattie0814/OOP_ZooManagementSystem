import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public final class GUI_EmployeeProfile extends JPanel {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final UserData employee;
    private final ArrayList<UserData> users;

    private final JTextField employeeIdField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField contactField = new JTextField();
    private final JTextField emergencyField = new JTextField();
    private final DateInputPanel birthdateField = new DateInputPanel();
    private final JTextField sexGenderField = new JTextField();
    private final JTextField civilStatusField = new JTextField();
    private final JTextField workHoursField = new JTextField();
    private final JTextField salaryField = new JTextField();

    private final JLabel pictureLabel = new JLabel("No Picture Selected", JLabel.CENTER);
    private File selectedPicture;

    public GUI_EmployeeProfile(UserData employee, ArrayList<UserData> users) {
        this(null, null, employee, users);
    }

    public GUI_EmployeeProfile(CardLayout cardLayout, JPanel mainPanel, UserData employee, ArrayList<UserData> users) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.employee = employee;
        this.users = users;

        setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel("EMPLOYEE PROFILE", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(title, BorderLayout.NORTH);

        // EMPLOYEE DETAILS
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        addField(detailsPanel, "Employee ID:", employeeIdField);
        addField(detailsPanel, "Name:", nameField);
        addField(detailsPanel, "Address:", addressField);
        addField(detailsPanel, "Contact Number:", contactField);
        addField(detailsPanel, "Emergency Contact:", emergencyField);
        addField(detailsPanel, "Birthdate (MM/DD/YYYY):", createBirthdatePanel());
        addField(detailsPanel, "Sex / Gender:", sexGenderField);
        addField(detailsPanel, "Civil Status:", civilStatusField);
        addField(detailsPanel, "Work Hours:", workHoursField);
        addField(detailsPanel, "Salary:", salaryField);

        // EMPLOYEE PICTURE
        JPanel picturePanel = new JPanel(new BorderLayout(5, 10));
        picturePanel.setBorder(BorderFactory.createTitledBorder("Employee Picture"));

        pictureLabel.setPreferredSize(new Dimension(220, 270));
        pictureLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        picturePanel.add(pictureLabel, BorderLayout.CENTER);

        JButton changePictureButton = new JButton("Change Picture");
        changePictureButton.addActionListener(e -> choosePicture());

        picturePanel.add(changePictureButton, BorderLayout.SOUTH);

        // CENTER PANEL
        JPanel centerPanel = new JPanel(new BorderLayout(15, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        centerPanel.add(detailsPanel, BorderLayout.CENTER);
        centerPanel.add(picturePanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        // BUTTONS
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveProfile());
        cancelButton.addActionListener(e -> {
            loadProfile();
            returnToHomeIfPossible();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 15));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadProfile();
    }

    private void addField(JPanel panel, String label, JComponent component) {
        panel.add(new JLabel(label));
        panel.add(component);
    }

    private JPanel createBirthdatePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(birthdateField);

        return panel;
    }

    public void loadProfile() {
        if (employee == null) {
            employeeIdField.setText("");
            nameField.setText("");
            addressField.setText("");
            contactField.setText("");
            emergencyField.setText("");
            birthdateField.setSelectedDate(LocalDate.now());
            sexGenderField.setText("");
            civilStatusField.setText("");
            workHoursField.setText("");
            salaryField.setText("");
            selectedPicture = null;
            showPicture(null);
            return;
        }

        employeeIdField.setText(value(employee.getEmployeeId()));
        nameField.setText(value(employee.getName()));
        addressField.setText(value(employee.getAddress()));
        contactField.setText(value(employee.getContactNumber()));
        emergencyField.setText(value(employee.getEmergencyContact()));
        birthdateField.setSelectedDate(employee.getBirthdate());
        sexGenderField.setText(value(employee.getSexGender()));
        civilStatusField.setText(value(employee.getCivilStatus()));
        workHoursField.setText(value(employee.getWorkHours()));
        salaryField.setText(value(employee.getSalary()));

        selectedPicture = null;
        showPicture(employee.getPicturePath());
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private void choosePicture() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose Employee Picture");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Image Files (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedPicture = chooser.getSelectedFile();
            showPicture(selectedPicture.getAbsolutePath());
        }
    }

    private void showPicture(String path) {
        if (path == null || path.trim().isEmpty()) {
            pictureLabel.setIcon(null);
            pictureLabel.setText("No Picture Selected");
            return;
        }

        File imageFile = new File(path);
        if (!imageFile.isFile()) {
            pictureLabel.setIcon(null);
            pictureLabel.setText("Picture Not Found");
            return;
        }

        ImageIcon original = new ImageIcon(imageFile.getAbsolutePath());
        Image scaled = original.getImage().getScaledInstance(
                220, 270, Image.SCALE_SMOOTH);

        pictureLabel.setText("");
        pictureLabel.setIcon(new ImageIcon(scaled));
    }

    private void saveProfile() {
        if (employee == null) {
            showError("No active employee profile to save.");
            return;
        }

        String employeeId = employeeIdField.getText().trim();
        if (employeeId.isEmpty()) {
            showError("Employee ID cannot be empty.");
            return;
        }

        if (users != null) {
            for (UserData otherUser : users) {
                if (otherUser != employee &&
                        employeeId.equalsIgnoreCase(value(otherUser.getEmployeeId()))) {
                    showError("That Employee ID is already being used.");
                    return;
                }
            }
        }

        String salary = salaryField.getText().trim();
        if (!salary.isEmpty()) {
            try {
                Double.parseDouble(salary.replace(",", ""));
            } catch (NumberFormatException e) {
                showError("Please enter a valid salary amount.");
                return;
            }
        }

        String picturePath = employee.getPicturePath();
        if (selectedPicture != null) {
            try {
                File folder = new File("src/data/staff_photos");
                if (!folder.exists() && !folder.mkdirs()) {
                    showError("Could not create the staff picture folder.");
                    return;
                }

                String extension = getExtension(selectedPicture.getName());
                String safeUsername = (employee.getUsername() != null)
                        ? employee.getUsername().replaceAll("[^a-zA-Z0-9_-]", "_")
                        : "employee";
                String fileName = safeUsername + "_" + System.currentTimeMillis() + extension;

                File destination = new File(folder, fileName);
                Files.copy(
                        selectedPicture.toPath(),
                        destination.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );

                picturePath = "src/data/staff_photos/" + fileName;
                selectedPicture = null;
            } catch (IOException e) {
                showError("Could not save the selected picture: " + e.getMessage());
                return;
            }
        }

        employee.setEmployeeId(employeeId);
        employee.setName(nameField.getText().trim());
        employee.setAddress(addressField.getText().trim());
        employee.setContactNumber(contactField.getText().trim());
        employee.setEmergencyContact(emergencyField.getText().trim());
        employee.setBirthdate(birthdateField.getSelectedDate());
        employee.setSexGender(sexGenderField.getText().trim());
        employee.setCivilStatus(civilStatusField.getText().trim());
        employee.setWorkHours(workHoursField.getText().trim());
        employee.setSalary(salary);
        employee.setPicturePath(picturePath);

        if (users != null) {
            Database.saveUsers(users);
        }

        JOptionPane.showMessageDialog(
                this,
                "Employee profile successfully updated.",
                "Saved",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void returnToHomeIfPossible() {
        if (cardLayout != null && mainPanel != null) {
            cardLayout.show(mainPanel, "HOME");
        }
    }

    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(dot).toLowerCase() : ".jpg";
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Profile Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}