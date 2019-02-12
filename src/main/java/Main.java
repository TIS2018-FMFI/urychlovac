
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main class - used to initiate and manage all necessary components of the system.
 *
 * @author FMPH
 */


public class Main {
    private static ArduinoCommunication arduinoCommunication;
    private static FrontEndSlave frontEnd;
    private static NotificationManager notificationManager = new NotificationManager();
    private static Configuration config;
    private static VacControllerCommunication vacControllerCommunication;

    public Main() {
        System.out.println("\"################# GHKP URYCHLOVAC #################");
        config = new Configuration();
        DataManager DM = DataManager.getInstance();
        DM.initFiles();

        arduinoCommunication = new ArduinoCommunication();
        arduinoCommunication.start();

        vacControllerCommunication = new VacControllerCommunication();
    }

    public static void main(String[] args) {
        new Main();
    }

    public static Configuration getConfig() {
        return config;
    }

    public static NotificationManager getNotificationManager(){return notificationManager;}

    public static ArduinoCommunication getArduinoCommunication() {
        return arduinoCommunication;
    }

    public void testData(){
        DataManager moj = DataManager.getInstance();
        //moj.initFiles();
        moj.addData(new BinaryStatus(4, new Date(new Date().getTime()+1000000000), false));//vypise
//        moj.addData(new BinaryStatus(0, new Date(), true));//nevypise
//        moj.addData(new BinaryStatus(0, new Date(), false));//nevypise
//        moj.addData(new BinaryStatus(0, new Date(new Date().getTime()+100000), true));//vypise
//        moj.addData(new BinaryStatus(2, new Date(), false));
//        moj.addData(new BinaryStatus(4, new Date(), false));
        //System.out.println(moj.convertToCSV(new BinaryStatus(0, new Date(), true)));
        //System.out.println(moj.convertToCSV(new MeasuredData(0, new Date(), 1.23f)));
        //new BinaryStatus(sensorId, new Date(), true);
        //new MeasuredData(sensorId, new Date(), value);
        /*for(LabData status :moj.loadDataFromFile("DHT22_temperature.txt")){
            System.out.println(status.getId().toString()+status.getTimestamp());
        }*/
       // try{
       // moj.removeFirstLine("DHT22_temperature.csv");}
        //catch(Exception e){
        //    e.printStackTrace();
        //}
//        List<LabData> datatime = moj.loadDataSensorTimePeriod(0,new Date(new Date().getTime()-600000),new Date());
//        for(LabData status :datatime){
//            System.out.println(status.getId().toString()+status.getTimestamp());
//        }
//        for(String line : moj.getListOfLogs("DHT22_temperature.csv")){
//            System.out.println(line);
//        }
    }
}
