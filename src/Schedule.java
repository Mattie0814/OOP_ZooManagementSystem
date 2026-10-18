import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schedule extends JPanel {
    private JLabel titleLabel;
    private JLabel timeLabel;

    private Map<LocalDate, List<Task>> taskMap = new HashMap<>();

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private CalendarPanel calendarPanel;

    public Schedule(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        titleLabel = new JLabel("SCHEDULE MANAGER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(timeLabel);

        JPanel optionsPanel = createOptionsPanel();
        calendarPanel = new CalendarPanel(this);

        add(headerPanel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.WEST);
        add(calendarPanel, BorderLayout.CENTER);

        handleTime();

        loadPresetData();
    }

    private void handleTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm:ss a");
        timeLabel.setText(LocalDateTime.now().format(formatter));
        Timer timer = new Timer(1000, e -> timeLabel.setText(LocalDateTime.now().format(formatter)));
        timer.start();
    }

    private JPanel createOptionsPanel() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setPreferredSize(new Dimension(220, 0));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));
        optionsPanel.setBackground(new Color(240, 240, 240));

        JLabel optionsLabel = new JLabel("OPTIONS");
        optionsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        optionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addButton = new JButton("Add Task Today");
        JButton removeButton = new JButton("Remove Task");
        JButton notificationButton = new JButton("Notifications");
        JButton returnButton = new JButton("Return to Main");

        Dimension buttonSize = new Dimension(180, 40);
        JButton[] buttons = {addButton, removeButton, notificationButton, returnButton};
        for (JButton b : buttons) {
            b.setMaximumSize(buttonSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        addButton.addActionListener(e -> promptAddTask(LocalDate.now()));
        removeButton.addActionListener(e -> removeTaskDialog());
        notificationButton.addActionListener(e -> showNotifications());
        returnButton.addActionListener(e -> showMain());

        optionsPanel.add(optionsLabel);
        optionsPanel.add(Box.createVerticalStrut(25));
        optionsPanel.add(addButton);
        optionsPanel.add(Box.createVerticalStrut(15));
        optionsPanel.add(removeButton);
        optionsPanel.add(Box.createVerticalStrut(15));
        optionsPanel.add(notificationButton);
        optionsPanel.add(Box.createVerticalStrut(15));
        optionsPanel.add(returnButton);

        return optionsPanel;
    }

    public void promptAddTask(LocalDate date) {
        String name = JOptionPane.showInputDialog(this, "Task Name for " + date + ":", "Add Task", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;

        String time = JOptionPane.showInputDialog(this, "Task Time (e.g., 09:00 AM):", "Add Task", JOptionPane.PLAIN_MESSAGE);
        if (time == null || time.trim().isEmpty()) return;

        String[] repeatOptions = {"NONE", "DAILY", "WEEKLY", "MONTHLY"};
        String recurrence = (String) JOptionPane.showInputDialog(
                this, "Repeat Frequency:", "Task Recurrence",
                JOptionPane.QUESTION_MESSAGE, null, repeatOptions, repeatOptions[0]
        );
        if (recurrence == null) recurrence = "NONE";

        Task task = new Task(name, time, date, recurrence);
        addTask(task);
        calendarPanel.refreshCalendar();
    }

    public void addTask(Task task) {
        taskMap.computeIfAbsent(task.getStartDate(), k -> new ArrayList<>()).add(task);
    }

    public List<Task> getTasksForDate(LocalDate date) {
        List<Task> result = new ArrayList<>();
        for (List<Task> list : taskMap.values()) {
            for (Task t : list) {
                if (t.occursOn(date)) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    private void removeTaskDialog() {
        List<Task> allTasks = new ArrayList<>();
        taskMap.values().forEach(allTasks::addAll);

        if (allTasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tasks available to remove.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Task selected = (Task) JOptionPane.showInputDialog(
                this, "Select a task to remove:", "Remove Task",
                JOptionPane.PLAIN_MESSAGE, null, allTasks.toArray(), allTasks.get(0)
        );

        if (selected != null) {
            List<Task> list = taskMap.get(selected.getStartDate());
            if (list != null) {
                list.remove(selected);
                calendarPanel.refreshCalendar();
            }
        }
    }

    private void showNotifications() {
        JOptionPane.showMessageDialog(this, "Notification system coming soon.", "Notifications", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMain() {
        if (cardLayout != null && mainPanel != null) {
            cardLayout.show(mainPanel, "MAIN");
        }
    }

    public void loadPresetData() {
        LocalDate today = LocalDate.now();

        addTask(new FoodTask(
                "Morning Carnivore Feeding", "08:30 AM", today, "DAILY",
                "Bengal Tigers", "Zone A - Cat Canyon", "25kg Raw Beef + Vitamin Powders"
        ));

        addTask(new FoodTask(
                "Giraffe Canopy Forage Setup", "10:00 AM", today, "DAILY",
                "Reticulated Giraffes", "Zone C - African Savannah", "Acacia Branches & Alfalfa Hay"
        ));

        addTask(new MedicineTask(
                "Elephant Joint Supplement Administration", "09:15 AM", today, "WEEKLY",
                "ELE-102 (Mahali)", "200g Glucosamine Chew", "Dr. Vance (Chief Vet)"
        ));

        addTask(new MedicineTask(
                "Penguin Avian Flu Vaccination", "01:30 PM", today.plusDays(2), "NONE",
                "PENG-GROUP (14 Birds)", "0.5ml Subcutaneous Dose", "Vet Tech Team B"
        ));

        addTask(new MaintenanceTask(
                "Reptile House Humidity & Heat Pump Service", "07:00 AM", today, "WEEKLY",
                "Zone D - Tropical Reptile House", "Medium", "HVAC Pressure Gauges & Filters"
        ));

        addTask(new MaintenanceTask(
                "Primary Mesh Containment Inspection", "03:00 PM", today.plusDays(4), "MONTHLY",
                "Aviary Enclosure 3", "High (Requires Bird Relocation)", "Safety Harness, Tension Tester"
        ));

        if (calendarPanel != null) {
            calendarPanel.refreshCalendar();
        }
    }
}

class CalendarPanel extends JPanel {
    private JLabel monthLabel;
    private JPanel calendarGrid;
    private YearMonth currentMonth;
    private Schedule parentSchedule;

    public CalendarPanel(Schedule parentSchedule) {
        this.parentSchedule = parentSchedule;
        this.currentMonth = YearMonth.now();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        createHeader();

        JPanel centerPanel = new JPanel(new BorderLayout(0, 5));
        centerPanel.add(createDayNames(), BorderLayout.NORTH);

        calendarGrid = new JPanel(new GridLayout(0, 7, 5, 5));
        centerPanel.add(calendarGrid, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        refreshCalendar();
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JButton previousButton = new JButton("<");
        JButton nextButton = new JButton(">");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 20));

        previousButton.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); refreshCalendar(); });
        nextButton.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); refreshCalendar(); });

        headerPanel.add(previousButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createDayNames() {
        JPanel dayNamePanel = new JPanel(new GridLayout(1, 7, 5, 5));
        DayOfWeek[] days = DayOfWeek.values();
        for (DayOfWeek day : days) {
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
                emptyCell.setBackground(new Color(240, 240, 240));
                calendarGrid.add(emptyCell);
            } else {
                LocalDate date = currentMonth.atDay(dayNumber);
                List<Task> tasksForDay = parentSchedule.getTasksForDate(date);

                String buttonText = "<html><center><b>" + dayNumber + "</b>";
                if (!tasksForDay.isEmpty()) {
                    buttonText += "<br><font color='blue' size='2'>" + tasksForDay.size() + " Task(s)</font>";
                }
                buttonText += "</center></html>";

                JButton dayButton = new JButton(buttonText);
                dayButton.setMargin(new Insets(2, 2, 2, 2));

                if (date.equals(LocalDate.now())) {
                    dayButton.setBackground(new Color(220, 240, 255));
                }

                dayButton.addActionListener(e -> handleDateClick(date, tasksForDay));
                calendarGrid.add(dayButton);
            }
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private void handleDateClick(LocalDate date, List<Task> tasks) {
        StringBuilder msg = new StringBuilder("Schedule for " + date + ":\n\n");
        if (tasks.isEmpty()) {
            msg.append("No tasks scheduled.");
        } else {
            for (Task t : tasks) {
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

class Task {
    private String taskName;
    private String taskTime;
    private LocalDate startDate;
    private String recurrence; // NONE, DAILY, WEEKLY, MONTHLY

    public Task(String taskName, String taskTime, LocalDate startDate, String recurrence) {
        this.taskName = taskName;
        this.taskTime = taskTime;
        this.startDate = startDate;
        this.recurrence = recurrence;
    }

    public String getTaskName() { return taskName; }
    public String getTaskTime() { return taskTime; }
    public LocalDate getStartDate() { return startDate; }
    public String getRecurrence() { return recurrence; }

    public String getType() {
        return "General";
    }

    public String getDetails() {
        return "No additional details.";
    }

    public boolean occursOn(LocalDate date) {
        if (date.isBefore(startDate)) return false;
        switch (recurrence) {
            case "DAILY":
                return true;
            case "WEEKLY":
                return startDate.getDayOfWeek() == date.getDayOfWeek();
            case "MONTHLY":
                return startDate.getDayOfMonth() == date.getDayOfMonth();
            default:
                return startDate.equals(date);
        }
    }

    @Override
    public String toString() {
        return "[" + getType() + "] " + taskName + " (" + taskTime + ")";
    }
}

class FoodTask extends Task {
    private String animalSpecies;
    private String enclosureZone;
    private String dietNotes;

    public FoodTask(String taskName, String taskTime, LocalDate startDate, String recurrence,
                    String animalSpecies, String enclosureZone, String dietNotes) {
        super(taskName, taskTime, startDate, recurrence);
        this.animalSpecies = animalSpecies;
        this.enclosureZone = enclosureZone;
        this.dietNotes = dietNotes;
    }

    public String getAnimalSpecies() { return animalSpecies; }
    public String getEnclosureZone() { return enclosureZone; }
    public String getDietNotes() { return dietNotes; }

    @Override
    public String getType() {
        return "Feeding & Diet";
    }

    @Override
    public String getDetails() {
        return "Species: " + animalSpecies + " | Zone: " + enclosureZone + " | Diet: " + dietNotes;
    }
}

class MedicineTask extends Task {
    private String animalID;
    private String dosage;
    private String administeredBy;

    public MedicineTask(String taskName, String taskTime, LocalDate startDate, String recurrence,
                        String animalID, String dosage, String administeredBy) {
        super(taskName, taskTime, startDate, recurrence);
        this.animalID = animalID;
        this.dosage = dosage;
        this.administeredBy = administeredBy;
    }

    public String getAnimalID() { return animalID; }
    public String getDosage() { return dosage; }
    public String getAdministeredBy() { return administeredBy; }

    @Override
    public String getType() {
        return "Vet Care & Meds";
    }

    @Override
    public String getDetails() {
        return "Animal ID: " + animalID + " | Dosage: " + dosage + " | Staff: " + administeredBy;
    }
}

class MaintenanceTask extends Task {
    private String habitatLocation;
    private String safetyRiskLevel; // Low, Medium, High (Requires Animal Relocation)
    private String equipmentNeeded;

    public MaintenanceTask(String taskName, String taskTime, LocalDate startDate, String recurrence,
                           String habitatLocation, String safetyRiskLevel, String equipmentNeeded) {
        super(taskName, taskTime, startDate, recurrence);
        this.habitatLocation = habitatLocation;
        this.safetyRiskLevel = safetyRiskLevel;
        this.equipmentNeeded = equipmentNeeded;
    }

    public String getHabitatLocation() { return habitatLocation; }
    public String getSafetyRiskLevel() { return safetyRiskLevel; }
    public String getEquipmentNeeded() { return equipmentNeeded; }

    @Override
    public String getType() {
        return "Habitat Maintenance";
    }

    @Override
    public String getDetails() {
        return "Location: " + habitatLocation + " | Risk Level: " + safetyRiskLevel + " | Tools: " + equipmentNeeded;
    }
}