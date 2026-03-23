package org.restaurant.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CleverCloudDB {

    private static final String URL =
            "jdbc:mysql://bxohvsqqznggomjetfir-mysql.services.clever-cloud.com:3306/bxohvsqqznggomjetfir" +
                    "?useSSL=true&requireSSL=true&verifyServerCertificate=false";

    private static final String USER = "ubbtxt5u2yy4tvxa";
    private static final String PASSWORD = "xG0D9YHiXIVl4ySRihXZ";

    // 🔌 Reusable connection
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 🧪 Test connection
    public static void testConnection() {
        try (Connection con = getConnection()) {
            System.out.println("✅ DB Connected Successfully 🚀");
        } catch (Exception e) {
            System.out.println("❌ DB Connection Failed");
            e.printStackTrace();
        }
    }

    // 📊 Show all tables
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
            e.printStackTrace();
        }
    }
}