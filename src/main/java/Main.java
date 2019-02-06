
import java.util.ArrayList;
import java.util.List;

/**
 * Main class - used to initiate and manage all necessary components of the system.
 *
 * @author FMPH
 */


public class Main {
    private static ArduinoCommunication arduinoData;

    private static FrontEndSlave frontEnd;
    private static NotificationManager notificationManager;
    private static Configuration config;

    public Main() {
        arduinoData = new ArduinoCommunication("230.1.1.1", 0);
        arduinoData.start();

        config = new Configuration("config.toml");
    }

    public static void main(String[] args) {
        new Main();
    }

    public static Configuration getConfig() {
        return config;
    }
}
