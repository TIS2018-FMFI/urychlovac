import net.consensys.cava.toml.Toml;
import net.consensys.cava.toml.TomlParseResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used to store info about current configuration
 *
 * @author FMPH
 */

public class Configuration {
    private long loggingFrequency;
    private List<NotificationRule> notificationRules;
    private HashMap<Integer, String> arduinoIpMap;
    static String  ROOT_PATH = "/home/piestany/urychlovac/";


    public Configuration() {

        String configFilePath = getRootPath() + "config.toml";
        Path source = Paths.get(configFilePath);

        TomlParseResult result = null;

        try {
            result = Toml.parse(source);
        } catch (IOException e) {
            System.out.println("Configuration: ERROR! Toml parsing unsuccessful!");
            e.printStackTrace();
        }

        if (result != null){
            result.errors().forEach(error -> System.err.println(error.toString()));
            setLoggingFrequency(result.getLong("intervals . logging_frequency"));

            ArrayList<NotificationRule> lst = new ArrayList<>();

            String crisis_prefix = "crisis_situations.";
            String name_suffix = ".name";
            String level_suffix = ".level";
            String condition_suffix = ".condition";

            String highVoltageDoors_string = "high_voltage_doors_open";
            lst.add(new NotificationRule(
                    result.getString(crisis_prefix + highVoltageDoors_string + name_suffix),
                    result.getLong(crisis_prefix + highVoltageDoors_string + level_suffix),
                    result.getString(crisis_prefix + highVoltageDoors_string + condition_suffix)
            ));


            String gas_leak_string = "gas_leak";
            lst.add(new NotificationRule(
                    result.getString(crisis_prefix + gas_leak_string + name_suffix),
                    result.getLong(crisis_prefix + gas_leak_string + level_suffix),
                    result.getString(crisis_prefix + gas_leak_string + condition_suffix)
            ));

            String cooling_fluid_low_string = "cooling_fluid_low";
            lst.add(new NotificationRule(
                    result.getString(crisis_prefix + cooling_fluid_low_string + name_suffix),
                    result.getLong(crisis_prefix + cooling_fluid_low_string + level_suffix),
                    result.getString(crisis_prefix + cooling_fluid_low_string + condition_suffix)
            ));

            setNotificationRules(lst);


            // Mapa ip-ciek Arduin:
            HashMap newArduinoIpMap = new HashMap<>();
            String arduinoIpAddress_prefix = "ip_addreeses.arduino.";
            String ip_suffix = ".ip";

            newArduinoIpMap.put(0, result.getString(arduinoIpAddress_prefix + "0" + ip_suffix));
            newArduinoIpMap.put(1, result.getString(arduinoIpAddress_prefix + "1" + ip_suffix));
            newArduinoIpMap.put(2, result.getString(arduinoIpAddress_prefix + "2" + ip_suffix));
            newArduinoIpMap.put(3, result.getString(arduinoIpAddress_prefix + "3" + ip_suffix));
            newArduinoIpMap.put(4, result.getString(arduinoIpAddress_prefix + "4" + ip_suffix));

            setArduinoIpMap(newArduinoIpMap);


            // TEST:
            System.out.println("\n################ CONFIGURATION TEST: ################ \n");
            System.out.println("Logging frequency: " + getLoggingFrequency());
            System.out.println("Notification rules (count): " + getNotificationRules().size());
            System.out.println("\n############# End of CONFIGURATION TEST #############");

        } else {
            System.out.println("Configuration: ERROR! Toml parsing unsuccessful!");
        }


    }

    public long getLoggingFrequency() {
        return loggingFrequency;
    }

    private void setLoggingFrequency(long loggingFrequency) {
        this.loggingFrequency = loggingFrequency;
    }

    public List<NotificationRule> getNotificationRules() {
        return notificationRules;
    }

    private void setNotificationRules(List<NotificationRule> notificationRules) {
        this.notificationRules = notificationRules;
    }

    public HashMap<Integer, String> getArduinoIpMap() {
        return arduinoIpMap;
    }

    public void setArduinoIpMap(HashMap<Integer, String> arduinoIpMap) {
        this.arduinoIpMap = arduinoIpMap;
    }

    public static String getRootPath() {
        return ROOT_PATH;
    }
}