import java.util.List;

/**
 * Notification manager class - used to monitor values in conditions of notifications and send notification when
 * conditions are met
 *
 * @author FMPH
 */

public class NotificationManager{

    public void sendNotification(NotificationRule rule) {
        Main.getArduinoNotif1().sendMessage(rule.getText());
        Main.getArduinoNotif2().sendMessage(rule.getText());
        //TODO dorobit frontend funkciu
    };

    public void sendNotificationArduinoFault(int arduinoId){
        Main.getArduinoNotif1().sendMessage("CHYBA - Arduino s id "+String.valueOf(arduinoId)+" sa nehlasi");
        Main.getArduinoNotif2().sendMessage("CHYBA - Arduino s id "+String.valueOf(arduinoId)+" sa nehlasi");
    }

}
