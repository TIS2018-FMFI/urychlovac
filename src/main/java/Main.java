
import java.util.ArrayList;
import java.util.List;

/**
 * Main class - used to initiate and manage all necessary components of the system.
 *
 * @author FMPH
 */


public class Main {
    private static ArduinoCommunication arduinoData;
    private static VacControllerCommunication vacControllerCommunication;

    private static FrontEndSlave frontEnd;
    private static NotificationManager notificationManager;
    private static Configuration config;

    public Main() {
        config = new Configuration("config.toml");

        arduinoData = new ArduinoCommunication(0);
        arduinoData.start();

        vacControllerCommunication = new VacControllerCommunication();
        vacControllerCommunication.start();
    }

    public static void main(String[] args) {
        new Main();
    }

    public static Configuration getConfig() {
        return config;
    }
}
