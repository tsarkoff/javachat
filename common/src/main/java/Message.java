import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;

import static java.lang.String.format;

@ToString
public class Message implements Serializable {
    public String sender;
    public String text;
    public String timeStamp;
    public String id;

    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
        this.timeStamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        id = format("%-11d", Math.abs(new Random().nextInt()));
    }
}
