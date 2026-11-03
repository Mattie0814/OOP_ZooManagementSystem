import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class GUI_MainMenu extends JFrame implements ActionListener {
    private final UserData user;

    private final JButton animalButton = createMenuButton("ANIMAL DATABASE");
    private final JButton inventoryButton = createMenuButton("INVENTORY");
    private final JButton scheduleButton = createMenuButton("SCHEDULE / TASKS");
    private final JButton logoutButton = createMenuButton("LOGOUT");

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    public GUI_MainMenu(UserData user) {
        this.user = user;

        setTitle("ZooTrak - Main Menu");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel.add(createMenuPanel(), "MAIN");
        mainPanel.add(new GUI_Schedule(cardLayout, mainPanel), "SCHEDULE");
        mainPanel.add(new GUI_Animal(cardLayout, mainPanel), "ANIMALS");
        mainPanel.add(new GUI_Inventory(cardLayout, mainPanel), "INVENTORY");

        add(mainPanel);

        cardLayout.show(mainPanel, "MAIN");

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.addActionListener(this);
        return button;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("ZOOTRAK", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        JLabel welcome = new JLabel("Welcome, " + user.getUsername() + " (" + user.getType() + ")", JLabel.CENTER);
        welcome.setFont(new Font("Arial", Font.PLAIN, 22));

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 400, 80, 400));

        buttonPanel.add(animalButton);
        buttonPanel.add(inventoryButton);
        buttonPanel.add(scheduleButton);
        buttonPanel.add(logoutButton);

        JPanel centerPanel = new JPanel(new BorderLayout());

        centerPanel.add(welcome, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(title, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == animalButton) cardLayout.show(mainPanel, "ANIMALS");
        else if (e.getSource() == inventoryButton) cardLayout.show(mainPanel, "INVENTORY");
        else if (e.getSource() == scheduleButton) cardLayout.show(mainPanel, "SCHEDULE");
        else if (e.getSource() == logoutButton) logout();
    }

    private void logout() {
        System.out.println(user.getUsername() + " logged out.");
        dispose();
        new GUI_Login();
    }
}