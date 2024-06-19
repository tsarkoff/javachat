import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaChatClient {
    public static boolean withoutScanner = false;
    private static Socket cs;
    public Thread inputAndSend = null;
    public Thread receiveAndPrint = null;
    public ObjectOutputStream oos = null;
    private final Scanner scanner = new Scanner(System.in);
    public String name = "";
    private static final AtomicBoolean run = new AtomicBoolean(true);

    JavaChatClient() {
    }

    JavaChatClient(boolean withoutScanner) {
        JavaChatClient.withoutScanner = withoutScanner;
    }

    public void connect() throws IOException {
        Logger.getLogger(Logger.CLIENT_LOG);
        cs = new Socket(Config.ADDRESS, Config.PORT);
        oos = new ObjectOutputStream(new BufferedOutputStream(cs.getOutputStream()));
        oos.flush();
        inputAndSend = new Thread(() -> {
            Logger.getLogger().print(Config.BOT, "Welcome to JavaChat. Please enter Config.YOUr nick name (or anytime '/exit' to stop): ");
            while (true) {
                try {
                    if (!withoutScanner && scanner.hasNext()) {
                        String text = scanner.nextLine();
                        tryHandshake(text);
                        sendMessage(new Message(name, text));
                        if (!tryExit(text))
                            Logger.getLogger().print(name + Config.YOU, "");
                    } else
                        break;
                } catch (Exception e) { // Scanner closed()
                    Logger.getLogger().print(Config.BOT, "");
                    System.out.println(e.getMessage());
                    break;
                }
            }
            Logger.getLogger().println(Config.BOT, "Client @" + name + " thread 'Input-And-Send' stops");
        });
        inputAndSend.start();

        receiveAndPrint = new Thread(() -> {
            try {
                var ois = new ObjectInputStream(new BufferedInputStream(cs.getInputStream()));
                while (run.get()) {
                    Message msg = (Message) ois.readObject();
                    if (msg.sender.equals(Config.SERVER_NAME)) { // handshake
                        name = msg.text;
                        Logger.getLogger().println(Config.BOT, "assigned your nick name: " + msg.text).log(msg);
                        Logger.getLogger().print(name + Config.YOU, "");
                        continue;
                    }
                    Logger.getLogger().lnprint(msg.sender, msg.text).log(msg);
                    Logger.getLogger().print(name + Config.YOU, "");
                }
            } catch (IOException | ClassNotFoundException e) {
                Logger.getLogger().print(Config.BOT, "");
                System.out.println(e.getMessage());
            }
            Logger.getLogger().println(Config.BOT, "Client @" + name + " thread 'Receive-And-Print' stops");
        });
        receiveAndPrint.start();
    }

    public void tryHandshake(String nick) {
        if (name.isEmpty()) { // needs Handshake to accept nick
            name = nick;
            Logger.getLogger().println(Config.BOT, "handshake '" + name + "' has sent to @server");
            sendMessage(new Message(name, null)); // handshake detected by NULL msg.text
        }
    }

    public void sendMessage(Message msg) {
        try {
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException e) {
            Logger.getLogger().print(Config.BOT, "");
            System.out.println(e.getMessage());
        }
    }

    public boolean tryExit(String text) {
        if (text.equalsIgnoreCase(Config.EXIT)) {
            Logger.getLogger().println(Config.BOT, "JavaChatClient @" + name + " stops");
            try {
                oos.close();
                scanner.close();
                cs.close();
                run.set(false);
                return true;
            } catch (IOException e) {
                Logger.getLogger().print(Config.BOT, "");
                System.out.println(e.getMessage());
            }
        }
        return false;
    }
}
