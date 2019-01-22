import java.util.Date;

/**
 * Lab data class - used to define things that Measured data and Binary statuses have in common
 *
 * @author FMPH
 */

public class LabData {
    private Integer id;
    private Date timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
