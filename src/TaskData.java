import java.time.LocalDate;

public class TaskData {
    private final String taskName;
    private final String taskTime;
    private final LocalDate startDate;
    private final String recurrence; // NONE, DAILY, WEEKLY, MONTHLY

    public TaskData(String taskName, String taskTime, LocalDate startDate, String recurrence) {
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