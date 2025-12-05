import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ActivityLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private String logId;
    private String action;
    private String details;
    private LocalDateTime timestamp;

    public ActivityLog(String action, String details) {
        this.logId = UUID.randomUUID().toString().substring(0, 8);
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getLogId() { return logId; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s - %s (Log ID: %s)",
                timestamp.format(formatter), action, details, logId);
    }
}
