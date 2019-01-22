import java.util.List;

/**
 * Notification manager class - used to monitor values in conditions of notifications and send notification when
 * conditions are met
 *
 * @author FMPH
 */

public class NotificationManager extends Thread {
    private List<Notification> notifications;

    @Override
    public void run() {
        // check conditions of notifications, send notification if needed
    }
}
