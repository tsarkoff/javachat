import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaChatClient {
    public static final String BOT = "bot";
    public static final String YOU = "(you)";
    public static Thread inputAndSend = null;
    public static Thread receiveAndPrint = null;
    private static String name = "";
    private static final AtomicBoolean run = new AtomicBoolean(true);

    public void connect() throws IOException, InterruptedException {
        Socket cs = new Socket(Config.ADDRESS, Config.PORT);
        Logger.getLogger(Paths.get("").toAbsolutePath() + Logger.CLIENT_LOG);
        var oos = new ObjectOutputStream(new BufferedOutputStream(cs.getOutputStream()));
        oos.flush();
        inputAndSend = new Thread(() -> {
            var scanner = new Scanner(System.in);
            Logger.getLogger().print(BOT, "Welcome to JavaChat. Please enter your nick name (or anytime '/exit' to stop): ");
            while (true) {
                String text = scanner.nextLine();
                try {
                    if (name.isEmpty()) { // needs Handshake to accept nick
                        name = text;
                        text = null;
                        Logger.getLogger().println(BOT, "handshake '" + name + "' has sent to @server");
                    }

                    Message msg = new Message(name, text);
                    //Logger.getLogger().println(BOT, "you are sending a message... : " + text).log(msg);
                    Logger.getLogger().print(name + YOU, "");

                    oos.writeObject(msg);
                    oos.flush();

                    if (text != null && (text.equalsIgnoreCase(Config.EXIT) || text.equalsIgnoreCase(Config.STOP))) {
                        Logger.getLogger().println(BOT, "JavaChatClient stops");
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
        });
        inputAndSend.start();

        receiveAndPrint = new Thread(() -> {
            try {
                var ois = new ObjectInputStream(new BufferedInputStream(cs.getInputStream()));
                while (run.get()) {
                    Message msg = (Message) ois.readObject();
                    if (msg.sender.equals(Config.SERVER_NAME)) { // handshake
                        name = msg.text;
                        Logger.getLogger().println(BOT, "assigned your nick name: " + msg.text).log(msg);
                        Logger.getLogger().print(name + YOU, "");
                        continue;
                    }
                    Logger.getLogger().lnprint(msg.sender, msg.text).log(msg);
                    Logger.getLogger().print(name + YOU, "");
                }
                ois.close();
                cs.close();
            } catch (IOException | ClassNotFoundException e) {
                Logger.getLogger().print(BOT, "");
                System.out.println(e.getMessage());
            }
        });
        receiveAndPrint.start();

        inputAndSend.join();
        receiveAndPrint.join();
    }
}
