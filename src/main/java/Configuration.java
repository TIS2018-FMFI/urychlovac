import java.util.List;

/**
 * Used to store info about current configuration
 *
 * @author FMPH
 */

public class Configuration {
    private int loggingFrequency;
    private List<NotificationRule> notificationRules;

    public Configuration(String configFilePath) {

    }

    public int getLoggingFrequency() {
        return loggingFrequency;
    }

    public void setLoggingFrequency(int loggingFrequency) {
        this.loggingFrequency = loggingFrequency;
    }

    public List<NotificationRule> getNotificationRules() {
        return notificationRules;
    }

    public void setNotificationRules(List<NotificationRule> notificationRules) {
        this.notificationRules = notificationRules;
    }
}