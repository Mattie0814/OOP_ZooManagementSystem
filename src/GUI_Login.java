import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public final class GUI_Login extends JFrame{
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    private final ArrayList<UserData> users = Database.getUserList();

    public GUI_Login() {
        setTitle("ZooTrak - Zoo Management System");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        BackgroundPanel backgroundPanel = new BackgroundPanel("/graphics/loginMenuBackground.jpg");
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)));

        backgroundPanel.setLayout(new GridLayout(5, 1, 10, 20));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(150, 350, 150, 350));

        ActionListener loginAction = e -> attemptLogin();

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        passwordField.addActionListener(loginAction);

        if (Database.getActiveUser() != null){
            login();
            return;
        }

        backgroundPanel.add(usernameField);
        backgroundPanel.add(passwordField);
        add(backgroundPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your username and password.",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (UserData user : users) {
            if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                System.out.println(user.getType() + " logged in.");

                Database.setActiveUser(user);

                new GUI_MainMenu();
                dispose();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Invalid username or password.",
                "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    private void login(){
        new GUI_MainMenu();
        dispose();
    }
}