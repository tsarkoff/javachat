import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.String.format;

public final class JavaChatServer {
    public static final String SERVER_NAME = "server";
    static ServerSocket serverSocket;
    private static final Map<String, ObjectOutputStream> clients = new HashMap<>();
    private static Logger logger = null;
    private static boolean run = true;

    public static void main(String[] args) {
        try (ServerSocket sc = new ServerSocket(Config.PORT)) {
            serverSocket = sc;
            logger = new Logger(Paths.get("").toAbsolutePath() + Logger.SERVER_LOG);
            logger.print(SERVER_NAME, "started on port: " + sc.getLocalPort() + "\n");
            while (run) {
                Socket cs = sc.accept();
                logger.print(SERVER_NAME, "new anonymous client connected on port: " + cs.getPort() + "\n");
                new Thread(() -> {
                    Message msg = null;
                    ObjectOutputStream oos = null;
                    try {
                        oos = new ObjectOutputStream(new BufferedOutputStream(cs.getOutputStream()));
                        oos.flush();
                        var ois = new ObjectInputStream(new BufferedInputStream(cs.getInputStream()));
                        while (true) {
                            msg = (Message) ois.readObject();
                            if (msg.text == null) { // handshake
                                handshake(msg, oos);
                                continue;
                            } else if (msg.text.equals(Config.EXIT))
                                throw new IOException(msg.sender);
                            else if (msg.text.equals(Config.STOP))
                                stop();
                            sendToAll(msg, oos);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        if (e.getClass().equals(IOException.class) && msg != null && msg.sender.equals(e.getMessage()))
                            releaseClient(oos);
                        else
                            System.out.println(e.getMessage() == null ? "\nClient thread stops\n" : e.getMessage()); // e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            logger.println(SERVER_NAME, "JavaChatServer stops (!)" + "\n");
        }
    }

    public static void handshake(Message msg, ObjectOutputStream oos) throws IOException {
        String nick = clients.containsKey(msg.sender) ? msg.sender + "_" + Math.abs(new Random().nextInt()) : msg.sender;
        clients.put(nick, oos);
        logger.print(SERVER_NAME, format("new client accepted, requested nick: '%s', assigned nick: %s\n", msg.sender, nick));
        oos.writeObject(new Message(SERVER_NAME, nick));
        oos.flush();
    }

    public static synchronized void sendToAll(Message msg, ObjectOutputStream oos) throws IOException {
        logger.print(SERVER_NAME, format("message posted - sender: %s, text: %s", msg.sender, msg.text));
        for (String recipient : clients.keySet()) {
            ObjectOutputStream out = clients.get(recipient);
            if (!out.equals(oos)) {
                out.writeObject(msg);
                out.flush();
                logger.print(SERVER_NAME, format("message transferred - sender: %s, recipient: %s, text: %s", msg.sender, recipient, msg.text));
            }
        }
    }

    public static synchronized void releaseClient(ObjectOutputStream oos) {
        String releasedClient = "unknown";
        for (String client : clients.keySet())
            if (clients.get(client).equals(oos)) {
                clients.remove(client);
                releasedClient = client;
                break;
            }
        logger.print(SERVER_NAME, format("client disconnected: %s\n", releasedClient));
    }

    public static void stop() throws IOException {
        run = false;
        serverSocket.close();
    }
}