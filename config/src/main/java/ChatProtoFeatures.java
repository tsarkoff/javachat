public enum ChatProtoFeatures implements IProtoFeature {
    TEXT("TEXT"),
    JSON("JSON"),
    IMAGE("IMAGE"),
    FILES("FILES"),
    COMMANDS("COMMANDS");

    final String chatProtoFeature;

    ChatProtoFeatures(String chatProtoFeature) {
        this.chatProtoFeature = chatProtoFeature;
    }

    @Override
    public String feature() {
        return chatProtoFeature;
    }
}
