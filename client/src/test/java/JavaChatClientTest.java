import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JavaChatClientTest {
    private static JavaChatServer server = null;
    private static JavaChatClient client1 = null;
    private static JavaChatClient client2 = null;

    @BeforeAll
    public static void init() throws IOException {
        server = new JavaChatServer();
        var serverThread = new Thread(server::listen);
        serverThread.start();
        client1 = new JavaChatClient(true); // interactive Console scanner is absent for @Test
        client1.connect();
        client2 = new JavaChatClient(true); // interactive Console scanner is absent for @Test
        client2.connect();
    }

    @Test
    @Order(1)
    public void testTryHandshake() {
        String nickToRegister = "alex";
        JavaChatClientTest.client1.tryHandshake(nickToRegister);
        Assertions.assertEquals(nickToRegister, client1.name);
        nickToRegister = "max";
        JavaChatClientTest.client2.tryHandshake(nickToRegister);
        Assertions.assertEquals(nickToRegister, client2.name);
    }

    @Test
    @Order(2)
    public void testSendMessage() throws IOException {
        String testMessage = "Test message: 'JavaChatClientTest.sendMessage()'";
        Message msg = new Message(client1.name, testMessage);
        String id = msg.id;
        System.out.println("\n\t JUnit SENDS = " + testMessage + " : ID = " + id);
        client1.sendMessage(msg);
        File log = new File(Logger.SERVER_LOG); // looking for Message ID sent from Client
        boolean idInServerFound = false;
        try (BufferedReader br = new BufferedReader(new FileReader(log))) {
            for (String line; (line = br.readLine()) != null; ) {
                if (line.contains(id)) {
                    idInServerFound = true;
                    System.out.println("FOUND in LOG = " + line);
                    break;
                }
            }
        }
        Assertions.assertTrue(idInServerFound);
    }

    @Test
    @Order(3)
    public void testTryExit() throws InterruptedException, IOException {
        if (client1.tryExit(Config.EXIT) && client2.tryExit(Config.EXIT)) {
            client1.inputAndSend.join();
            client1.receiveAndPrint.join();
            client2.inputAndSend.join();
            client2.receiveAndPrint.join();
            Assertions.assertFalse(
                    client1.inputAndSend.isAlive()
                            && client1.receiveAndPrint.isAlive()
                            && client2.inputAndSend.isAlive()
                            && client2.receiveAndPrint.isAlive()
            );
            server.stop();
        } else {
            Assertions.fail("client.tryExit(EXIT) failed");
        }
    }
}