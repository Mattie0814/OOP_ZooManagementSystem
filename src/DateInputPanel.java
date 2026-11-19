import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

class DateInputPanel extends JPanel {
    private final JComboBox<Integer> monthBox;
    private final JComboBox<Integer> dayBox;
    private final JComboBox<Integer> yearBox;

    private boolean updatingDays = false;

    public DateInputPanel() {
        this(LocalDate.now());
    }

    public DateInputPanel(LocalDate initialDate) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));

        LocalDate seed = (initialDate != null) ? initialDate : LocalDate.now();

        Integer[] months = new Integer[12];
        for (int m = 1; m <= 12; m++) {
            months[m - 1] = m;
        }

        int currentYear = seed.getYear();
        Integer[] years = new Integer[21];
        for (int i = 0; i < years.length; i++) {
            years[i] = (currentYear - 10) + i;
        }

        monthBox = createInputBox(months);
        dayBox = createInputBox(new Integer[0]);
        yearBox = createInputBox(years);

        setSelectedDate(seed);

        ActionListener recalcDaysListener = e -> {
            if (!updatingDays) {
                Integer currentDay = (Integer) dayBox.getSelectedItem();
                updateDayChoices(currentDay != null ? currentDay : 1);
            }
        };

        monthBox.addActionListener(recalcDaysListener);
        yearBox.addActionListener(recalcDaysListener);

        add(monthBox);
        add(new JLabel("/"));
        add(dayBox);
        add(new JLabel("/"));
        add(yearBox);
    }

    public void setSelectedDate(LocalDate date) {
        LocalDate target = (date != null) ? date : LocalDate.now();
        yearBox.setSelectedItem(target.getYear());
        monthBox.setSelectedItem(target.getMonthValue());
        updateDayChoices(target.getDayOfMonth());
    }

    private void updateDayChoices(int preferredDay) {
        Integer selectedMonth = (Integer) monthBox.getSelectedItem();
        Integer selectedYear = (Integer) yearBox.getSelectedItem();

        if (selectedMonth == null || selectedYear == null) {
            return;
        }

        int maxDays = YearMonth.of(selectedYear, selectedMonth).lengthOfMonth();
        int targetDay = Math.max(1, Math.min(preferredDay, maxDays));

        updatingDays = true;
        try {
            DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<>();
            for (int d = 1; d <= maxDays; d++) {
                model.addElement(d);
            }
            dayBox.setModel(model);
            dayBox.setSelectedItem(targetDay);
        } finally {
            updatingDays = false;
        }
    }

    public LocalDate getSelectedDate() {
        int year = (Integer) yearBox.getSelectedItem();
        int month = (Integer) monthBox.getSelectedItem();
        int day = (Integer) dayBox.getSelectedItem();
        return LocalDate.of(year, month, day);
    }

    public String getFormattedDate(DateTimeFormatter formatter) {
        DateTimeFormatter targetFormatter = (formatter != null) ? formatter : DateTimeFormatter.ISO_LOCAL_DATE;
        return getSelectedDate().format(targetFormatter);
    }

    private <T> JComboBox<T> createInputBox(T[] items) {
        JComboBox<T> box = new JComboBox<>(items);
        box.setFont(new Font("SansSerif", Font.BOLD, 13));
        box.setBackground(new Color(248, 250, 252));
        box.setFocusable(false);
        return box;
    }
}