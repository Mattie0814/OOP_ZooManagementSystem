import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TaskData {
    private String taskName;
    private String taskTime;
    private LocalDate startDate;
    private String recurrence; // NONE, DAILY, WEEKLY, MONTHLY, INTERVAL
    private int intervalDays;

    public TaskData(String taskName, String taskTime, LocalDate startDate, String recurrence) {
        this(taskName, taskTime, startDate, recurrence, 1);
    }

    public TaskData(String taskName, String taskTime, LocalDate startDate, String recurrence, int intervalDays) {
        this.taskName = taskName;
        this.taskTime = taskTime;
        this.startDate = startDate;
        this.recurrence = recurrence;
        this.intervalDays = Math.max(1, intervalDays);
    }

    public String getTaskName() { return taskName; }
    public String getTaskTime() { return taskTime; }
    public LocalDate getStartDate() { return startDate; }
    public String getRecurrence() {
        if ("INTERVAL".equals(recurrence)) {
            return "Every " + intervalDays + " day(s)";
        }
        return recurrence;
    }

    public String getType() { return "General"; }
    public String getDetails() { return "No additional details."; }

    public boolean occursOn(LocalDate date) {
        if (date.isBefore(startDate)) return false;

        switch (recurrence) {
            case "DAILY":
                return true;
            case "WEEKLY":
                return startDate.getDayOfWeek() == date.getDayOfWeek();
            case "MONTHLY":
                return startDate.getDayOfMonth() == date.getDayOfMonth();
            case "INTERVAL":
                long daysBetween = ChronoUnit.DAYS.between(startDate, date);
                return daysBetween % intervalDays == 0;
            default:
                return startDate.equals(date);
        }
    }

    @Override
    public String toString() {return "[" + getType() + "] " + taskName + " (" + taskTime + ") - " + getRecurrence();}
}