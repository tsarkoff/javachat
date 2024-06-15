import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final String CONFIG_FILE = "./setts.cfg";
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
        ADDRESS = properties.getProperty("PORT");
        PORT = Integer.parseInt(properties.getProperty("PORT"));
    }
}