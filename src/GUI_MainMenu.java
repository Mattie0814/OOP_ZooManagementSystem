import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

public final class GUI_MainMenu extends JFrame implements ActionListener {
    private final JButton overviewButton = createMenuButton("DASHBOARD");
    private final JButton staffButton = createMenuButton("STAFF DATABASE");
    private final JButton animalButton = createMenuButton("ANIMAL DATABASE");
    private final JButton inventoryButton = createMenuButton("INVENTORY DATABASE");
    private final JButton scheduleButton = createMenuButton("SCHEDULE / TASKS");

    private final JButton accountButton = createMenuButton("ACCOUNT MANAGEMENT");
    private final JButton logoutButton = createMenuButton("LOGOUT");
    private final JButton closeButton = createMenuButton("CLOSE APPLICATION");

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentLayout);

    private final UserData user;
    private final NotificationCenter notificationCenter = new NotificationCenter();
    private final Timer notificationTimer = new Timer(1000, e -> checkForNotifications());

    public GUI_MainMenu() {
        this.user = Database.getActiveUser();

        setTitle("ZooTrak - Dashboard");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);

        add(createSidebarPanel(), BorderLayout.WEST);

        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        contentPanel.add(createOverviewPanel(), "HOME");
        contentPanel.add(new GUI_Animal(contentLayout, contentPanel), "ANIMALS");
        contentPanel.add(new GUI_Inventory(contentLayout, contentPanel), "INVENTORY");
        contentPanel.add(new GUI_Schedule(contentLayout, contentPanel), "SCHEDULE");

        if (user.getType() == UserData.ROLE.ADMIN) {
            contentPanel.add(new GUI_StaffRegistry(contentLayout, contentPanel, user), "STAFF");
        }

        contentPanel.add(new GUI_EmployeeProfile(contentLayout, contentPanel, user, Database.getUserList()), "ACCOUNT MANAGEMENT");

        BackgroundPanel workspaceWrapper = new BackgroundPanel("/graphics/mainMenuBackground.jpg");
        workspaceWrapper.setLayout(new BorderLayout());
        workspaceWrapper.add(contentPanel, BorderLayout.CENTER);

        add(workspaceWrapper, BorderLayout.CENTER);

        contentLayout.show(contentPanel, "HOME");
        setVisible(true);
        checkForNotifications();
        notificationTimer.start();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(24, 34, 29));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 75, 68)),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JLabel appTitle = new JLabel("ZOOTRAK");
        appTitle.setFont(new Font("Arial", Font.BOLD, 24));
        appTitle.setForeground(Color.WHITE);

        String userInfo = (user != null)
                ? user.getUsername() + " (" + user.getType() + ")"
                : "Guest";
        JLabel userLabel = new JLabel("Logged in: " + userInfo);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        userLabel.setForeground(new Color(210, 225, 215));

        header.add(appTitle, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(new Color(33, 45, 39));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(60, 75, 68)),
                BorderFactory.createEmptyBorder(16, 12, 16, 12)));

        JPanel navButtons = new JPanel(new GridLayout(0, 1, 0, 10));
        navButtons.setOpaque(false);
        navButtons.add(overviewButton);

        if (user != null && user.getType() == UserData.ROLE.ADMIN) {
            navButtons.add(staffButton);
        }

        navButtons.add(animalButton);
        navButtons.add(inventoryButton);
        navButtons.add(scheduleButton);

        JPanel bottomButtons = new JPanel(new GridLayout(0, 1, 0, 10));
        bottomButtons.setOpaque(false);
        bottomButtons.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        bottomButtons.add(accountButton);
        bottomButtons.add(logoutButton);
        bottomButtons.add(closeButton);

        sidebar.add(navButtons, BorderLayout.NORTH);
        sidebar.add(bottomButtons, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel createOverviewPanel() {
        JPanel home = new JPanel(new BorderLayout());
        home.setOpaque(false);

        JPanel card = new JPanel(new GridLayout(2, 1, 8, 8));
        card.setBackground(new Color(0, 0, 0, 150));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        String username = (user != null) ? user.getUsername() : "User";
        String role = (user != null) ? String.valueOf(user.getType()) : "STAFF";

        JLabel welcome = new JLabel("Welcome back, " + username + "!");
        welcome.setFont(new Font("Arial", Font.BOLD, 26));
        welcome.setForeground(Color.WHITE);

        JLabel hint = new JLabel("Select a module on the left (" + role + ") to manage records, inventory, or schedules.");
        hint.setFont(new Font("Arial", Font.PLAIN, 16));
        hint.setForeground(new Color(225, 235, 230));

        card.add(welcome);
        card.add(hint);
        home.add(card, BorderLayout.NORTH);

        return home;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addActionListener(this);
        return button;
    }

    private void checkForNotifications() {
        List<String> notifications =
                notificationCenter.getNewDashboardNotifications(LocalDateTime.now(), user);
        if (notifications.isEmpty()) {
            return;
        }

        JTextArea message = new JTextArea(String.join("\n\n", notifications));
        message.setEditable(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setColumns(45);
        message.setRows(Math.min(12, Math.max(4, notifications.size() * 2)));
        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(message),
                "ZooTrak Notifications",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == overviewButton) {
            contentLayout.show(contentPanel, "HOME");
        } else if (src == animalButton) {
            contentLayout.show(contentPanel, "ANIMALS");
        } else if (src == inventoryButton) {
            contentLayout.show(contentPanel, "INVENTORY");
        } else if (src == scheduleButton) {
            contentLayout.show(contentPanel, "SCHEDULE");
        } else if (src == staffButton) {
            if (user != null && user.getType() == UserData.ROLE.ADMIN) {
                contentLayout.show(contentPanel, "STAFF");
            }
        } else if (src == accountButton) {
            contentLayout.show(contentPanel, "ACCOUNT MANAGEMENT");
        } else if (src == logoutButton) {
            logout();
        } else if (src == closeButton) {
            System.exit(0);
        }
    }

    private void logout() {
        notificationTimer.stop();
        if (user != null) {
            System.out.println(user.getUsername() + " logged out.");
            Database.setActiveUser(null);
        }
        dispose();
        new GUI_Login();
    }
}