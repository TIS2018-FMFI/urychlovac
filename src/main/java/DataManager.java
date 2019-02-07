import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

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
        long freq = Main.getConfig().getLoggingFrequency();
        if(checkData(data)) {
            if(timePassed("filename",data.getId(),freq)) {
                String writeData = convertToCSV(data);
                if (data instanceof MeasuredData) {
                    String fileName = SENSORS.get(data.getId());
                    saveDataToFile(fileName+".txt", writeData);
                }
                if (data instanceof BinaryStatus) {
                    saveDataToFile("binaryData.txt",writeData);
                }
            }
        } else{
            //sendNotification();
        }
    }

    public boolean timePassed(String fileName, int id, long duration){
        List<String> lines = Collections.emptyList();
        try
        {
            //ZISTIT NAZOV TEXTAKU
            lines = Files.readAllLines(Paths.get(SENSORS.get(id)+".txt"), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        for (int i= lines.size();i>=0;i--){
            String[] line = lines.get(i).split(CSV_SEPARATOR);
            Date date = new Date(line[1]);
            if (Integer.parseInt(line[0])==id && new Date().getTime()-date.getTime()>duration){
                return true;
            }
        }
        return false;
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
            String[] control = rule.getRule().split("&");
            for (String condition : control){
                if(data instanceof BinaryStatus){
                    if(data.getId() == Integer.parseInt(condition) && ((BinaryStatus) data).isValue()){
                        return true;
                    }
                    else return false;
                }
            }

        }
        return true;
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
    public List<LabData> loadDataSensorTimePeriod(int sensorId, Date fromTime, Date toTime) {
        // ZISTIT CI BINARY ALEBO MEASUERED
        List<LabData> result = new ArrayList<>();
        List<String> lines = Collections.emptyList();
        try
        {
            //ZISTIT NAZOV TEXTAKU
            lines = Files.readAllLines(Paths.get(SENSORS.get(sensorId)+".txt"), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        for (String line : lines){
            String[] data = line.split(CSV_SEPARATOR);
            //STRING TO TIMESTAMP , ZMENIT FUNKCIU NA FROM TIMESTAMP TO TIMESTAMP
            //data[1] = timestamp
            Date time = new Date(data[1]);
            if (Integer.parseInt(data[0])==sensorId && (time.after(fromTime) && time.before(toTime))){
               // result.add(new LabData(sensorId, time, data[2]));
            }
        }
        return result;
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