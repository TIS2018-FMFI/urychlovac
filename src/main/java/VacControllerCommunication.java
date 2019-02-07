import com.fazecast.jSerialComm.SerialPort;

import java.util.Date;

/**
 * Vacuum controller master class - used to communicate with Vacuum controller, process and distribute its messages
 *
 * @author FMPH
 */
public class VacControllerCommunication extends Thread {
    private static final int VACUUM_ID = 0;
    SerialPort comPort;
    byte[] buffer;



    public VacControllerCommunication() {
        // konfiguruj port
        comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();



    }

    @Override
    public void run() {
        final String address = "000";
        final String dataRequestAction = "00";
        final String parameterNumber = "000";     // TODO!
        final String dataRequestLengthAndData = "02=?";
        final String firstPart = address + dataRequestAction + parameterNumber + dataRequestLengthAndData;
        String checksum = calculateChecksum(firstPart + );
        final String cr = "13";


        final byte[] writeBuffer = new byte[16];
        String string4Buffer = address +
        writeBuffer = ...

//        V cykle:
//          1. posli REQUEST
//          2. cakaj kym pride (cez listener? )


        // reading
        try {
            while (true)
            {
                // transmitting (data request):

                comPort.writeBytes(writeBuffer, writeBuffer.length);



                // waiting for request:
                while (comPort.bytesAvailable() == 0)
                    Thread.sleep(20);

                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(readBuffer, readBuffer.length); // writes into buffer;
                // numRead is number of bytes successfully read, or -1 if there was an error erading from the port

//                System.out.println("Read " + data + " bytes.");
                if (numRead != -1){
//                    LabData receivedData = dataProcessor.processData(received);
                    MeasuredData mData = new MeasuredData(VACUUM_ID, new Date(), getDataFromBuffer(readBuffer));
                    DataManager.getInstance().addData(mData);

                }

            }
        } catch (Exception e) { e.printStackTrace(); }
        comPort.closePort();
    }

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
        return 9.999999999f;    //TODO

    }

}
