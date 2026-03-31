package org.restaurant.repository.login;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.login.CustomerLogin;

import java.sql.*;
import java.util.*;

public class CustomerLoginRepo {

    private static CustomerLoginRepo instance;

    private CustomerLoginRepo() {}

    public static CustomerLoginRepo getInstance() {
        if (instance == null) {
            instance = new CustomerLoginRepo();
        }
        return instance;
    }

    // Register with all fields stored to DB
    public boolean register(String username, String hashedPassword, String email, String phone) {
        String checkSql  = "SELECT username FROM customer_login WHERE username = ?";
        String insertSql = "INSERT INTO customer_login (username, password, email, phone) VALUES (?, ?, ?, ?)";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {

            checkPs.setString(1, username);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                System.out.println("Username already taken. Try another.");
                return false;
            }

            try (PreparedStatement insertPs = con.prepareStatement(insertSql)) {
                insertPs.setString(1, username);
                insertPs.setString(2, hashedPassword);
                insertPs.setString(3, email);
                insertPs.setString(4, phone);
                insertPs.executeUpdate();
                System.out.println("Registration successful!");
                return true;
            }

        } catch (Exception e) {
            System.out.println("❌ Registration error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // LOGIN – find customer by username
    // ─────────────────────────────────────────────
    public CustomerLogin findByUsername(String username) {
        String sql = "SELECT username, password FROM customer_login WHERE username = ?";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CustomerLogin(
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Login lookup error: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // GET ALL CUSTOMERS
    // ─────────────────────────────────────────────
    public Collection<CustomerLogin> getAllCustomers() {
        List<CustomerLogin> customers = new ArrayList<>();
        String sql = "SELECT username, password FROM customer_login";

        try (Connection con = CleverCloudDB.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(new CustomerLogin(
                        rs.getString("username"),
                        rs.getString("password")
                ));
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching all customers: " + e.getMessage());
        }
        return customers;
    }
}
