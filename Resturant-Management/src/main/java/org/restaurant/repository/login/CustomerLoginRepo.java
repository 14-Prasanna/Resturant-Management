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

    // ─────────────────────────────────────────────
    // REGISTER – insert new customer into DB
    // ─────────────────────────────────────────────
    public boolean register(String username, String password, String fullName, String email, String phone) {
        // First check if username already exists
        if (findByUsername(username) != null) {
            System.out.println("Username already taken. Try another.");
            return false;
        }

        String sql = "INSERT INTO customer_login (username, password, full_name, email, phone) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.executeUpdate();
            System.out.println("Registration successful!");
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username already taken. Try another.");
            return false;
        } catch (Exception e) {
            System.out.println("❌ Registration error: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // LOGIN – find customer by username
    // ─────────────────────────────────────────────
    public CustomerLogin findByUsername(String username) {
        String sql = "SELECT * FROM customer_login WHERE username = ?";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CustomerLogin(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone")
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

    public boolean addFeedback(String username, String orderId, String message) {
        String sql = "INSERT INTO customer_reports (customer_id, order_id, report_message) VALUES ((SELECT id FROM customer_login WHERE username=?), ?, ?)";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            if (orderId == null || orderId.trim().isEmpty()) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, orderId);
            }
            ps.setString(3, message);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Error adding feedback: " + e.getMessage());
            return false;
        }
    }
}
