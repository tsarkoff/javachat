import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class ChatProtocol extends Protocol implements Serializable {
    @Serial
    private static final long serialVersionUID = Definitions.CHAT_PROTO_VER_DEFAULT;
    //private static final long serialVersionUID = Definitions.CHAT_PROTO_VER_NEW; // будущая проверка несовместимости версий

    ChatProtocol() {
        super.features = Set.of(
                ChatProtoFeatures.values()
                //ChatProtoFeatures.TEXT
        );
    }

    @Override
    public long getVersion() {
        return serialVersionUID;
    }
}
