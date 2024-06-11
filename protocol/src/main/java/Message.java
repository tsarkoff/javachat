import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
public class Message implements Serializable {
    public String sender;
    public String message;
    public String timeStamp;

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timeStamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
