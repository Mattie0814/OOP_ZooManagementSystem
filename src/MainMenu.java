import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class MainMenu extends JFrame implements ActionListener
{
    User user;

    JButton animalButton;
    JButton inventoryButton;
    JButton scheduleButton;
    JButton logoutButton;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel menuPanel;


    public MainMenu(User user)
    {
        this.user = user;

        setTitle("ZooTrak - Main Menu");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();

        mainPanel = new JPanel(cardLayout);

        menuPanel = createMenuPanel();

        mainPanel.add(menuPanel, "MAIN");

        mainPanel.add(new Schedule(cardLayout, mainPanel), "SCHEDULE");
        mainPanel.add(new Animals(cardLayout, mainPanel), "ANIMALS");
        mainPanel.add(new InventoryMenu(cardLayout, mainPanel), "INVENTORY");

        add(mainPanel);

        cardLayout.show(mainPanel, "MAIN");

        setLocationRelativeTo(null);
        setVisible(true);
    }


    private JPanel createMenuPanel()
    {
        JPanel panel = new JPanel(
                new BorderLayout()
        );


        // Title
        JLabel title = new JLabel(
                "ZOOTRAK",
                JLabel.CENTER
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 32)
        );

        title.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 0, 20, 0
                )
        );


        // Welcome message
        JLabel welcome = new JLabel(
                "Welcome, " + user.getUsername() +
                        " (" + user.getRole() + ")",
                JLabel.CENTER
        );

        welcome.setFont(
                new Font("Arial", Font.PLAIN, 22)
        );


        // Button panel
        JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(
                new GridLayout(4, 1, 20, 20)
        );

        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        40, 400, 80, 400
                )
        );


        // Animal Database
        animalButton = new JButton(
                "ANIMAL DATABASE"
        );

        animalButton.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        animalButton.addActionListener(this);


        // Inventory
        inventoryButton = new JButton(
                "INVENTORY"
        );

        inventoryButton.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        inventoryButton.addActionListener(this);


        // Schedule
        scheduleButton = new JButton(
                "SCHEDULE / TASKS"
        );

        scheduleButton.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        scheduleButton.addActionListener(this);


        // Logout
        logoutButton = new JButton(
                "LOGOUT"
        );

        logoutButton.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        logoutButton.addActionListener(this);


        // Add buttons
        buttonPanel.add(animalButton);
        buttonPanel.add(inventoryButton);
        buttonPanel.add(scheduleButton);
        buttonPanel.add(logoutButton);


        // Center panel
        JPanel centerPanel = new JPanel(
                new BorderLayout()
        );

        centerPanel.add(
                welcome,
                BorderLayout.NORTH
        );

        centerPanel.add(
                buttonPanel,
                BorderLayout.CENTER
        );


        panel.add(
                title,
                BorderLayout.NORTH
        );

        panel.add(
                centerPanel,
                BorderLayout.CENTER
        );


        return panel;
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == animalButton)
        {
            cardLayout.show(mainPanel, "ANIMALS");
        }

        else if (e.getSource() == inventoryButton)
        {
            cardLayout.show(mainPanel, "INVENTORY");
        }

        else if (e.getSource() == scheduleButton)
        {
            cardLayout.show(mainPanel, "SCHEDULE");
        }

        else if (e.getSource() == logoutButton)
        {
            logout();
        }
    }


    public void logout()
    {
        System.out.println(
                user.getUsername() + " logged out."
        );

        dispose();

        new Login();
    }
}