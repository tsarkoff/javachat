import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static java.lang.String.format;

public class JavaChatServer {
    static ServerSocket serverSocket;
    private static final Map<String, ObjectOutputStream> clients = new HashMap<>();
    private static boolean run = true;

    public void listen() {
        try (ServerSocket sc = new ServerSocket(Config.PORT)) {
            serverSocket = sc;
            Logger.getLogger(Logger.SERVER_LOG); // init with log file name
            Logger.getLogger().println(Config.SERVER_NAME, "started on port: " + sc.getLocalPort());
            while (run) {
                Socket cs = sc.accept();
                Logger.getLogger().println(Config.SERVER_NAME, "new anonymous client connected on port: " + cs.getPort());
                new Thread(() -> {
                    Message msg = new Message("unknown", null);
                    ObjectOutputStream oos;
                    try {
                        oos = new ObjectOutputStream(new BufferedOutputStream(cs.getOutputStream()));
                        oos.flush();
                        var ois = new ObjectInputStream(new BufferedInputStream(cs.getInputStream()));
                        while (run) {
                            msg = (Message) ois.readObject();
                            if (msg.text == null) { // handshake
                                handshake(msg, oos);
                                continue;
                            }
                            sendToAll(msg, oos);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        Logger.getLogger().lnprint(Config.SERVER_NAME, e.getMessage() == null ? "Client @" + msg.sender + " thread stops" : e.getMessage()); // e.printStackTrace();
                        clients.remove(msg.sender);
                    }
                }).start();
            }
        } catch (IOException e) {
            Logger.getLogger().println(Config.SERVER_NAME, "JavaChatServer stops (!)");
        }
    }

    public void handshake(Message msg, ObjectOutputStream oos) throws IOException {
        String nick = clients.containsKey(msg.sender) ? msg.sender + "_" + Math.abs(new Random().nextInt()) : msg.sender;
        clients.put(nick, oos);
        Logger.getLogger().println(Config.SERVER_NAME, format("new client accepted, requested nick: '%s', assigned nick: %s", msg.sender, nick)).log(msg);
        oos.writeObject(new Message(Config.SERVER_NAME, nick));
        oos.flush();
    }

    public synchronized void sendToAll(Message msg, ObjectOutputStream oos) throws IOException {
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

    public void stop() throws IOException {
        run = false;
        serverSocket.close();
    }
}