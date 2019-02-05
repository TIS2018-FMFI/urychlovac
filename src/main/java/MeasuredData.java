import java.util.Date;

/**
 * Measured data class - used to get, process, store and log measured data (e.g. vacuum, temperature)
 *
 * @author FMPH
 */

public class MeasuredData extends LabData {
    private float value;

    public MeasuredData(Integer id, Date timestamp, float value){
        super(id, timestamp);
        setValue(value);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
