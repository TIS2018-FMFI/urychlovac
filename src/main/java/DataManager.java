import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Data manager class - used to load, process and re-export logged measured data (e.g. vacuum, temperature)
 *
 * @author FMPH
 */

public class DataManager {
    private static DataManager ourInstance = new DataManager();
    private static final String CSV_SEPARATOR = ";";
    private static String ROOT_PATH="/home/piestany/BACKEND_TEST/";

    private static final Map<Integer, String> SENSORS;
    static {
        Map<Integer, String> aMap = new HashMap<>();
        aMap.put(0, "DHT22_temperature"); //teplota measured
        aMap.put(1, "DHT22_humidity"); //vlhkost measured
        aMap.put(2, "ReedSwitch"); //Dvere boolean
        aMap.put(3, "DS18_cooling_water_temp"); //teplota measured
        aMap.put(4, "Optic_water_level"); //stav hladiny boolean
        aMap.put(5, "Vacuum_gauge"); //stav vakua boolean
        SENSORS = Collections.unmodifiableMap(aMap);
    }

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    public void initFiles(){
        for(Integer key : SENSORS.keySet()){
            File file = new File(ROOT_PATH+SENSORS.get(key)+".txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addData(LabData data){
        long freq = Main.getConfig().getLoggingFrequency();
        checkData(data);
            String fileName = SENSORS.get(data.getId());
            if(timePassed(data.getId(),freq)) {
                String writeData = convertToCSV(data);
                if (data instanceof MeasuredData) {
                    saveDataToFile(ROOT_PATH+fileName, writeData);
                }
                if (data instanceof BinaryStatus) {
                    saveDataToFile(ROOT_PATH+fileName,writeData);
                }
            }
    }

    public boolean timePassed(int sensorId, long duration){
        String result="";
        File file = new File(ROOT_PATH+SENSORS.get(sensorId)+".txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null){
                //System.out.println(st);
                result=st;
            }
            if (result==""){
                return true;
            } else{
                String[] line = result.split(CSV_SEPARATOR);
                Date date =new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(line[1]);
                //System.out.println(date.toString());
                //System.out.println(Boolean.valueOf(new Date().getTime()-date.getTime()>=duration));
                //System.out.println(""+(new Date().getTime()-date.getTime())+" "+duration);
                if (Integer.parseInt(line[0])==sensorId && new Date().getTime()-date.getTime()>=duration){
                    return true;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
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
        //List<NotificationRule> rules = Main.getConfig().getNotificationRules();
        for (NotificationRule rule : Main.getConfig().getNotificationRules()) {
            //System.out.println(rule.getText());
            String[] control = rule.getRule().split("&");
            //System.out.println(control[0]);
            for (String condition : control){
                if(data instanceof BinaryStatus){
                    if(data.getId() == Integer.parseInt(condition) && !((BinaryStatus) data).isValue()){
                        Main.getNotificationManager().sendNotification(rule);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void saveDataToFile(String fileName, String data) {
        try(FileWriter fw = new FileWriter(ROOT_PATH+fileName+".txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<LabData> loadDataFromFile(String fileName) {
        List<LabData> result = new ArrayList<>();
        File file = new File(ROOT_PATH+fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null){
                String[] data = st.split(CSV_SEPARATOR);
                int sensorId = Integer.parseInt(data[0]);
                Date time =new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(data[1]);
                if(data[2].equals("true") || data[2].equals("false")){
                    result.add(new BinaryStatus(sensorId, time, Boolean.valueOf(data[2])));
                } else {
                    result.add(new MeasuredData(sensorId, time, Float.parseFloat(data[2])));
                }
            }
        } catch (Exception e){
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
        List<LabData> lines = loadDataFromFile(ROOT_PATH+SENSORS.get(sensorId)+".txt");
        List<LabData> result = new ArrayList<>();
        for (LabData data : lines){
            if (data.getId()==sensorId && (data.getTimestamp().after(fromTime) && data.getTimestamp().before(toTime))){
                result.add(data);
            }
        }
        return result;
    }

    public List<String> getListOfLogs(String fileName) {
        List<String> result = new ArrayList<>();
        File file = new File(ROOT_PATH+fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null){
                //System.out.println(st);
                result.add(st);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}