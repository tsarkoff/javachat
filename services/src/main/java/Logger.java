import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;

import static java.lang.String.format;

public class Logger {
    public static final String SERVER_LOG = "./server_log.txt";
    public static final String CLIENT_LOG = "./clients_log.txt";
    private String crlf = "";
    private final FileWriter fileWriter;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RED = "\u001B[31m";

    public Logger(String logFilePath) throws IOException {
        fileWriter = new FileWriter(logFilePath);
        fileWriter.flush();
    }

    public void print(String nick, String text) {
        String color;
        if (nick.contains("you"))
            color = ANSI_GREEN;
        else if (nick.contains("bot") || text.contains("new"))
            color = ANSI_YELLOW;
        else if (text.contains("disconnected") || text.contains("stop") || text.contains("exit"))
            color = ANSI_RED;
        else
            color = ANSI_CYAN;

        String record = format(color + crlf + "[%s] @%s> %s" + ANSI_RESET, LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), nick, text);
        System.out.print(record);
        try {
            record = format("[%s] @%s> %s", LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), nick, text);
            fileWriter.append(record);
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void println(String nick, String text) {
        crlf = "\n";
        print(nick, text);
        crlf = "";
    }
}
