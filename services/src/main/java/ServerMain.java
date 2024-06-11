import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        JavaChatServer javaChatServer= new JavaChatServer();
        javaChatServer.listen(9999);
        // javaChatServer.exit();
    }
}
