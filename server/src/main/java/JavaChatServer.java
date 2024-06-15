import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static java.lang.String.format;

public final class JavaChatServer {
    public static final int SERVER_PORT = 8099;
    public static final String SERVER_NAME = "server";
    private static final Map<String, ObjectOutputStream> clients = new HashMap<>();

    public static void main(String[] args) throws IOException {
        try (ServerSocket sc = new ServerSocket(SERVER_PORT)) {
            Logger.print(SERVER_NAME, "started on port: " + sc.getLocalPort() + "\n");
            while (true) {
                Socket cs = sc.accept();
                Logger.print(SERVER_NAME, "new anonymous client connected on port: " + cs.getPort() + "\n");
                new Thread(() -> {
                    try {
                        var oos = new ObjectOutputStream(new BufferedOutputStream(cs.getOutputStream()));
                        oos.flush();
                        var ois = new ObjectInputStream(new BufferedInputStream(cs.getInputStream()));
                        while (true) {
                            Message msg = (Message) ois.readObject();
                            if (msg.text == null) { // handshake
                                String nick = clients.containsKey(msg.sender) ? msg.sender + "_" + Math.abs(new Random().nextInt()) : msg.sender;
                                clients.put(nick, oos);
                                Logger.print(SERVER_NAME, format("new client accepted, requested nick: '%s', assigned nick: %s\n", msg.sender, nick));
                                oos.writeObject(new Message(SERVER_NAME, nick));
                                oos.flush();
                                continue;
                            }
                            for (ObjectOutputStream out : clients.values()) {
                                Logger.print(SERVER_NAME, format("new message received - sender: %s, text: %s\n", msg.sender, msg.text));
                                if (!out.equals(oos)) {
                                    out.writeObject(msg);
                                    out.flush();
                                    Logger.print(SERVER_NAME, format("received message '%s' has sent to client: %s",
                                            msg.text,
                                            clients.entrySet().stream().filter(e -> e.getValue().equals(out)).findFirst().get().getKey())
                                    );
                                }
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}