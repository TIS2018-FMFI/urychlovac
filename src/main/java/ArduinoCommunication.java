import java.io.IOException;
import java.net.*;

/**
 * Arduino master class - used to communicate with Arduino, process and distribute its messages
 *
 * @author FMPH
 */


public class ArduinoCommunication extends Thread {
    // source of multicast receive code: https://www.baeldung.com/java-broadcast-multicast

    private int arduinoID = 0;
    private final static int MULTICAST_PORT = 7410;
    private String ipAddress;
    private DataProcessor dataProcessor;
    private MulticastSocket socket = null;
    private byte[] buf = new byte[256];
    private long lastUpdateTimestamp = System.currentTimeMillis();

    public ArduinoCommunication(String ipAddress, int arduinoID) {
        this.ipAddress = ipAddress;
        dataProcessor = new DataProcessor();

        this.arduinoID = arduinoID;
    }

    @Override
    public void run() {
        try {
            socket = new MulticastSocket(MULTICAST_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InetAddress group = null;
        try {
            group = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            socket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.setSoTimeout((int)Main.getConfig().getLoggingFrequency()*2);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (SocketTimeoutException e) {
                //TODO send notification - he dead
                System.out.println("Arduino with ID " + arduinoID + " timed out!");
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received = new String(
                    packet.getData(), 0, packet.getLength());
            if ("end".equals(received)) {
                break;
            }

            lastUpdateTimestamp = System.currentTimeMillis();

            LabData receivedData = dataProcessor.processData(received);
            if (receivedData != null) {
                DataManager.getInstance().addData(receivedData);
            }
        }

        try {
            socket.leaveGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }

        socket.close();
    }
}
