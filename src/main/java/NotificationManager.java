import java.io.*;
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
        File file = new File(DataManager.getInstance().getRootPath()+"NOTIFICATIONS.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(FileWriter fw = new FileWriter(DataManager.getInstance().getRootPath()+"NOTIFICATIONS.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(rule.getText());
            System.out.println("POSIELAM"+rule.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO dorobit frontend funkciu
    };

    public void sendNotificationArduinoFault(int arduinoId, String message){
        Main.getArduinoCommunication().sendMessage(3, "CHYBA - Arduino id "+String.valueOf(arduinoId)+" - " + message);
        Main.getArduinoCommunication().sendMessage(4, "CHYBA - Arduino id "+String.valueOf(arduinoId)+" - " + message);

        //TODO dorobit frontend funkciu
    }

}
