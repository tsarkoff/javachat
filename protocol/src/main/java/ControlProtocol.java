import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class ControlProtocol extends Protocol implements Serializable {
    @Serial
    private static final long serialVersionUID = Definitions.CONTROL_PROTO_VER_DEFAULT;
    //private static final long serialVersionUID = Definitions.CONTROL_PROTO_VER_NEW; // будущая проверка несовместимости версий

    ControlProtocol() {
        super.features = Set.of(
                //ControlProtoFeatures.values()
                ControlProtoFeatures.CHECK_VERSIONS,
                ControlProtoFeatures.REGISTER_NEW_USER
        );
    }

    @Override
    public long getVersion() {
        return serialVersionUID;
    }
}
