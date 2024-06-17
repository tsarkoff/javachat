import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaChatClient2 {
    public static final String BOT = "bot";
    public static final String YOU = "(you)";
    private static String name = "";
    private static Logger logger = null;
    static AtomicBoolean run = new AtomicBoolean(true);

    public static void main(String[] args) throws IOException {
        logger = new Logger(Paths.get("").toAbsolutePath() + Logger.CLIENT_LOG);
        Socket cs = new Socket(Config.ADDRESS, Config.PORT);
        var oos = new ObjectOutputStream(new BufferedOutputStream(cs.getOutputStream()));
        oos.flush();
        new Thread(() -> {
            var scanner = new Scanner(System.in);
            logger.print(BOT, "Welcome to JavaChat. Please enter your nick name (or anytime '/exit' to stop): ");
            while (true) {
                String text = scanner.nextLine();
                try {
                    if (name.isEmpty()) { // needs Handshake to accept nick name
                        name = text;
                        text = null;
                        logger.print(BOT, "handshake '" + name + "' has sent to @server\n");
                    } else if (!text.equalsIgnoreCase(Config.EXIT) && !text.equalsIgnoreCase(Config.STOP)) {
                        logger.print(BOT, "you are sending a text... : " + text + "\n");
                        logger.print(name + YOU, "");
                        text += "\n";
                    }
                    oos.writeObject(new Message(name, text));
                    oos.flush();

                    if (text != null && (text.equalsIgnoreCase(Config.EXIT) || text.equalsIgnoreCase(Config.STOP))) {
                        logger.print(BOT, "JavaChatClient stops\n");
                        oos.close();
                        scanner.close();
                        cs.close();
                        run.set(false);
                        break;
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();

        new Thread(() -> {
            try {
                var ois = new ObjectInputStream(new BufferedInputStream(cs.getInputStream()));
                while (run.get()) {
                    Message msg = (Message) ois.readObject();
                    if (msg.sender.equals(JavaChatServer.SERVER_NAME)) { // handshake
                        name = msg.text;
                        logger.print(BOT, "assigned your nick name: " + msg.text + "\n");
                        logger.print(name + YOU, "");
                        continue;
                    }
                    logger.println(msg.sender, msg.text);
                    logger.print(name + YOU, "");
                }
                ois.close();
                cs.close();
            } catch (IOException | ClassNotFoundException e) {
                logger.print(BOT, "");
                System.out.println(e.getMessage());
            }
        }).start();
    }
}
