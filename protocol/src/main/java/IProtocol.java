import java.util.Set;

public interface IProtocol {
    long getVersion();

    String getAddress();

    boolean setAddress(String address);

    int getPort();

    boolean setPort(int port);

    Set<IProtoFeature> getProtoFeatures();
}
