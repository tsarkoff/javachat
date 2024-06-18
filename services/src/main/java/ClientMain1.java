import java.io.IOException;

public class ClientMain1 {
    public static void main(String[] args) throws IOException, InterruptedException {
        new JavaChatClient().connect();
        System.out.println("ClientMain stops");
    }
}
