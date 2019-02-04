/**
 * Data processor class - takes RAW data from Arduino communication, parses them and creates appropriate objects
 *
 * @author FMPH
 */

public class DataProcessor {
    //header format

    // A[0]I[1]T[2]V[3] if [0] == 0
    //[0] Arduino type
    //      0 = measurment/statuses
    //      1 = notification
    //[1] Sensor ID
    //      see DataManager.SENSORS
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

    public LabData processData(String input) {
        return null;
    }
}
