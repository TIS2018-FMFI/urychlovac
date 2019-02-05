import java.util.Date;

/**
 * Data processor class - takes RAW data from Arduino communication, parses them and creates appropriate objects
 *
 * @author FMPH
 */

public class DataProcessor {
    //header format

    // A[0]I[1]T[2]V[3] if [0] == 0
    // example A0I01T1V1.33
    //[0] Arduino type
    //      0 = measurment/statuses
    //      1 = notification
    //[1] Sensor ID
    //      string of len 2 - for id map see DataManager.SENSORS
    //[2] Type of value
    //      0 = Boolean value
    //      1 = Float
    //[3] Value
    //      0/1 if [2] == 0
    //      "xxx.yyy" if [2] == 1

    // A[0]S[1] if [0] == 1
    //[0] Arduino type
    //      0 = measurment/statuses
    //      1 = notification
    //[1] Status of notification arduino
    //      0 = OK
    //      1 = NOT OK

    public LabData processData(String input) {
        if (input.charAt(1) == '0') {
            /*int i = 0;
            boolean saveId = false;
            int id = 0;

            while(true) {
                if (input.charAt(i) == 'T') {
                    saveId = false;
                    break;
                }

                if (saveId) {
                    id *= 10;
                    id += input.charAt(i) - '0';
                }

                if (input.charAt(i) == 'I') {
                    saveId = true;
                }

                i++;
            }*/

            int id = (input.charAt(3) - '0') * 10 + input.charAt(3) - '0';

            if (input.charAt(6) == '0') {//boolean status
                if (input.charAt(8) == '0') {//value == false
                    return new BinaryStatus(id, new Date(), false);
                } else if (input.charAt(8) == '1') {
                    return new BinaryStatus(id, new Date(), true);
                }
            } else if (input.charAt(6) == '1') {//float status
                int i = 8;
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

                return new MeasuredData(id, new Date(), value);
            }
        } else if (input.charAt(1) == '1') {

        }

        return null;
    }
}
