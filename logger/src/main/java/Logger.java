import java.time.LocalTime;

public class Logger {
/*
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
*/
    private static String clrf = "";

    public static void print(String nick, String text/*, boolean you*/) {
        //String begin = you ? ANSI_GREEN : ANSI_RED;
        //System.out.printf(begin + clrf + "[%s] @%s> %s" + ANSI_RESET, LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), nick, text);
        System.out.printf(clrf + "[%s] @%s> %s", LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), nick, text);
    }

    public static void println(String nick, String text/*, boolean you*/) {
        synchronized (clrf) {
        clrf = "\n";
        print(nick, text/*, you*/);
        clrf = "";
        }
    }
}
