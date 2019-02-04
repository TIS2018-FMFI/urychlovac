
public class NotificationRule {
    private String text;
    private long level;
    private String rule;

    public NotificationRule(String text, long level, String rule) {
        this.text = text;
        this.level = level;
        this.rule = rule;
    }

    public void setText(String text) {
        this.text = text;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setRule(String rule) {
        this.rule = rule;
    }


    public String getRule() {
        return rule;
    }
    public String getText() {
        return text;
    }
    public long getLevel() {
        return level;
    }

}