import java.time.LocalTime;

public class Logger {
    public static void print(String nick, String text) {
        System.out.printf("[%s] @%s> %s", LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), nick, text);
    }
}
