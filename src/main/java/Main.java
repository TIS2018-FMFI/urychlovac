
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main class - used to initiate and manage all necessary components of the system.
 *
 * @author FMPH
 */


public class Main {
    private static ArduinoCommunication arduinoData1;
    private static ArduinoCommunication arduinoData2;
    private static ArduinoCommunication arduinoData3;
    private static ArduinoCommunication arduinoNotif1;
    private static ArduinoCommunication arduinoNotif2;

    private static FrontEndSlave frontEnd;
    private static NotificationManager notificationManager = new NotificationManager();
    private static Configuration config;
    private static VacControllerCommunication vacControllerCommunication;

    public Main() {
        System.out.println("\"################# GHKP URYCHLOVAC #################");
        config = new Configuration("config.toml");
        DataManager DM = DataManager.getInstance();
        DM.initFiles();


        arduinoData1 = new ArduinoCommunication("147.213.232.140",0);
        arduinoData1.start();

        arduinoData2 = new ArduinoCommunication("147.213.232.141",1);
        arduinoData2.start();

        arduinoData3 = new ArduinoCommunication("147.213.232.142",2);
        arduinoData3.start();

        arduinoNotif1 = new ArduinoCommunication("147.213.232.143",3);
        arduinoNotif1.start();

        arduinoNotif2 = new ArduinoCommunication("147.213.232.144",4);
        arduinoNotif2.start();


        vacControllerCommunication = new VacControllerCommunication();
    }

    public static void main(String[] args) {
        new Main();
    }

    public static Configuration getConfig() {
        return config;
    }

    public static NotificationManager getNotificationManager(){return notificationManager;}

    public static ArduinoCommunication getArduinoData1() {
        return arduinoData1;
    }

    public static ArduinoCommunication getArduinoData2() {
        return arduinoData2;
    }

    public static ArduinoCommunication getArduinoData3() {
        return arduinoData3;
    }

    public static ArduinoCommunication getArduinoNotif1() {
        return arduinoNotif1;
    }

    public static ArduinoCommunication getArduinoNotif2() {
        return arduinoNotif2;
    }

    public void testData(){
        DataManager moj = DataManager.getInstance();
        //moj.initFiles();
        moj.addData(new BinaryStatus(0, new Date(), true));//vypise
        moj.addData(new BinaryStatus(0, new Date(), true));//nevypise
        moj.addData(new BinaryStatus(0, new Date(), false));//nevypise
        moj.addData(new BinaryStatus(0, new Date(new Date().getTime()+2000), true));//vypise
        moj.addData(new BinaryStatus(2, new Date(), false));
        moj.addData(new BinaryStatus(4, new Date(), false));
        //System.out.println(moj.convertToCSV(new BinaryStatus(0, new Date(), true)));
        //System.out.println(moj.convertToCSV(new MeasuredData(0, new Date(), 1.23f)));
        //new BinaryStatus(sensorId, new Date(), true);
        //new MeasuredData(sensorId, new Date(), value);
        /*for(LabData status :moj.loadDataFromFile("DHT22_temperature.txt")){
            System.out.println(status.getId().toString()+status.getTimestamp());
        }*/
        List<LabData> datatime = moj.loadDataSensorTimePeriod(0,new Date(new Date().getTime()-600000),new Date());
        for(LabData status :datatime){
            //System.out.println(status.getId().toString()+status.getTimestamp());
        }
        for(String line : moj.getListOfLogs("DHT22_temperature.txt")){
            //System.out.println(line);
        }
    }
}
