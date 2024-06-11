import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class JavaChatServer {
    public static final String SERVER_NAME = "@JavaChatServer";
    ServerSocket sc;
    private static final ConcurrentMap<String, BufferedOutputStream> clients = new ConcurrentHashMap<>();
    private static final List<Thread> clientThreads = new ArrayList<>();
    boolean isServerStopped;

    public void listen(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            sc = serverSocket;
            System.out.printf("JavaChatServer starter (%s:%s)\n", sc.getInetAddress(), sc.getLocalPort());
            while (isServerStopped) {
                Socket cs = sc.accept();
                Thread clientThread = new Thread(() -> {
                    try (var in = new BufferedInputStream(cs.getInputStream());
                         var out = new BufferedOutputStream(cs.getOutputStream())) {
                        while (true) {
                            Message msg = readMessage(in);
                            if (msg.message.isEmpty()) { // handshake
                                if (clients.containsValue(out))
                                    writeMessage(out, new Message(SERVER_NAME, msg.sender + new Random().nextInt()));
                                else
                                    clients.put(msg.sender, out);
                                continue;
                            }
                            for (BufferedOutputStream bos : clients.values())
                                if (!bos.equals(out))
                                    writeMessage(bos, msg);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                });
                clientThreads.add(clientThread);
                clientThread.start();
            }
        }
    }

    private Message readMessage(BufferedInputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(in);
        return (Message) ois.readObject();
    }

    private void writeMessage(BufferedOutputStream out, Message msg) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(msg);
        oos.flush();
    }

    public void exit() throws IOException, InterruptedException {
        isServerStopped = true;
        sc.close();
        for (Thread thread : clientThreads)
            thread.join();
    }
}