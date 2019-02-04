
import java.util.List;

/**
 * Main class - used to initiate and manage all necessary components of the system.
 *
 * @author FMPH
 */


public class Main {
    private List<ArduinoCommunication> inputArduinos;  //for inputs - binary statuses, measuring sensors
    private List<ArduinoCommunication> outputArduinos; //for output - notifications

    private FrontEndSlave frontEnd;
    private NotificationManager notificationManager;
    private Configuration config;
    private DataManager dataManager;

    public Main() {

        config = new Configuration("config.toml");
    }

    public static void main(String[] args) {
        new Main();
    }

    public Configuration getConfig() {
        return config;
    }
}
