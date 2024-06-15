import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class JavaChatClient1 {
    private static final String EXIT = "'/exit'";
    private static final String BOT = "bot";
    private static final String YOU = "(you)";
    private static String name = "";

    public static void main(String[] args)
            throws IOException {
        Socket cs = new Socket("localhost", JavaChatServer.SERVER_PORT);
        var oos = new ObjectOutputStream(new BufferedOutputStream(cs.getOutputStream()));
        oos.flush();
        new Thread(() -> {
            var scanner = new Scanner(System.in);
            Logger.print(BOT, "Welcome to JavaChat. Please enter your nick name (or anytime '/exit' to stop): ");
            while (true) {
                String text = scanner.nextLine();
                try {
                    if (text.equalsIgnoreCase(EXIT)) {
                        cs.close();
                        Logger.print(BOT, "JavaChatClient stopped\n");
                        break;
                    }
                    if (text.equals("\n"))
                        continue;
                    if (name.isEmpty()) { // needs Handshake to accept nick name
                        name = text;
                        text = null;
                        Logger.print(BOT, "handshake '" + name + "' has sent to @server\n");
                    } else {
                        Logger.print(name + YOU, "you are sending a text... : " + text + "\n");
                        Logger.print(name + YOU, "");
                        text += "\n";
                    }
                    oos.writeObject(new Message(name, text));
                    oos.flush();
                } catch (IOException e) {
                    Logger.print(BOT, "");
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            try {
                var ois = new ObjectInputStream(new BufferedInputStream(cs.getInputStream()));
                while (true) {
                    Message msg = (Message) ois.readObject();
                    if (msg.sender.equals(JavaChatServer.SERVER_NAME)) { // handshake
                        name = msg.text;
                        Logger.print(BOT, "assigned your nick name: " + msg.text + "\n");
                        Logger.print(name + YOU, "");
                        continue;
                    }
                    Logger.println(msg.sender, msg.text);
                    Logger.print(name + YOU, "");
                }
            } catch (IOException | ClassNotFoundException e) {
                Logger.print(BOT, "");
                e.printStackTrace();
            }
        }).start();
    }
}
