import java.io.*;
import java.text.Format;
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
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static String LOGS_PATH = "logs" + FILE_SEPARATOR;      // for archived logs
    private static String DATA_PATH = "www"  + FILE_SEPARATOR + "data" + FILE_SEPARATOR;    // for frontend

    public static String getLogsPath() {
        return LOGS_PATH;
    }

    private static String ARCHIVE_PATH_SUFFIX = "archive" + FILE_SEPARATOR;
    private static long NrLines= 605000000/Main.getConfig().getLoggingFrequency();

    private static final Map<Integer, String> SENSORS;
    static {
        Map<Integer, String> aMap = new HashMap<>();
        /***Arduino pri generatore napatia***/
        aMap.put(0, "DHT22_temperature_a1_1"); //teplota measured, arduino id 1, senzor 1
        aMap.put(1, "DHT22_humidity_a1_1"); //vlhkost measured, arduino id 1, senzor 1

        aMap.put(2, "DHT22_temperature_a1_2"); //teplota measured, arduino id 1, senzor 2
        aMap.put(3, "DHT22_humidity_a1_2"); //vlhkost measured, arduino id 1, senzor 2

        aMap.put(4, "DHT22_temperature_a1_3"); //teplota measured, arduino id 1, senzor 3
        aMap.put(5, "DHT22_humidity_a1_3"); //vlhkost measured, arduino id 1, senzor 3

        aMap.put(6, "DHT22_temperature_a1_4"); //teplota measured, arduino id 1, senzor 4
        aMap.put(7, "DHT22_humidity_a1_4"); //vlhkost measured, arduino id 1, senzor 4
        /******/

        /***Arduino pri hlavni urychlovaca***/
        aMap.put(8, "DHT22_temperature_a2_1"); //teplota measured, arduino id 2, senzor 1
        aMap.put(9, "DHT22_humidity_a2_1"); //vlhkost measured, arduino id 2, senzor 1

        aMap.put(10, "DHT22_temperature_a2_2"); //teplota measured, arduino id 2, senzor 2
        aMap.put(11, "DHT22_humidity_a2_2"); //vlhkost measured, arduino id 2, senzor 2

        aMap.put(12, "DHT22_temperature_a2_3"); //teplota measured, arduino id 2, senzor 3
        aMap.put(13, "DHT22_humidity_a2_3"); //vlhkost measured, arduino id 2, senzor 3

        aMap.put(14, "DHT22_temperature_a2_4"); //teplota measured, arduino id 2, senzor 4
        aMap.put(15, "DHT22_humidity_a2_4"); //vlhkost measured, arduino id 2, senzor 4
        /******/

        /***Arduino na stlpe***/
        aMap.put(20, "DHT22_temperature_a3_1"); //teplota measured, arduino id 0, senzor 1
        aMap.put(21, "DHT22_humidity_a3_1"); //vlhkost measured, arduino id 0, senzor 1

        aMap.put(22, "DS18_coolant_temp_1"); //teplota measured
        aMap.put(23, "DS18_coolant_temp_2"); //teplota measured

        aMap.put(24, "coolant level"); //stav hladiny chladiacej kvapaliny boolean

        aMap.put(25, "front_door_switch"); //Predne dvere boolean
        aMap.put(26, "back_door_switch"); //Zadne dvere boolean

        aMap.put(27, "high_voltage"); //Pustene vysoke napatie boolean
        /******/

        /***Vakuova mierka***/
        aMap.put(30, "Vacuum_gauge"); //stav vakua boolean
        /******/

        SENSORS = Collections.unmodifiableMap(aMap);
    }

    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }

    public void initFiles(){
        for(Integer key : SENSORS.keySet()){
            File file = new File(DATA_PATH +SENSORS.get(key)+".csv");
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("DATA LOGGING: Failed creating file for frontend!");
                e.printStackTrace();
            }
        }
    }

    public void addData(LabData data){
        long freq = Main.getConfig().getLoggingFrequency();
        checkData(data);        // check notification conditions
        String fileName = DATA_PATH + SENSORS.get(data.getId()) + ".csv";
        if(timePassed(data.getId(),freq)) {
            String writeData = convertToCSV(data);
            Calendar cal = Calendar.getInstance();
            cal.setTime(data.getTimestamp());
            int week = cal.get(Calendar.WEEK_OF_YEAR);
            int year = cal.get(Calendar.YEAR);
            String archiveFilePathName = LOGS_PATH + SENSORS.get(data.getId()) + "_" + year + "_" + week + ".csv";
            File file = new File(archiveFilePathName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("DATA LOGGING: Failed to create file " + archiveFilePathName + "!");
                e.printStackTrace();
            }

            saveDataToFile(fileName, writeData);
            saveDataToFile(archiveFilePathName, writeData);
            /*
            if (data instanceof MeasuredData) {
                saveDataToFile(fileName, writeData);
                saveDataToFile(ARCHIVE_PATH_SUFFIX +fileName+"_"+year+"_"+week, writeData);
            }
            if (data instanceof BinaryStatus) {
                saveDataToFile(fileName, writeData);
                saveDataToFile(ARCHIVE_PATH_SUFFIX +fileName+"_"+year+"_"+week, writeData);
            }*/
        }
    }

    public boolean timePassed(int sensorId, long duration){
        String result="";

        int linecount =0;
        File file = new File(DATA_PATH + SENSORS.get(sensorId) + ".csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null){
                //System.out.println(st);
                result=st;
                linecount++;
            }
            if (result==""){
                return true;
            } else{
//                String[] firstLineArr = firstLine.split(CSV_SEPARATOR);
//                Date dateFirst = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(firstLineArr[1]);
                String[] line = result.split(CSV_SEPARATOR);
                Date date =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(line[1]);
                //System.out.println(date.toString());
                //System.out.println(Boolean.valueOf(new Date().getTime()-date.getTime()>=duration));
                //System.out.println(""+(new Date().getTime()-date.getTime())+" "+duration);
                if (Integer.parseInt(line[0])==sensorId && new Date().getTime()-date.getTime()>=duration){
                    if (NrLines>=linecount){
                        removeFirstLine(DATA_PATH +SENSORS.get(sensorId)+".csv");
                    }
                    return true;
                }
            }
        } catch (Exception e){
            System.out.println("DATA LOGGING: Error checking last update time!");
            e.printStackTrace();
        }
        return false;
    }

    public void removeFirstLine(String fileName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        //Initial write position
        long writePosition = raf.getFilePointer();
        raf.readLine();
        // Shift the next lines upwards.
        long readPosition = raf.getFilePointer();

        byte[] buff = new byte[1024];
        int n;
        while (-1 != (n = raf.read(buff))) {
            raf.seek(writePosition);
            raf.write(buff, 0, n);
            readPosition += n;
            writePosition += n;
            raf.seek(readPosition);
        }
        raf.setLength(writePosition);
        raf.close();
    }

    public String convertToCSV(LabData data){
        String writeData=null;
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = ((SimpleDateFormat) formatter).format(data.getTimestamp());
        if (data instanceof MeasuredData) {
            writeData = data.getId().toString()+CSV_SEPARATOR+date+CSV_SEPARATOR+((MeasuredData) data).getValue();
        } else
        if (data instanceof BinaryStatus) {
            writeData = data.getId().toString()+CSV_SEPARATOR+date+CSV_SEPARATOR+((BinaryStatus) data).isValue();
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
        try(FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(data);
        } catch (IOException e) {
            System.out.println("DATA LOGGING: Error saving frontend data to file!");
            e.printStackTrace();
        }
    }

    public List<LabData> loadDataFromFile(String fileName) {
        List<LabData> result = new ArrayList<>();
        File file = new File(fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null){
                String[] data = st.split(CSV_SEPARATOR);
                int sensorId = Integer.parseInt(data[0]);
                Date time =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data[1]);
                if(data[2].equals("true") || data[2].equals("false")){
                    result.add(new BinaryStatus(sensorId, time, Boolean.valueOf(data[2])));
                } else {
                    result.add(new MeasuredData(sensorId, time, Float.parseFloat(data[2])));
                }
            }
        } catch (Exception e){
            System.out.println("DATA LOGGING: Error loading data from file!");
            e.printStackTrace();
        }
        return result;
    }

    public List<LabData> loadDataSensorTimePeriod(int sensorId, Date fromTime, Date toTime) {
        List<LabData> lines = loadDataFromFile(DATA_PATH +SENSORS.get(sensorId)+".csv");
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
        File file = new File(fileName);
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