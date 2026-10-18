import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class Login extends JFrame implements ActionListener
{
    JTextField usernameField;
    JPasswordField passwordField;

    JButton loginButton;

    // One main password for all accounts
    String mainPassword = "zoo123";


    public Login()
    {
        JFrame frame = this;

        frame.setTitle("Zoo Management System");
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        // Main panel
        JPanel panel = new JPanel();
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
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == loginButton)
        {
            login();
        }
    }


    public void login()
    {
        String username = usernameField.getText();
        String password = new String(
                passwordField.getPassword()
        );


        if (username.equals("Admin") &&
                password.equals(mainPassword))
        {
            User user = new Admin(username);

            System.out.println("Admin logged in.");

            new MainMenu(user);

            dispose();
        }

        else if (username.equals("ZooKeeper") &&
                password.equals(mainPassword))
        {
            User user = new ZooKeeper(username);

            System.out.println("Zoo Keeper logged in.");

            new MainMenu(user);

            dispose();
        }

        else if (username.equals("Staff") &&
                password.equals(mainPassword))
        {
            User user = new Staff(username);

            System.out.println("Staff logged in.");

            new MainMenu(user);

            dispose();
        }

        else
        {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}