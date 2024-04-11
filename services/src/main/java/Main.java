public class Main {
    public static void main(String[] args) {
        Protocol controlProtocol = new ControlProtocol();
        System.out.println(controlProtocol.features);
        Protocol chatProtocol = new ChatProtocol();
        System.out.println(chatProtocol.features);

        if (controlProtocol.features.stream().anyMatch(f -> f == ControlProtoFeatures.CHECK_BANDWIDTH)) {
            System.out.println("BANDWIDTH");
        }

        for (IProtoFeature cpf : controlProtocol.features) {
            switch (cpf) {
                case ControlProtoFeatures.CHECK_BANDWIDTH -> System.out.println("BANDWIDTH");
                case ControlProtoFeatures.CHECK_VERSIONS -> System.out.println("VERSIONS");
                case ControlProtoFeatures.SHUTDOWN_SERVER -> System.out.println("SERVER SHUTDOWN");
                default -> System.out.println("DEFAULT");
            }
        }
    }
}
