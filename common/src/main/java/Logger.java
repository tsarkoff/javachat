import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.time.LocalTime;

import static java.lang.String.format;

public class Logger {
    public static final String SERVER_LOG = "./server_log.txt";
    public static final String CLIENT_LOG = "./clients_log.txt";
    public static String CRLF = "";
    private static Logger logger = null;
    private final FileWriter fileWriter;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RED = "\u001B[31m";

    public static void getLogger(String logFilePath) {
        if (logger == null) {
            try {
                logger = new Logger(logFilePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Logger getLogger() {
        if (logger == null)
            try {
                throw new InvalidObjectException("Singleton Logger must first be called by getLogger(String logFilePath), and only then by getLogger()");
            } catch (InvalidObjectException e) {
                throw new RuntimeException(e);
            }
        return logger;
    }

    private Logger(String logFilePath) throws IOException {
        fileWriter = new FileWriter(logFilePath);
        fileWriter.flush();
    }

    public synchronized void print(String nick, String text) {
        String color;
        if (nick.contains("you"))
            color = ANSI_GREEN;
        else if (nick.contains("bot") || text.contains("new"))
            color = ANSI_YELLOW;
        else if (text.contains("disconnected") || text.contains("stop") || text.contains("exit"))
            color = ANSI_RED;
        else
            color = ANSI_CYAN;
        String record = format(CRLF + color + "[%s] @%s> %s" + ANSI_RESET, LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), nick, text);
        System.out.print(record);
        if (text != null && !text.isEmpty() && !text.isBlank()) {
            try {
                log(new Message(nick, text));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void log(Message msg) throws IOException {
        // нужно дотюнить Лог = в файл пишется иногда крокозябла (может synchronized надо)
        if (msg.text != null && !msg.text.isEmpty() && !msg.text.isBlank()) {
            String record = String.format("[%s] [%s]\t@%s> %s\n", LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), msg.id, msg.sender, msg.text);
            fileWriter.append(record);
            fileWriter.flush();
        }
    }

    public synchronized Logger println(String nick, String text) {
        print(nick, text + "\n");
        return this;
    }

    public synchronized Logger lnprint(String nick, String text) {
        CRLF = "\n";
        print(nick, text + CRLF);
        CRLF = "";
        return this;
    }
}
