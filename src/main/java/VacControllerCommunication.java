import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;

import java.util.Date;

/**
 * Vacuum controller master class - used to communicate with Vacuum controller, process and distribute its messages
 *
 * @author FMPH
 */
public class VacControllerCommunication  {
    private static final int VACUUM_ID = 0;
    private static final int BUFFER_SIZE = 20; // Ocakavam packet dlzky 20
    SerialPort comPort;
    byte[] buffer;


////////////////// NESTED CLASS //////////////////
    // source: https://github.com/Fazecast/jSerialComm/wiki/Event-Based-Reading-Usage-Example

    /**
     * PacketListener is waiting for packets incoming to serial port with packet size 20.
     */
    private final class PacketListener implements SerialPortPacketListener
    {
        @Override
        public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }

        @Override
        public int getPacketSize() { return BUFFER_SIZE; }

        @Override
        public void serialEvent(SerialPortEvent event)
        {
            buffer = event.getReceivedData();
            System.out.println("\nVacuumController: Received data of size: " + buffer.length);

            // Testovaci vypis:
            System.out.println("Vacuum Controller: DATA:");
            for (int i = 0; i < buffer.length; ++i)
                System.out.print((char)buffer[i]);
            System.out.println("\n");

            // TODO pridat podmienky - ak su chybove data !


            if (buffer[buffer.length -1] != 13){
                System.out.println("VacuumController: ERROR! CR does not fit! Packet size does not fit!");
            }

            // Ulozi data:
            MeasuredData mData = new MeasuredData(VACUUM_ID, new Date(), getDataFromBuffer(buffer));
            DataManager.getInstance().addData(mData);

        }
    }
////////////////// end of NESTED CLASS //////////////////


    // CONSTRUCTOR //
    public VacControllerCommunication() {
        SerialPort[] ports = SerialPort.getCommPorts();

        System.out.println("\n\n########## VacuumController Test: ##########");
        if (ports.length < 1) {
            System.out.println("VacuumController: ERROR: no serial ports! Please enter portDescriptor as argument to getComports().");
        } else if (ports.length == 1){
            System.out.println("VacuumController: OK we have 1 port available.");
            System.out.println("port description: " + ports[0].getPortDescription());
        } else if (ports.length > 1) {
            System.out.println("VacuumController: WARNING we have more than one serial ports! Please enter portDescriptor as argument to getComports().");
        }
        System.out.println("########## end of VacuumController Test: ##########\n");


        if (ports.length >= 1){
            // konfiguruj port
            comPort = ports[0];
            comPort.openPort();

            PacketListener listener = new PacketListener();
            comPort.addDataListener(listener);
        }


//        comPort.removeDataListener(); // cannot do now
//        comPort.closePort(); // We can not close port now.
    }


// momentalne netreba
    private String calculateChecksum(String string) {
        byte[] bytes = string.getBytes();
        int sum = 0;
        for (int i = 0; i < bytes.length; i++) {
            sum += bytes[i];
        }
        Integer result =  sum % 256;
        return result.toString();
    }

    float getDataFromBuffer(byte[] buffer){
        String bufferStr = new String(buffer);
        int dataLength = Integer.parseInt(bufferStr.substring(8, 10));

        float data = Float.parseFloat(bufferStr.substring(10, 10 + dataLength));
        return data / 100;
    }




}
