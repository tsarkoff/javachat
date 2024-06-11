import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        runAmazingJavaChatServer(); // бросил делать "крутой" чат и сделал минимум, как по заданию (надо еще жить и работать)
        JavaChatServer javaChatServer= new JavaChatServer();
        javaChatServer.listen(9999);
        // javaChatServer.exit();
    }

    // Были планы сделать крутой чат-сервис ))
    private static void runAmazingJavaChatServer() {
        System.out.println("Big plans suspended...");
        Protocol controlProtocol = new ControlProtocol();
        System.out.println("controlProtocol.features = " + controlProtocol.features);
        Protocol chatProtocol = new ChatProtocol();
        System.out.println("chatProtocol.features" + chatProtocol.features);
    }
}
