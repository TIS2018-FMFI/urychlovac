import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Data manager class - used to load, process and re-export logged measured data (e.g. vacuum, temperature)
 *
 * @author FMPH
 */

public class DataManager {
    private static DataManager ourInstance = new DataManager();
    private static final String CSV_SEPARATOR = ";";
    private static String ROOT_PATH="";
    private static final Map<Integer, String> SENSORS;
    static {
        Map<Integer, String> aMap = new HashMap<>();
        aMap.put(0, "vacuum");
        aMap.put(1, "temperature");
        aMap.put(2, "door");
        aMap.put(3, "voltage");
        aMap.put(4, "gas");
        aMap.put(5, "coolant");
        //measured: vacuum temperature
        //binary: dvere napatie plyn chladiaca
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
            String fileName = SENSORS.get(data.getId());
            if(timePassed(data.getId(),freq)) {
                String writeData = convertToCSV(data);
                if (data instanceof MeasuredData) {

                    saveDataToFile(ROOT_PATH+fileName+".txt", writeData);
                }
                if (data instanceof BinaryStatus) {
                    saveDataToFile(ROOT_PATH+fileName+".txt",writeData);
                }
            }
        } else{
            //sendNotification();
        }
    }

    public boolean timePassed(int sensorId, long duration){
        List<String> lines = Collections.emptyList();
        try
        {
            lines = Files.readAllLines(Paths.get(ROOT_PATH+SENSORS.get(sensorId)+".txt"), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        for (int i= lines.size()-1;i>=0;i--){
            String[] line = lines.get(i).split(CSV_SEPARATOR);
            Date date = new Date(line[1]);
            if (Integer.parseInt(line[0])==sensorId && new Date().getTime()-date.getTime()>=duration){
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
        List<NotificationRule> rules = Main.getConfig().getNotificationRules();
        for (NotificationRule rule : rules) {
            String[] control = rule.getRule().split("&");
            for (String condition : control){
                if(data instanceof BinaryStatus){
                    if(data.getId() == Integer.parseInt(condition) && ((BinaryStatus) data).isValue() || data.getId() != Integer.parseInt(condition)){
                        return true;
                    }

                    //TODO poslat notifikaciu s notif rule
                    else{
                        Main.getNotificationManager().sendNotification(rule);
                        return false;
                    }
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
            e.printStackTrace();
        }
    }

    public List<LabData> loadDataFromFile(String fileName) {
        List<LabData> result = Collections.emptyList();
        List<String> lines = Collections.emptyList();
        try
        {
            lines = Files.readAllLines(Paths.get(ROOT_PATH+fileName+".txt"), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        for (String line : lines){
            String[] data = line.split(CSV_SEPARATOR);
            int sensorId = Integer.parseInt(data[0]);
            Date time = new Date(data[1]);
            if(data[2].equals("true") || data[2].equals("false")){
                result.add(new BinaryStatus(sensorId, time, Boolean.valueOf(data[2])));
            } else {
                result.add(new MeasuredData(sensorId, time, Float.parseFloat(data[2])));
            }

        }
        return result;
    }
    public List<LabData> loadDataSensorTimePeriod(int sensorId, Date fromTime, Date toTime) {
        List<LabData> result = loadDataFromFile(ROOT_PATH+SENSORS.get(sensorId)+".txt");
        for (LabData data : result){
            if (data.getId()==sensorId && (data.getTimestamp().after(fromTime) && data.getTimestamp().before(toTime))){
                result.add(data);
            }
        }
        return result;
    /*    // ZISTIT CI BINARY ALEBO MEASUERED
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
        return result;*/
    }

    public List<String> getListOfLogs(String fileName) {
        List<String> lines = Collections.emptyList();
        try
        {
            lines = Files.readAllLines(Paths.get(ROOT_PATH+fileName+".txt"), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return lines;
    }
}