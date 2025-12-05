import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityLogger {
    private List<ActivityLog> activityLogs;
    private static final String LOG_FILE = "activity_logs.dat";

    public ActivityLogger() {
        this.activityLogs = new ArrayList<>();
        loadFromFile();
    }

    public void logActivity(String action, String details) {
        ActivityLog log = new ActivityLog(action, details);
        activityLogs.add(log);
        saveToFile();
    }

    public List<ActivityLog> getAllLogs() {
        return new ArrayList<>(activityLogs);
    }

    public List<ActivityLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return activityLogs.stream()
                .filter(log -> !log.getTimestamp().isBefore(start) &&
                        !log.getTimestamp().isAfter(end))
                .collect(Collectors.toList());
    }

    public List<ActivityLog> getRecentLogs(int count) {
        int size = activityLogs.size();
        int fromIndex = Math.max(0, size - count);
        return new ArrayList<>(activityLogs.subList(fromIndex, size));
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(LOG_FILE))) {
            oos.writeObject(activityLogs);
        } catch (IOException e) {
            System.err.println("Error saving activity logs: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(LOG_FILE))) {
            activityLogs = (List<ActivityLog>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading activity logs: " + e.getMessage());
            activityLogs = new ArrayList<>();
        }
    }

    public void displayAllLogs() {
        if (activityLogs.isEmpty()) {
            System.out.println("No activity logs available.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("          ACTIVITY LOGS");
        System.out.println("========================================");
        for (ActivityLog log : activityLogs) {
            System.out.println(log);
        }
        System.out.println("========================================");
    }

    public void displayRecentLogs(int count) {
        List<ActivityLog> recentLogs = getRecentLogs(count);
        if (recentLogs.isEmpty()) {
            System.out.println("No activity logs available.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("       RECENT ACTIVITY LOGS (Last " + count + ")");
        System.out.println("========================================");
        for (ActivityLog log : recentLogs) {
            System.out.println(log);
        }
        System.out.println("========================================");
    }
}
