package com.kec.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection - JDBC Connection Manager for KEC Evaluation System.
 * Manages database driver registration, connection pooling/acquisition,
 * configuration loading, and diagnostic fallback.
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

        // 1. Try loading from working directory db.properties
        File file = new File("db.properties");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                loaded = true;
            } catch (Exception ignored) {
            }
        }

        // 2. Try loading from classpath
        if (!loaded) {
            try (InputStream is = DBConnection.class.getResourceAsStream("/db.properties")) {
                if (is != null) {
                    props.load(is);
                    loaded = true;
                }
            } catch (Exception ignored) {
            }
        }

        // 3. Set properties with robust defaults
        url = props.getProperty("db.url", 
                "jdbc:mysql://localhost:3307/kec_eval_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata");
        user = props.getProperty("db.user", "root");
        password = props.getProperty("db.password", "");
        driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("[WARN] MySQL Driver not found on classpath: " + e.getMessage());
        }
    }

    /**
     * Obtains an active JDBC Connection.
     * Includes automatic fallback between port 3307 and 3306 if needed.
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException primaryEx) {
            // If configured on 3307 and failed, attempt fallback to 3306
            if (url.contains(":3307")) {
                String altUrl = url.replace(":3307", ":3306");
                try {
                    Connection altConn = DriverManager.getConnection(altUrl, user, password);
                    url = altUrl; // update url to functioning port
                    return altConn;
                } catch (SQLException ignored) {
                }
            }
            throw primaryEx;
        }
    }

    /**
     * Verifies whether the database is accessible.
     */
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

    public static String getUser() {
        return user;
    }

    public static void setCredentials(String newUrl, String newUser, String newPass) {
        url = newUrl;
        user = newUser;
        password = newPass;
    }

    public static void close(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception ignored) {
            }
        }
    }
}
