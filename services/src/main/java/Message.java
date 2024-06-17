import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
public class Message implements Serializable {
    public String sender;
    public String text;
    public String timeStamp;

    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
        this.timeStamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
