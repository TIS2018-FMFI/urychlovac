import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Data manager class - used to load, process and re-export logged measured data (e.g. vacuum, temperature)
 *
 * @author FMPH
 */

public class DataManager {
    private static DataManager ourInstance = new DataManager();
    private static final String CSV_SEPARATOR = ";";

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    public void saveDataToFile(String fileName, LabData data) {
        try(FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            //pripise jeden riadok do suboru;
            String temp = "temporary solution";
            //String temp = data.getId().toString()+CSV_SEPARATOR+data.getTimestamp()+data.getValue();
            //String temp = data.getId().toString()+CSV_SEPARATOR+data.getTimestamp()+data.isValue().toString();
            out.println(temp);
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
    public List<LabData> loadDataSensorTimePeriod(int sensorId, int fromTime, int toTime) { return null; }

    public List<String> getListOfLogs() {return null;}
}