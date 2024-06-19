import org.junit.jupiter.api.*;

import java.io.IOException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JavaChatServerTest {
    private static JavaChatServer server = null;
    private static Thread serverThread = null;

    @BeforeAll
    public static void init() {
        server = new JavaChatServer();
        serverThread = new Thread(server::listen);
        serverThread.start();
    }

    @Test
    public void testStop() throws InterruptedException, IOException {
        server.stop();
        serverThread.join();
        Assertions.assertFalse(serverThread.isAlive());
    }

    @Test
    void handshake() {
        // cannot test due to cycled dependency for JavaClientChat in pon.xml
        // Expert suggestion is welcome how to solve!
        Assertions.assertTrue(true);
    }

    @Test
    void sendToAll() {
        // cannot test due to cycled dependency for JavaClientChat in pon.xml
        // Expert suggestion is welcome how to solve!
        Assertions.assertTrue(true);
    }
}