import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

public final class GUI_Login extends JFrame{
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    private final ArrayList<UserData> users = Database.loadUsers();

    public GUI_Login() {
        setTitle("ZooTrak - Zoo Management System");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        URL imageUrl = getClass().getResource("graphics/loginMenuBackground.jpg");
        Image backgroundImage = imageUrl != null ? new ImageIcon(imageUrl).getImage() : null;

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
            }
        };

        panel.setLayout(new GridLayout(5, 1, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(150, 350, 150, 350));

        ActionListener loginAction = e -> login();

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        passwordField.addActionListener(loginAction);

        panel.add(usernameField);
        panel.add(passwordField);
        add(panel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void login() {
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
}