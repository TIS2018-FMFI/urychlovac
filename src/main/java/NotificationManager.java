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

    public void sendNotificationArduinoFault(int arduinoId){
        Main.getArduinoCommunication().sendMessage(3, "CHYBA - Arduino s id "+String.valueOf(arduinoId)+" sa nehlasi");
        Main.getArduinoCommunication().sendMessage(4, "CHYBA - Arduino s id "+String.valueOf(arduinoId)+" sa nehlasi");
    }

}
