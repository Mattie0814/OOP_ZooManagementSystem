import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// Pop-up employee ID/profile window. This is opened from the admin-only Staff Registry.
public final class GUI_EmployeeProfilePopup extends JDialog {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

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

    public GUI_EmployeeProfilePopup(Window parent, UserData employee, ArrayList<UserData> users) {
        super(parent, "Employee Profile / ID", ModalityType.APPLICATION_MODAL);

        this.employee = employee;
        this.users = users;

        setSize(900, 610);
        setResizable(false);
        setLocationRelativeTo(parent);
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
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 15));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadProfile();
    }

    // ADD LABEL AND TEXT FIELD
    private void addField(JPanel panel, String label, JComponent component) {
        panel.add(new JLabel(label));
        panel.add(component);
    }

    // CREATE BIRTHDAY FIELD
    private JPanel createBirthdatePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(birthdateField);

        return panel;
    }

    // LOAD EMPLOYEE INFORMATION
    private void loadProfile() {
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

        showPicture(employee.getPicturePath());
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    // SELECT EMPLOYEE PICTURE
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

    // DISPLAY EMPLOYEE PICTURE
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

    // SAVE EMPLOYEE INFORMATION
    private void saveProfile() {
        String employeeId = employeeIdField.getText().trim();

        if (employeeId.isEmpty()) {
            showError("Employee ID cannot be empty.");
            return;
        }

        // Check duplicate Employee IDs
        for (UserData user : users) {
            if (user != employee &&
                    employeeId.equalsIgnoreCase(value(user.getEmployeeId()))) {

                showError("That Employee ID is already being used.");
                return;
            }
        }

        // Check salary
        String salary = salaryField.getText().trim();

        if (!salary.isEmpty()) {
            try {
                Double.parseDouble(salary.replace(",", ""));
            } catch (NumberFormatException e) {
                showError("Please enter a valid salary amount.");
                return;
            }
        }

        // SAVE EMPLOYEE PICTURE
        String picturePath = employee.getPicturePath();

        if (selectedPicture != null) {
            try {
                File folder = new File("src/data/staff_photos");

                if (!folder.exists() && !folder.mkdirs()) {
                    showError("Could not create the staff picture folder.");
                    return;
                }

                String extension = getExtension(selectedPicture.getName());

                String fileName = employee.getUsername().replaceAll(
                        "[^a-zA-Z0-9_-]", "_")
                        + "_" + System.currentTimeMillis() + extension;

                File destination = new File(folder, fileName);

                Files.copy(
                        selectedPicture.toPath(),
                        destination.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );

                picturePath = "src/data/staff_photos/" + fileName;

            } catch (IOException e) {
                showError("Could not save the selected picture: " + e.getMessage());
                return;
            }
        }

        // UPDATE EMPLOYEE INFORMATION
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

        // SAVE TO JSON DATABASE
        Database.saveUsers(users);

        JOptionPane.showMessageDialog(
                this,
                "Employee profile successfully updated.",
                "Saved",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }

    // GET IMAGE FILE EXTENSION
    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');

        return dot >= 0
                ? fileName.substring(dot).toLowerCase()
                : ".jpg";
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
