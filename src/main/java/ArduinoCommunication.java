import java.io.IOException;
import java.net.*;

/**
 * Arduino master class - used to communicate with Arduino, process and distribute its messages
 *
 * @author FMPH
 */


public class ArduinoCommunication extends Thread {
    // source of multicast receive code: https://www.baeldung.com/java-broadcast-multicast
    // source of unicast receive code: https://www.baeldung.com/udp-in-java

    private int arduinoID = 0;

    private String ipAddress;
    private DataProcessor dataProcessor;
    private byte[] buf = new byte[256];
    private long lastUpdateTimestamp = System.currentTimeMillis();

    private DatagramSocket socket;
    private final static int PORT = 5000;
    //private MulticastSocket socket = null;

    public ArduinoCommunication(String ipAddress, int arduinoID) {
        this.ipAddress = ipAddress;
        this.arduinoID = arduinoID;
        dataProcessor = new DataProcessor();
        try {
            socket = new DatagramSocket(PORT);
            socket.setSoTimeout((int)Main.getConfig().getLoggingFrequency()*2);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            System.out.println("Waiting for packet...");

            try {
                socket.receive(packet);
            } catch (SocketTimeoutException e) {
                //TODO send notification - he dead
                System.out.println("Arduino with ID " + arduinoID + " timed out!");
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received = new String(packet.getData(), 0, packet.getLength());
            lastUpdateTimestamp = System.currentTimeMillis();
            System.out.println("Received packet: " + received);

            LabData receivedData = dataProcessor.processData(received);
            if (receivedData != null) {
                DataManager.getInstance().addData(receivedData);
            }
        }

        /*try {
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
            System.out.println("Waiting for UDP packet...");

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

            System.out.println(received);
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

        socket.close();*/
    }

    public void sendMessage(String msg) {
        buf = msg.getBytes();
        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, PORT);

            socket.send(packet);
            System.out.println("Message sent.");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
