import java.util.Date;

/**
 * Data processor class - takes RAW data from Arduino communication, parses them and creates appropriate objects
 *
 * @author FMPH
 */

public class DataProcessor {
    //header format

    // I[0]A[1]S[2]T[3]V[4] if [1] == 0
    // example I0A0I01T1V1.33
    //[0] Arduino ID
    //      0 = data arduino
    //      1-9 = notification arduinos
    //[1] Arduino type
    //      0 = measurment/statuses
    //      1 = notification
    //[2] Sensor ID
    //      string of len 2 - for id map see DataManager.SENSORS
    //[3] Type of value
    //      0 = Boolean value
    //      1 = Float
    //[4] Value
    //      0/1 if [2] == 0
    //      "xxx.yyy" if [2] == 1

    // I[0]A[1]S[2] if [1] == 1
    //[0] Arduino ID
    //      0 = data arduino
    //      1-9 = notification arduinos
    //[1] Arduino type
    //      0 = measurment/statuses
    //      1 = notification
    //[2] Status of notification arduino
    //      0 = OK
    //      1 = NOT OK

    public LabData processData(String input) {
        if (input.charAt(1) == '0') {
            int sensorId = (input.charAt(5) - '0') * 10 + input.charAt(5) - '0';

            if (input.charAt(8) == '0') {//boolean status
                if (input.charAt(10) == '0') {//value == false
                    return new BinaryStatus(sensorId, new Date(), false);
                } else if (input.charAt(10) == '1') {
                    return new BinaryStatus(sensorId, new Date(), true);
                }
            } else if (input.charAt(8) == '1') {//float status
                int i = 10;
                float value = 0;
                boolean separator = false;
                int digit = 10;

                while (true) {
                    if (i >= input.length()) {
                        break;
                    }

                    if (input.charAt(i) == '.') {
                        separator = true;
                    } else if (!separator) {
                        value = value * 10 + input.charAt(i) - '0';
                    } else {
                        value += (input.charAt(i) - '0')/digit;
                        digit *= 10;
                    }

                    i++;
                }

                return new MeasuredData(sensorId, new Date(), value);
            }
        } else if (input.charAt(3) == '1') {
            if (input.charAt(5) == '1') {
                //TODO send notification
                System.out.println("Arduino with ID " + (input.charAt(1) - '0') + "has an issue");
            }
        }

        return null;
    }
}
