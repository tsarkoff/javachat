public enum ControlProtoFeatures implements IProtoFeature {
    CHECK_VERSIONS("CHECK_VERSION"),
    REGISTER_NEW_USER("REGISTER_NEW_USER"),
    SPY_USER_INFO("SPY_USER_INFO"),
    SHUTDOWN_SERVER("SHUTDOWN_SERVER"),
    BLOCK_USER("BLOCK_USER"),
    CHECK_BANDWIDTH("CHECK_BANDWIDTH");

    final String controlProtoFeature;

    ControlProtoFeatures(String controlProtoFeature) {
        this.controlProtoFeature = controlProtoFeature;
    }

    @Override
    public String feature() {
        return controlProtoFeature;
    }
}
