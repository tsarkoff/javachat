import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    public static final String SERVER_NAME = "server";
    public static final String BOT = "bot";
    public static final String YOU = "(you)";
    public static final String EXIT = "/exit";
    private static final String CONFIG_FILE = "/settings.txt";
    public static String ADDRESS;
    public static int PORT;

    static {
        Properties properties = new Properties();
        FileInputStream propertiesFile;
        try {
            final String configure = getAbsoluteFilePath(CONFIG_FILE);
            propertiesFile = new FileInputStream(configure);
            properties.load(propertiesFile);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        ADDRESS = properties.getProperty("ADDRESS");
        PORT = Integer.parseInt(properties.getProperty("PORT"));
    }

    public static String getAbsoluteFilePath(String fileName) {
        final Path currentRelativePath = Paths.get("");
        final String s = currentRelativePath.toAbsolutePath().toString();
        return s + "/" + fileName;
    }

}