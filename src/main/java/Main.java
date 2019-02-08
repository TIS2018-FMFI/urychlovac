
import java.util.ArrayList;
import java.util.List;

/**
 * Main class - used to initiate and manage all necessary components of the system.
 *
 * @author FMPH
 */


public class Main {
    private static ArduinoCommunication arduinoData1;
    private static ArduinoCommunication arduinoData2;
    private static ArduinoCommunication arduinoData3;
    private static ArduinoCommunication arduinoNotif1;
    private static ArduinoCommunication arduinoNotif2;

    private static FrontEndSlave frontEnd;
    private static NotificationManager notificationManager;
    private static Configuration config;

    public Main() {
        config = new Configuration("config.toml");

        arduinoData1 = new ArduinoCommunication("192.168.1.100",0);
        arduinoData1.start();

        arduinoData2 = new ArduinoCommunication("192.168.1.100",1);
        arduinoData2.start();

        arduinoData3 = new ArduinoCommunication("192.168.1.100",2);
        arduinoData3.start();

        arduinoNotif1 = new ArduinoCommunication("192.168.1.101",3);
        arduinoNotif1.start();

        arduinoNotif2 = new ArduinoCommunication("192.168.1.102",4);
        arduinoNotif2.start();
    }

    public static void main(String[] args) {
        new Main();
    }

    public static Configuration getConfig() {
        return config;
    }
}
