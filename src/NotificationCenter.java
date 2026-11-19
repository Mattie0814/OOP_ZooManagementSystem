import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

final class NotificationCenter {
    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final LocalTime HEALTH_REMINDER_TIME = LocalTime.of(9, 0);
    private static final DateTimeFormatter TASK_TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    private final Map<TaskData, LocalDate> notifiedTasks = new IdentityHashMap<>();
    private final Set<String> notifiedLowStockItems = new HashSet<>();
    private LocalDate lastHealthDashboardCheckDate;
    private LocalDate lastHealthScheduledReminderDate;
    private String lastUnhealthyAnimalSignature = "";

    List<String> getNewDashboardNotifications(LocalDateTime now, UserData user) {
        List<String> notifications = getDueTaskNotifications(now, user, notifiedTasks);

        List<String> unhealthyAnimals = getUnhealthyAnimalNotifications();
        LocalDate today = now.toLocalDate();
        boolean firstDashboardCheck = !today.equals(lastHealthDashboardCheckDate);
        boolean scheduledHealthReminder = !now.toLocalTime().isBefore(HEALTH_REMINDER_TIME)
                && !today.equals(lastHealthScheduledReminderDate);
        boolean healthConditionChanged = !unhealthyAnimals.isEmpty()
                && !String.join("\n", unhealthyAnimals).equals(lastUnhealthyAnimalSignature);
        lastUnhealthyAnimalSignature = String.join("\n", unhealthyAnimals);

        if (firstDashboardCheck) {
            lastHealthDashboardCheckDate = today;
        }
        if (scheduledHealthReminder) {
            lastHealthScheduledReminderDate = today;
        }
        if (!unhealthyAnimals.isEmpty()
                && (healthConditionChanged || firstDashboardCheck || scheduledHealthReminder)) {
            String reminderType = healthConditionChanged
                    ? "Animal health alert and daily check"
                    : firstDashboardCheck && scheduledHealthReminder
                            ? "Daily animal health check (first dashboard check and 9:00 AM reminder)"
                            : firstDashboardCheck
                                    ? "Daily animal health check"
                                    : "9:00 AM animal health reminder";
            notifications.add(reminderType + ":\n" + String.join("\n", unhealthyAnimals));
        }

        Set<String> currentlyLowStock = new HashSet<>();
        for (ItemData item : getInventory()) {
            if (item.getStock() <= LOW_STOCK_THRESHOLD) {
                currentlyLowStock.add(item.getID());
                if (notifiedLowStockItems.add(item.getID())) {
                    notifications.add(formatLowStockNotification(item));
                }
            }
        }
        notifiedLowStockItems.retainAll(currentlyLowStock);

        return notifications;
    }

    static List<String> getCurrentNotifications(LocalDateTime now, UserData user) {
        List<String> notifications = getDueTaskNotifications(now, user, null);
        notifications.addAll(getUnhealthyAnimalNotifications());

        for (ItemData item : getInventory()) {
            if (item.getStock() <= LOW_STOCK_THRESHOLD) {
                notifications.add(formatLowStockNotification(item));
            }
        }
        return notifications;
    }

    private static List<String> getDueTaskNotifications(
            LocalDateTime now, UserData user, Map<TaskData, LocalDate> notifiedTasks) {
        List<String> notifications = new ArrayList<>();
        if (user == null || user.getSchedule() == null) {
            return notifications;
        }

        LocalDate date = now.toLocalDate();
        LocalTime currentMinute = now.toLocalTime().withSecond(0).withNano(0);
        for (TaskData task : user.getSchedule()) {
            if (task == null || task.getStartDate() == null || !task.occursOn(date)) {
                continue;
            }

            LocalTime taskTime = parseTaskTime(task.getTaskTime());
            if (taskTime == null || !taskTime.equals(currentMinute)) {
                continue;
            }
            if (notifiedTasks != null && date.equals(notifiedTasks.get(task))) {
                continue;
            }

            notifications.add("Task starts now: " + task.getTaskName() + " (" + task.getTaskTime() + ")");
            if (notifiedTasks != null) {
                notifiedTasks.put(task, date);
            }
        }
        return notifications;
    }

    private static LocalTime parseTaskTime(String taskTime) {
        if (taskTime == null) {
            return null;
        }
        try {
            return LocalTime.parse(taskTime.trim(), TASK_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private static List<String> getUnhealthyAnimalNotifications() {
        List<String> notifications = new ArrayList<>();
        List<EnclosureData> enclosures = Database.getEnclosureList();
        if (enclosures == null) {
            return notifications;
        }

        for (EnclosureData enclosure : enclosures) {
            for (AnimalData animal : enclosure.getCurrentOccupants()) {
                String healthStatus = animal.getHealthStatus();
                if (healthStatus != null && !healthStatus.trim().isEmpty()
                        && !"healthy".equalsIgnoreCase(healthStatus.trim())) {
                    notifications.add("- " + animal.getName() + " (" + animal.getSpecies()
                            + ") in " + enclosure.getName() + ": " + healthStatus);
                }
            }
        }
        return notifications;
    }

    private static String formatLowStockNotification(ItemData item) {
        return "Low stock: " + item.getName() + " (" + item.getID() + ") has "
                + item.getStock() + " unit(s) remaining.";
    }

    private static List<ItemData> getInventory() {
        List<ItemData> inventory = Database.getInventoryList();
        return inventory != null ? inventory : new ArrayList<>();
    }
}
