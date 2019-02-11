import java.util.List;

/**
 * Notification manager class - used to monitor values in conditions of notifications and send notification when
 * conditions are met
 *
 * @author FMPH
 */

public class NotificationManager{

    public NotificationManager(){};

    public void sendNotification(NotificationRule rule) {
        Main.getArduinoCommunication().sendMessage(3, rule.getText());
        Main.getArduinoCommunication().sendMessage(4, rule.getText());
        //System.out.println("NotifManag: "+rule.getText());
        //TODO dorobit frontend funkciu
    };

    public void sendNotificationArduinoFault(int arduinoId, String message){
        Main.getArduinoCommunication().sendMessage(3, "CHYBA - Arduino id "+String.valueOf(arduinoId)+" - " + message);
        Main.getArduinoCommunication().sendMessage(4, "CHYBA - Arduino id "+String.valueOf(arduinoId)+" - " + message);

        //TODO dorobit frontend funkciu
    }

}
