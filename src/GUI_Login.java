import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

public final class GUI_Login extends JFrame implements ActionListener {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    private final ArrayList<UserData> users = Database.loadUsers();
    private final ArrayList<ItemData> inv = Database.loadInventory();

    public GUI_Login() {
        setTitle("Zoo Management System");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        URL imageUrl = getClass().getResource("graphics/tempLoginImg.jpg");
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

        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(150, 350, 150, 350));

        JLabel title = new JLabel("ZOO MANAGEMENT SYSTEM", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 20));
        loginButton.addActionListener(this);

        panel.add(title);
        panel.add(usernameField);
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            login();
        }
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
                new GUI_MainMenu(user);
                dispose();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Invalid username or password.",
                "Login Error", JOptionPane.ERROR_MESSAGE);
    }
}