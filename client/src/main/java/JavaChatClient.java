import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class JavaChatClient {
    Socket cs;
    private String sender = "";
//    ObjectOutputStream oos;
//    ObjectInputStream ois;

    // Коннект к серверу и отправка Handshake сообщения
    public void connect() {
        try (Socket socket = new Socket("localhost", 9999)) {
            cs = socket;
            var in = new BufferedInputStream(cs.getInputStream());
            var out = new BufferedOutputStream(cs.getOutputStream());
            Logger.print("bot", "connected to JavaChatServer Socket: " + cs + "\n");
            // ALERT! Using "try-with-resources()" for out/in Objects below = does auto-close Client Socket
            // Console Scan and Send Message - Thread
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                Logger.print("bot", "Welcome to JavaChat. Please enter your nick name (or anytime /exit to stop): ");
                while (true) {
                    String text = scanner.nextLine();
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(out);
                        if (text.equals("/exit"))
                            exit();
                        if (text.equals("\n"))
                            continue;
                        if (sender.isEmpty()) { // needs Handshake to accept nick name
                            sender = text;
                            text = null;
                            Logger.print("bot", "handshake '" + sender + "' has sent to @server\n");
                        } else {
                            Logger.print(sender, "you are sending a text... : " + text + "\n");
                        }
                        oos.writeObject(new Message(sender, text));
                        oos.flush();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }).start();
            // Receive and Console Print Message - Thread
            ObjectInputStream ois = new ObjectInputStream(in);
            new Thread(() -> {
                while (true) {
                    Message msg;
                    try {
                        msg = (Message) ois.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                        Logger.print(sender, "");
                        continue;
                    }
                    if (msg.sender.equals(JavaChatServer.SERVER_NAME)) { // handshake
                        sender = msg.message;
                        Logger.print("bot", "assigned your nick name: " + msg.message + "\n");
                        Logger.print(sender, "");
                        continue;
                    }
                    Logger.print(msg.sender, msg.message);
                }
            }).start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void exit() throws IOException {
        cs.close();
        Logger.print("bot", "JavaChatClient stopped\n");
    }
}
