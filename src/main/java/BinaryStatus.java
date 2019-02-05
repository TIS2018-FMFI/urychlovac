import java.util.Date;

/**
 * Binary status class - used to get, process, store and log binary statuses (e.g. door open/closed, high voltage on/off)
 *
 * @author FMPH
 */

public class BinaryStatus extends LabData {
    private boolean value;

    public BinaryStatus(Integer id, Date timestamp, boolean value){
        super(id, timestamp);
        setValue(value);
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
