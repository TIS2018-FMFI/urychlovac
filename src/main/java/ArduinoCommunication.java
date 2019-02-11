import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Arduino master class - used to communicate with Arduino, process and distribute its messages
 *
 * @author FMPH
 */


public class ArduinoCommunication extends Thread {
    // source of unicast receive code: https://www.baeldung.com/udp-in-java

    private DataProcessor dataProcessor;
    private byte[] buf = new byte[256];
    private DatagramSocket socket;
    private final static int PORT = 5000;
    private int packetReceiveTimeout;
    private int arduinoNotCommunicatingThreshold = 60000;

    private Map<Integer, Long> lastUpdates;

    public ArduinoCommunication() {
        dataProcessor = new DataProcessor();
        lastUpdates = new ConcurrentHashMap<>();

        try {
            socket = new DatagramSocket(PORT);
            packetReceiveTimeout = (int)Main.getConfig().getLoggingFrequency()*2;
            socket.setSoTimeout(packetReceiveTimeout);
        } catch (SocketException e) {
            System.out.println("ARDUINO: Initialization of receiving socket failed!");
            e.printStackTrace();
        }

        Runnable arduinoLastTimeActiveCheck = new Runnable() {
            public void run() {
                arduinosCommunicatingCheck();
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(arduinoLastTimeActiveCheck, 0,
                arduinoNotCommunicatingThreshold, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            //System.out.println("ARDUINO: Waiting for packet...");

            try {
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("ARDUINO: Received packet: \"" + received + "\" (raw form).");

                LabData receivedData = dataProcessor.processData(received);
                if (receivedData != null) {
                    lastUpdates.put(receivedData.getId(), System.currentTimeMillis());
                    DataManager.getInstance().addData(receivedData);
                }
            } catch (SocketTimeoutException e) {
                System.out.println("ARDUINO: Timed out! " +
                        "Haven't received ANY packets in " + packetReceiveTimeout + " milliseconds.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(int arduinoId, String msg) {
        buf = msg.getBytes();
        try {
            InetAddress ip = InetAddress.getByName(Main.getConfig().getArduinoIpMap().get(arduinoId));
            DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, PORT);

            socket.send(packet);
            System.out.println("ARDUINO: SMS notification request packet sent!");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean arduinosCommunicatingCheck() {
        Long currentTime = System.currentTimeMillis();
        Iterator it = lastUpdates.entrySet().iterator();
        boolean okFlag = true;
        Set<Integer> dead = new HashSet<>();

        while (it.hasNext()) {
            Map.Entry lastUpdate = (Map.Entry)it.next();
            Long lastUpdateTime = (Long) lastUpdate.getValue();
            Integer arduinoId = (Integer) lastUpdate.getKey();

            if (lastUpdateTime.compareTo(currentTime - arduinoNotCommunicatingThreshold) < 0) {
                okFlag = false;
                System.out.println("ARDUINO: Arduino ID " + arduinoId + " is not communicating!");
                Main.getNotificationManager().sendNotificationArduinoFault(arduinoId, "Arduino sa nehlasi");
                dead.add(arduinoId);
            }
        }

        // Ensures that notification is only sent once, until arduino reconnects
        for (Integer arduinoId : dead) {
            lastUpdates.put(arduinoId, Long.MAX_VALUE);
        }

        return okFlag;
    }
}
