import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public final class GUI_Schedule extends JPanel {
    private static final DateTimeFormatter CLOCK_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm:ss a");

    private final JLabel timeLabel;
    private final Timer clockTimer;

    private final HashMap<LocalDate, List<TaskData>> taskMap = new HashMap<>();

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final CalendarPanel calendarPanel;
    private final UserData user;

    public GUI_Schedule(CardLayout cardLayout, JPanel mainPanel) {
        this(cardLayout, mainPanel, Database.getActiveUser());
    }

    public GUI_Schedule(CardLayout cardLayout, JPanel mainPanel, UserData user) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.user = (user != null) ? user : Database.getActiveUser();

        setLayout(new BorderLayout());

        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel headerPanel = createHeaderPanel(timeLabel);
        JPanel optionsPanel = createOptionsPanel();
        this.calendarPanel = new CalendarPanel(this);

        add(headerPanel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.WEST);
        add(calendarPanel, BorderLayout.CENTER);

        updateClockLabel();
        clockTimer = new Timer(1000, e -> updateClockLabel());

        loadInitialTasks();
        calendarPanel.refreshCalendar();
    }

    private JPanel createHeaderPanel(JLabel clockLabel) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)));

        JLabel titleLabel = new JLabel("SCHEDULE MANAGER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(clockLabel);
        return headerPanel;
    }

    private JPanel createOptionsPanel() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setPreferredSize(new Dimension(220, 0));
        optionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(25, 15, 25, 15)));

        JLabel optionsLabel = new JLabel("OPTIONS");
        optionsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        optionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addButton = new JButton("Add Task Today");
        JButton removeButton = new JButton("Remove Task");
        JButton notificationButton = new JButton("Notifications");

        Dimension buttonSize = new Dimension(180, 40);
        JButton[] buttons = {addButton, removeButton, notificationButton};
        for (JButton b : buttons) {
            b.setMaximumSize(buttonSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setFocusPainted(false);
        }

        addButton.addActionListener(e -> promptAddTask(LocalDate.now()));
        removeButton.addActionListener(e -> removeTaskDialog());
        notificationButton.addActionListener(e -> showNotifications());

        optionsPanel.add(optionsLabel);
        optionsPanel.add(Box.createVerticalStrut(25));
        optionsPanel.add(addButton);
        optionsPanel.add(Box.createVerticalStrut(15));
        optionsPanel.add(removeButton);
        optionsPanel.add(Box.createVerticalStrut(15));
        optionsPanel.add(notificationButton);

        return optionsPanel;
    }

    private void updateClockLabel() {
        timeLabel.setText(LocalDateTime.now().format(CLOCK_FORMATTER));
    }

    private void loadInitialTasks() {
        if (user != null && user.getSchedule() != null) {
            for (TaskData task : user.getSchedule()) {
                loadTaskIntoMap(task);
            }
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        clockTimer.start();
    }

    @Override
    public void removeNotify() {
        clockTimer.stop();
        super.removeNotify();
    }

    public void promptAddTask(LocalDate date) {
        JTextField nameField = new JTextField(15);
        TimeInputPanel timeInputPanel = new TimeInputPanel();

        String[] repeatOptions = {"NONE", "DAILY", "WEEKLY", "MONTHLY", "INTERVAL"};
        JComboBox<String> recurrenceBox = new JComboBox<>(repeatOptions);

        JLabel intervalLabel = new JLabel("Repeat Every (Days):");
        JTextField intervalField = createNumericTextField(10);
        intervalLabel.setVisible(false);
        intervalField.setVisible(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Task Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Task Time:"), gbc);
        gbc.gridx = 1;
        formPanel.add(timeInputPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Recurrence:"), gbc);
        gbc.gridx = 1;
        formPanel.add(recurrenceBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(intervalLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(intervalField, gbc);

        recurrenceBox.addActionListener(e -> {
            boolean isInterval = "INTERVAL".equals(recurrenceBox.getSelectedItem());
            intervalLabel.setVisible(isInterval);
            intervalField.setVisible(isInterval);

            if (!isInterval) {
                intervalField.setText("");
            }

            formPanel.revalidate();
            formPanel.repaint();

            Window dialogWindow = SwingUtilities.getWindowAncestor(formPanel);
            if (dialogWindow != null) {
                dialogWindow.pack();
            }
        });

        int result = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                "Add Task for " + date,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Task name cannot be empty.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String time = timeInputPanel.getFormattedTime();
        String recurrence = (String) recurrenceBox.getSelectedItem();

        TaskData newTask;
        if ("INTERVAL".equals(recurrence)) {
            String intervalText = intervalField.getText().trim();
            if (intervalText.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Please enter an interval in days.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int intervalDays;
            try {
                intervalDays = Integer.parseInt(intervalText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this, "Please enter a valid number of days.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (intervalDays <= 0) {
                JOptionPane.showMessageDialog(
                        this, "Interval must be at least 1 day.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            newTask = new TaskData(name, time, date, recurrence, intervalDays);
        } else {
            newTask = new TaskData(name, time, date, recurrence);
        }

        addTask(newTask);
        calendarPanel.refreshCalendar();
    }

    private JTextField createNumericTextField(int columns) {
        JTextField field = new JTextField(columns);
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string != null && string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text != null && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        return field;
    }

    private void loadTaskIntoMap(TaskData task) {
        taskMap.computeIfAbsent(task.getStartDate(), k -> new ArrayList<>()).add(task);
    }

    private void addTask(TaskData task) {
        loadTaskIntoMap(task);
        if (user != null) {
            if (user.getSchedule() != null) {
                user.getSchedule().add(task);
            }
            System.out.println("Added Task for " + user.getUsername() + ": " + task.getDetails());
        }
    }

    public List<TaskData> getTasksForDate(LocalDate date) {
        List<TaskData> result = new ArrayList<>();
        for (List<TaskData> list : taskMap.values()) {
            for (TaskData task : list) {
                if (task.getStartDate() != null && task.getStartDate().isBefore(LocalDate.now())) {
                    continue;
                }

                if (task.occursOn(date)) {
                    result.add(task);
                }
            }
        }
        return result;
    }

    private void removeTaskDialog() {
        List<TaskData> allTasks = new ArrayList<>();
        taskMap.values().forEach(allTasks::addAll);

        if (allTasks.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "No tasks available to remove.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object selectedObj = JOptionPane.showInputDialog(
                this, "Select a task to remove:", "Remove Task",
                JOptionPane.PLAIN_MESSAGE, null, allTasks.toArray(), allTasks.get(0)
        );

        if (selectedObj instanceof TaskData selected) {
            List<TaskData> list = taskMap.get(selected.getStartDate());
            if (list != null) {
                list.remove(selected);
            }
            if (user != null && user.getSchedule() != null) {
                user.getSchedule().remove(selected);
            }
            calendarPanel.refreshCalendar();
        }
    }

    private void showNotifications() {
        List<String> notifications =
                NotificationCenter.getCurrentNotifications(LocalDateTime.now(), user);
        if (notifications.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "No current notifications.", "Notifications", JOptionPane.INFORMATION_MESSAGE);
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
                "Notifications",
                JOptionPane.WARNING_MESSAGE
        );
    }
}

class CalendarPanel extends JPanel {
    private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
    private final JPanel calendarGrid = new JPanel(new GridLayout(0, 7, 5, 5));
    private final GUI_Schedule parentSchedule;
    private YearMonth currentMonth = YearMonth.now();

    public CalendarPanel(GUI_Schedule parentSchedule) {
        this.parentSchedule = parentSchedule;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createMonthHeader(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 5));
        centerPanel.add(createDayNames(), BorderLayout.NORTH);
        centerPanel.add(calendarGrid, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createMonthHeader() {
        JPanel monthHeader = new JPanel(new BorderLayout());

        JButton previousButton = new JButton("<");
        JButton nextButton = new JButton(">");
        monthLabel.setFont(new Font("Arial", Font.BOLD, 20));

        previousButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshCalendar();
        });
        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshCalendar();
        });

        monthHeader.add(previousButton, BorderLayout.WEST);
        monthHeader.add(monthLabel, BorderLayout.CENTER);
        monthHeader.add(nextButton, BorderLayout.EAST);

        return monthHeader;
    }

    private JPanel createDayNames() {
        JPanel dayNamePanel = new JPanel(new GridLayout(1, 7, 5, 5));

        for (DayOfWeek day : DayOfWeek.values()) {
            JLabel label = new JLabel(day.name().substring(0, 3), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            dayNamePanel.add(label);
        }
        return dayNamePanel;
    }

    public void refreshCalendar() {
        calendarGrid.removeAll();
        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeekOffset = firstOfMonth.getDayOfWeek().getValue() - 1;
        int daysInMonth = currentMonth.lengthOfMonth();

        int totalCells = (int) Math.ceil((dayOfWeekOffset + daysInMonth) / 7.0) * 7;

        for (int i = 0; i < totalCells; i++) {
            int dayNumber = i - dayOfWeekOffset + 1;

            if (dayNumber < 1 || dayNumber > daysInMonth) {
                JPanel emptyCell = new JPanel();
                emptyCell.setBackground(new Color(244, 134, 134));
                calendarGrid.add(emptyCell);
            } else {
                LocalDate date = currentMonth.atDay(dayNumber);
                List<TaskData> tasksForDay = parentSchedule.getTasksForDate(date);

                StringBuilder buttonText = new StringBuilder("<html><center><b>")
                        .append(dayNumber)
                        .append("</b>");
                if (!tasksForDay.isEmpty()) {
                    buttonText.append("<br><font color='blue' size='2'>")
                            .append(tasksForDay.size())
                            .append(" Task(s)</font>");
                }
                buttonText.append("</center></html>");

                JButton dayButton = new JButton(buttonText.toString());
                dayButton.setMargin(new Insets(2, 2, 2, 2));

                if (date.equals(LocalDate.now())) {
                    dayButton.setBackground(new Color(170, 244, 164));
                }

                dayButton.addActionListener(e -> handleDateClick(date, tasksForDay));
                calendarGrid.add(dayButton);
            }
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private void handleDateClick(LocalDate date, List<TaskData> tasks) {
        StringBuilder msg = new StringBuilder("Schedule for ").append(date).append(":\n\n");
        if (tasks.isEmpty()) {
            msg.append("No tasks scheduled.");
        } else {
            for (TaskData t : tasks) {
                msg.append("• [").append(t.getType()).append("] ").append(t.getTaskName())
                        .append(" at ").append(t.getTaskTime()).append("\n")
                        .append("  Details: ").append(t.getDetails()).append("\n")
                        .append("  Recurrence: ").append(t.getRecurrence()).append("\n\n");
            }
        }

        Object[] options = {"Add Task", "Close"};
        int choice = JOptionPane.showOptionDialog(
                this, msg.toString(), "Tasks on " + date,
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]
        );

        if (choice == JOptionPane.YES_OPTION) {
            parentSchedule.promptAddTask(date);
        }
    }
}

class TimeInputPanel extends JPanel {
    private final JComboBox<String> hourBox;
    private final JComboBox<String> minuteBox;
    private final JComboBox<String> amPmBox;

    public TimeInputPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));

        String[] hours = new String[12];
        for (int i = 0; i < 12; i++) {
            hours[i] = String.format("%02d", i + 1);
        }

        String[] minutes = new String[60];
        for (int i = 0; i < 60; i++) {
            minutes[i] = String.format("%02d", i);
        }

        hourBox = createInputBox(hours);
        minuteBox = createInputBox(minutes);
        amPmBox = createInputBox(new String[]{"AM", "PM"});

        LocalTime now = LocalTime.now();
        int currentHour = now.getHour() % 12;
        hourBox.setSelectedIndex(currentHour == 0 ? 11 : currentHour - 1);
        minuteBox.setSelectedIndex(now.getMinute());
        amPmBox.setSelectedItem(now.getHour() >= 12 ? "PM" : "AM");

        add(hourBox);
        add(new JLabel(":"));
        add(minuteBox);
        add(amPmBox);
    }

    public String getFormattedTime() {
        return hourBox.getSelectedItem() + ":" + minuteBox.getSelectedItem() + " " + amPmBox.getSelectedItem();
    }

    private JComboBox<String> createInputBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("SansSerif", Font.BOLD, 13));
        box.setBackground(new Color(248, 250, 252));
        box.setFocusable(false);
        return box;
    }
}