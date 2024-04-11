import java.awt.*;
import java.time.LocalDateTime;

public interface IMessage {
    boolean setMessageSender(User sender);

    User getMessageSender();

    boolean setMessageRecipient(User recipient);

    User getMessageRecipient();

    boolean setMessageDateTime(LocalDateTime dt);

    LocalDateTime getMessageDateTime();

    boolean setMessageText(String text);

    String getMessageText();

    boolean setMessageFile(byte[] data);

    byte[] getMessageFile();

    boolean setMessageJson(String json);

    String getMessageJson();

    boolean setMessageImage(Image img);

    Image getMessageImage();
}