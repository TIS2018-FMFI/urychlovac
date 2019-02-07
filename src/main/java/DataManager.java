import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data manager class - used to load, process and re-export logged measured data (e.g. vacuum, temperature)
 *
 * @author FMPH
 */

public class DataManager {
    private static DataManager ourInstance = new DataManager();
    private static final String CSV_SEPARATOR = ";";
    private static final Map<Integer, String> SENSORS;
    static {
        Map<Integer, String> aMap = new HashMap<>();
        aMap.put(0, "vacuum");
        aMap.put(1, "temperature");
        aMap.put(2, "door");
        aMap.put(3, "voltage");
        aMap.put(4, "gas");
        aMap.put(5, "coolant");
        //measured vacuum temperature
        //binary dvere napatie plyn chladiaca
        SENSORS = Collections.unmodifiableMap(aMap);
    }

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    public void addData(LabData data){
        if(checkData(data)) {
            String writeData = convertToCSV(data);
            if (data instanceof MeasuredData) {
                String fileName = SENSORS.get(data.getId());
                saveDataToFile(fileName+".txt", writeData);
            }
            if (data instanceof BinaryStatus) {
                saveDataToFile("binaryData.txt",writeData);
            }
        } else{
            // posli notifikaciu
        }
    }

    public String convertToCSV(LabData data){
        String writeData=null;
        if (data instanceof MeasuredData) {
            writeData = data.getId().toString()+CSV_SEPARATOR+data.getTimestamp().toString()+CSV_SEPARATOR+((MeasuredData) data).getValue();
        } else
        if (data instanceof BinaryStatus) {
            writeData = data.getId().toString()+CSV_SEPARATOR+data.getTimestamp().toString()+CSV_SEPARATOR+((BinaryStatus) data).isValue();
        }
        return writeData;
    }

    public boolean checkData(LabData data){
        // TO DO
        List<NotificationRule> rules = Main.getConfig().getNotificationRules();
        for (NotificationRule rule : rules) {
            //skontroluj data
        }
        return false;
    }

    public void saveDataToFile(String fileName, String data) {
        try(FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(data);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    public List<LabData> loadDataFromFile(String fileName) {
        List<LabData> result = Collections.emptyList();
        List<String> lines = Collections.emptyList();
        try
        {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    public List<LabData> loadDataSensorTimePeriod(int sensorId, int fromTime, int toTime) {
        return null;
    }

    public List<String> getListOfLogs(String fileName) {
        List<String> lines = Collections.emptyList();
        try
        {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return lines;
    }
}