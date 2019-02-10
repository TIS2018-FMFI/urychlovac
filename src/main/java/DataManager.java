import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static String ROOT_PATH="";
    private static final Map<Integer, String> SENSORS;
    static {
        Map<Integer, String> aMap = new HashMap<>();
        aMap.put(0, "DHT22_temperature"); //teplota measured
        aMap.put(1, "DHT22_humidity"); //vlhkost measured
        aMap.put(2, "ReedSwitch"); //Dvere boolean
        aMap.put(3, "DS18_cooling_water_temp"); //teplota measured
        aMap.put(4, "Optic_water_level"); //stav hladiny boolean
        aMap.put(5, "Vacuum_gauge"); //stav vakua boolean
        //measured: vacuum temperature
        //binary: dvere napatie plyn chladiaca
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
                    //System.out.println("ahoj");
                    saveDataToFile(ROOT_PATH+fileName,writeData);
                }
            }

    }

    public boolean timePassed(int sensorId, long duration){

        //String aktdir = System.getProperty("user.dir");
        //System.out.println(ROOT_PATH+SENSORS.get(sensorId)+".txt");

       // File reqFile= new File(aktdir,ROOT_PATH+SENSORS.get(sensorId)+".txt");


        //File file = new File("C:\\Users\\micha\\OneDrive\\Matfyz\\3.rocnik\\TIS\\urychlovac\\DHT22_temperature.txt");
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
                if (Integer.parseInt(line[0])==sensorId && new Date().getTime()-date.getTime()>=duration){
                    return true;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

       /* List<String> lines = new ArrayList<String>();
        try
        {
            System.out.println(Paths.get(ROOT_PATH+SENSORS.get(sensorId)+".txt"));
            lines = Files.readAllLines(Paths.get(), StandardCharsets.UTF_8);
            System.out.println("kokos");
            System.out.println(lines.size());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (lines.size()==0){
            //System.out.println("ahoj");
            return true;
        } else{
            for (int i= lines.size()-1;i>=0;i--){
                String[] line = lines.get(i).split(CSV_SEPARATOR);
                Date date = new Date(line[1]);
                System.out.println(date.toString());
                if (Integer.parseInt(line[0])==sensorId && new Date().getTime()-date.getTime()>=duration){
                    System.out.println(new Date().getTime()-date.getTime()>=duration);
                    return true;
                }
            }
        }*/
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
                    if(data.getId() == Integer.parseInt(condition) && ((BinaryStatus) data).isValue() || data.getId() != Integer.parseInt(condition)){
                        return true;
                    }

                    //TODO poslat notifikaciu s notif rule
                    else{
                        //System.out.println(rule.getText());
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
            //exception handling left as an exercise for the reader
            e.printStackTrace();
        }
    }

    public List<LabData> loadDataFromFile(String fileName) {
        //String result="";
        List<LabData> result = new ArrayList<>();
        File file = new File(ROOT_PATH+fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null){
                //System.out.println(st);

                String[] data = st.split(CSV_SEPARATOR);
                int sensorId = Integer.parseInt(data[0]);
                //Date time = new Date(data[1]);

                Date time =new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(data[1]);
                if(data[2].equals("true") || data[2].equals("false")){
                    result.add(new BinaryStatus(sensorId, time, Boolean.valueOf(data[2])));
                } else {
                    result.add(new MeasuredData(sensorId, time, Float.parseFloat(data[2])));
                }
                //result=st;
            }
           /* if (result==""){
                return true;
            } else{
                String[] line = result.split(CSV_SEPARATOR);
                Date date =new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(line[1]);
                //System.out.println(date.toString());
                //System.out.println(Boolean.valueOf(new Date().getTime()-date.getTime()>=duration));
                if (Integer.parseInt(line[0])==sensorId && new Date().getTime()-date.getTime()>=duration){
                    return true;
                }
            }*/
        } catch (Exception e){
            e.printStackTrace();
        }




       /*
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

        }*/
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

        /*
        List<String> lines = Collections.emptyList();
        try
        {
            lines = Files.readAllLines(Paths.get(ROOT_PATH+fileName+".txt"), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }*/
        return result;
    }
}