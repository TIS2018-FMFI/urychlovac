import net.consensys.cava.toml.Toml;
import net.consensys.cava.toml.TomlParseResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to store info about current configuration
 *
 * @author FMPH
 */

public class Configuration {
    private long loggingFrequency;
    private List<NotificationRule> notificationRules;

    public Configuration(String configFilePath) {

        InputStream is = Configuration.class.getResourceAsStream(configFilePath);
        TomlParseResult result = null;
        try {
            result = Toml.parse(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result != null){
            result.errors().forEach(error -> System.err.println(error.toString()));
            setLoggingFrequency(result.getLong("intervals.logging_frequency"));

            ArrayList<NotificationRule> lst = new ArrayList<>();

            String crisis_prefix = "crisis_situations";

            String highVoltageDoors_prefix = "high_voltage_doors_open";
            lst.add(new NotificationRule(
                    result.getString(crisis_prefix + "." + highVoltageDoors_prefix + ". name"),
                    result.getLong(crisis_prefix + "." + highVoltageDoors_prefix + ". level"),
                    result.getString(crisis_prefix + "." + highVoltageDoors_prefix + ". condition")
            ));


            String gas_leak_prefix = "gas_leak";
            lst.add(new NotificationRule(
                    result.getString(crisis_prefix + "." + highVoltageDoors_prefix + ". name"),
                    result.getLong(crisis_prefix + "." + highVoltageDoors_prefix + ". level"),
                    result.getString(crisis_prefix + "." + highVoltageDoors_prefix + ". condition")
            ));

            String cooling_fluid_low = "cooling_fluid_low";
            lst.add(new NotificationRule(
                    result.getString(crisis_prefix + "." + highVoltageDoors_prefix + ". name"),
                    result.getLong(crisis_prefix + "." + highVoltageDoors_prefix + ". level"),
                    result.getString(crisis_prefix + "." + highVoltageDoors_prefix + ". condition")
            ));

            setNotificationRules(lst);

            // TEST:
            System.out.println(getLoggingFrequency());
            System.out.println(getNotificationRules());
        } else {
            System.err.println("ERROR! Configuration: result is null!");
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
}