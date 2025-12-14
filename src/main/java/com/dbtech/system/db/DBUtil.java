package com.dbtech.system.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;
import java.io.IOException;

public class DBUtil {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    // Static block: Τρέχει μια φορά όταν φορτώνει η κλάση
    static {
        try (InputStream input = DBUtil.class.getResourceAsStream("/db.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
            } else {
                // Φόρτωση των ρυθμίσεων από το αρχείο
                prop.load(input);

                URL = prop.getProperty("db.url");
                USER = prop.getProperty("db.user");
                PASSWORD = prop.getProperty("db.password");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}