import java.util.Set;

public abstract class Protocol implements IProtocol {
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;
    protected String address;
    protected int port;
    protected Set<IProtoFeature> features;

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public boolean setAddress(String address) {
        if (!address.isBlank()) {
            this.address = address;
            return true;
        }
        return false;
    }

    @Override
    public boolean setPort(int port) {
        if (port > MIN_PORT && port < MAX_PORT) {
            this.port = port;
            return true;
        }
        return false;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public Set<IProtoFeature> getProtoFeatures() {
        return features;
    }
}
