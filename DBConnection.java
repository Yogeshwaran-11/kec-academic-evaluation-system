import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection - JDBC Connection Manager for KEC Evaluation System.
 * Submission Deliverable: DBConnection.java
 */
public class DBConnection {

    private static String url;
    private static String user;
    private static String password;
    private static String driver;

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        Properties props = new Properties();
        boolean loaded = false;

        File file = new File("db.properties");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                loaded = true;
            } catch (Exception ignored) {
            }
        }

        if (!loaded) {
            try (InputStream is = DBConnection.class.getResourceAsStream("/db.properties")) {
                if (is != null) {
                    props.load(is);
                    loaded = true;
                }
            } catch (Exception ignored) {
            }
        }

        url = props.getProperty("db.url", 
                "jdbc:mysql://localhost:3307/kec_eval_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata");
        user = props.getProperty("db.user", "root");
        password = props.getProperty("db.password", "");
        driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("[WARN] MySQL Driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException primaryEx) {
            if (url.contains(":3307")) {
                String altUrl = url.replace(":3307", ":3306");
                try {
                    Connection altConn = DriverManager.getConnection(altUrl, user, password);
                    url = altUrl;
                    return altConn;
                } catch (SQLException ignored) {
                }
            }
            throw primaryEx;
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public static String getActiveUrl() {
        return url;
    }

    public static void close(AutoCloseable res) {
        if (res != null) {
            try {
                res.close();
            } catch (Exception ignored) {
            }
        }
    }
}
