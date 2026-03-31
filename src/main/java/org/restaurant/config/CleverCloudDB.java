package org.restaurant.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CleverCloudDB {

    private static final String URL      = System.getenv("DB_URL");
    private static final String USER     = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws Exception {
        if (URL == null || URL.isEmpty()) {
            throw new IllegalStateException("Database URL not configured. Please set the DB_URL environment variable.");
        }
        if (USER == null || USER.isEmpty()) {
            throw new IllegalStateException("Database user not configured. Please set the DB_USER environment variable.");
        }
        if (PASSWORD == null || PASSWORD.isEmpty()) {
            throw new IllegalStateException("Database password not configured. Please set the DB_PASSWORD environment variable.");
        }
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testConnection() {
        try (Connection con = getConnection()) {
            System.out.println("✅ DB Connected Successfully 🚀");
        } catch (Exception e) {
            System.out.println("❌ DB Connection Failed");
            e.printStackTrace();
        }
    }

    public static void showTables() {
        String query = "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = 'bxohvsqqznggomjetfir'";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            System.out.println("📂 Tables in DB:");
            while (rs.next()) {
                System.out.println("Table: " + rs.getString("table_name"));
            }
        } catch (Exception e) {
            System.out.println("Could not fetch tables — check internet connection!");
        }
    }
}