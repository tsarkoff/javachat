import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.String.format;

public class JavaChatServer {
    static ServerSocket serverSocket;
    private static final Map<String, ObjectOutputStream> clients = new HashMap<>();
    private static boolean run = true;

    public void listen() {
        try (ServerSocket sc = new ServerSocket(Config.PORT)) {
            serverSocket = sc;
            Logger.getLogger(Paths.get("").toAbsolutePath() + Logger.SERVER_LOG);
            Logger.getLogger().println(Config.SERVER_NAME, "started on port: " + sc.getLocalPort());
            while (run) {
                Socket cs = sc.accept();
                Logger.getLogger().println(Config.SERVER_NAME, "new anonymous client connected on port: " + cs.getPort());
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
                            Logger.getLogger().lnprint(Config.SERVER_NAME, e.getMessage() == null ? "Client thread stops" : e.getMessage()); // e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            Logger.getLogger().println(Config.SERVER_NAME, "JavaChatServer stops (!)");
        }
    }

    public static void handshake(Message msg, ObjectOutputStream oos) throws IOException {
        String nick = clients.containsKey(msg.sender) ? msg.sender + "_" + Math.abs(new Random().nextInt()) : msg.sender;
        clients.put(nick, oos);
        Logger.getLogger().println(Config.SERVER_NAME, format("new client accepted, requested nick: '%s', assigned nick: %s", msg.sender, nick)).log(msg);
        oos.writeObject(new Message(Config.SERVER_NAME, nick));
        oos.flush();
    }

    public static synchronized void sendToAll(Message msg, ObjectOutputStream oos) throws IOException {
        Logger.getLogger().println(Config.SERVER_NAME, format("message posted - sender: %s, text: %s", msg.sender, msg.text)).log(msg);
        for (String recipient : clients.keySet()) {
            ObjectOutputStream out = clients.get(recipient);
            if (!out.equals(oos)) {
                out.writeObject(msg);
                out.flush();
                Logger.getLogger().println(Config.SERVER_NAME, format("message transferred - sender: %s, recipient: %s, text: %s", msg.sender, recipient, msg.text)).log(msg);
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
        Logger.getLogger().println(Config.SERVER_NAME, format("client disconnected: %s", releasedClient));
    }

    public static void stop() throws IOException {
        run = false;
        serverSocket.close();
    }
}