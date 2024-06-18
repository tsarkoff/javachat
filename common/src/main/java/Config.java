import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    public static final String SERVER_NAME = "server";
    public static final String EXIT = "/exit";
    public static final String STOP = "/stop";
    private static final String CONFIG_FILE = "./settings.txt";
    public static String ADDRESS;
    public static int PORT;

    static {
        Properties properties = new Properties();
        FileInputStream propertiesFile;
        try {
            propertiesFile = new FileInputStream(CONFIG_FILE);
            properties.load(propertiesFile);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        ADDRESS = properties.getProperty("ADDRESS");
        PORT = Integer.parseInt(properties.getProperty("PORT"));
    }
}