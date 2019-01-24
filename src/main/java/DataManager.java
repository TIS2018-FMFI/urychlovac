import java.util.List;

/**
 * Data manager class - used to load, process and re-export logged measured data (e.g. vacuum, temperature)
 *
 * @author FMPH
 */

public class DataManager {
    private static DataManager ourInstance = new DataManager();

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    public void saveDataToFile(String fileName, LabData data) { }

    public List<LabData> loadDataFromFile(String fileName) {
        return null;
    }
    public List<LabData> loadDataSensorTimePeriod(int sensorId, int fromTime, int toTime) { return null; }

    public List<String> getListOfLogs() {return null;}
}
