import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


public class GUI_Login extends JFrame implements ActionListener {
    JTextField usernameField;
    JPasswordField passwordField;

    JButton loginButton;

    ArrayList<UserData> users = Database.loadUsers();

    public GUI_Login() {
        JFrame frame = this;

        frame.setTitle("Zoo Management System");
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Main panel
        JPanel panel = new JPanel() {
            private final Image backgroundImage = new ImageIcon(getClass().getResource("graphics/tempLoginImg.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(150, 350, 150, 350)
        );

        // Title
        JLabel title = new JLabel(
                "ZOO MANAGEMENT SYSTEM",
                JLabel.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 30));


        // Username
        usernameField = new JTextField();

        usernameField.setFont(
                new Font("Arial", Font.PLAIN, 20)
        );

        usernameField.setBorder(
                BorderFactory.createTitledBorder("Username")
        );


        // Password
        passwordField = new JPasswordField();

        passwordField.setFont(
                new Font("Arial", Font.PLAIN, 20)
        );

        passwordField.setBorder(
                BorderFactory.createTitledBorder("Password")
        );


        // Login Button
        loginButton = new JButton("LOGIN");

        loginButton.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        loginButton.addActionListener(this);


        // Add components
        panel.add(title);
        panel.add(usernameField);
        panel.add(passwordField);
        panel.add(loginButton);


        frame.add(panel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton)
        {
            login();
        }
    }


    public void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter your username and password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        for (UserData user : users) {

            if (user.getUsername().equals(username)
                    && user.getPassword().equals(password)) {

                // Successful login
                System.out.println(user.getType() + " logged in.");

                new GUI_MainMenu(user);

                dispose();
                return;
            }
        }

        JOptionPane.showMessageDialog(
                this,
                "Invalid username or password.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}