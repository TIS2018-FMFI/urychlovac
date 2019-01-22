/**
 * Arduino master class - used to communicate with Arduino, process and distribute its messages
 *
 * @author FMPH
 */


public class ArduinoCommunication extends Thread {
    private int ipAddress;
    private DataProcessor dataProcessor;

    public ArduinoCommunication(int ipAddress) {
    }

    @Override
    public void run() {

    }

    public void sendMessage(String message) {

    }
}
