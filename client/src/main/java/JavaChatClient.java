import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Scanner;

public class JavaChatClient {
    Socket cs;
    private String sender;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    boolean isConsolePrintStopped;
    boolean isConsoleScanStopped;
    Thread consolePrintThread;
    Thread consoleScanThread;

    // Коннект к серверу и отправка Handshake сообщения
    public void connect() {
        try (Socket socket = new Socket("localhost", 9999)) {
            cs = socket;
            print("@bot", "connected to JavaChatServer Socket: " + cs + "\n");
            out = new ObjectOutputStream(cs.getOutputStream());
            consoleScanThread = new Thread(this::consoleScanWorker);
            consoleScanThread.start(); // старт потока чтения из консоли чата
            in = new ObjectInputStream(cs.getInputStream());
            consolePrintThread = new Thread(this::consolePrintWorker);
            consolePrintThread.start(); // старт потока записи в консоль чата
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void consolePrintWorker() {
        try { // ALERT! Using "try-with-resources()" for out/in Objects below = does auto-close Client Socket
            while (!isConsolePrintStopped) {
                Message msg = (Message) in.readObject();
                if (msg.sender.equals(JavaChatServer.SERVER_NAME)) { // handshake
                    sender = msg.message;
                    continue;
                }
                print(msg.sender, msg.message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void consoleScanWorker() {
        Scanner scanner = new Scanner(System.in);
        print("@bot", "Welcome to JavaChat. Please enter your @nick name (or /exit to stop): ");
        try { // ALERT! Using "try-with-resources()" for out/in Objects below = does auto-close Client Socket
            while (!isConsoleScanStopped) {
                String text = scanner.nextLine();
                if (text.equals("/exit"))
                    exit();
                if (text.equals("\n"))
                    continue;
                writeMessage(new Message(sender.isEmpty() ? sender = text : sender, sender.isEmpty() ? null : text));
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    private void writeMessage(Message msg) throws IOException {
        out.writeObject(msg); // send Handshake
        out.flush();
    }

    private void print(String nick, String text) {
        System.out.printf("[%s] %s > %s", LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), nick, text);
    }

    public void exit() throws IOException, InterruptedException {
        print("@bot", "JavaChatClient is finishing operation");
        out.close();
        in.close();
        isConsolePrintStopped = true;
        isConsoleScanStopped = true;
        consolePrintThread.join();
        consoleScanThread.join();
        cs.close();
        print("@bot", "JavaChatClient stopped");
    }
}
