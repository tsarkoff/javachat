import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.String.format;

public final class JavaChatServer {
    public static final String SERVER_NAME = "server";
    ServerSocket sc;
    private static final ConcurrentMap<String, BufferedOutputStream> clients = new ConcurrentHashMap<>();
    private static final List<Thread> clientThreads = new ArrayList<>();
    boolean isServerStopped;

    public void listen(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            sc = serverSocket;
            Logger.print(SERVER_NAME, format("started (%s:%s)\n", sc.getInetAddress(), sc.getLocalPort()));
            while (!isServerStopped) {
                Socket cs = sc.accept();
                var in = new BufferedInputStream(cs.getInputStream());
                var out = new BufferedOutputStream(cs.getOutputStream());
                Logger.print(SERVER_NAME, format("accepted new client socket (%s), streams (in: %s, out: %s)\n", cs, in, out));
                clientThreads.add(new Thread(() -> {
                    try {
                        while (true) {
                            ObjectInputStream ois = new ObjectInputStream(in);
                            Message msg = (Message) ois.readObject();
                            if (msg.message == null) { // handshake
                                ObjectOutputStream oos = new ObjectOutputStream(out);
                                if (clients.containsValue(out))
                                    msg.sender += new Random().nextInt();
                                else
                                    clients.put(msg.sender, out);
                                writeMessage(oos, new Message(SERVER_NAME, msg.sender));
                                Logger.print(SERVER_NAME, "new client added to list: " + msg.sender + "\n");
                                continue;
                            }
                            for (BufferedOutputStream bos : clients.values()) {
                                if (!bos.equals(out)) {
                                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                                    writeMessage(oos, msg);
                                }
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }));
                clientThreads.getLast().start();
            }
        }
    }

    private void writeMessage(ObjectOutputStream oos, Message msg) throws IOException {
        oos.writeObject(msg);
        oos.flush();
        //Logger.print(msg.sender, msg.message + "\n");
    }

    public void exit() throws IOException, InterruptedException {
        isServerStopped = true;
        sc.close();
        for (Thread thread : clientThreads)
            thread.join();
    }
}