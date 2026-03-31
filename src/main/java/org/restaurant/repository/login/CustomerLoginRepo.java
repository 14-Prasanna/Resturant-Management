package org.restaurant.repository.login;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.login.CustomerLogin;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.*;

public class CustomerLoginRepo {

    public boolean register(String username, String hashedPassword, String email, String phone) {
        String sql = "INSERT INTO customer_login (username, password, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.executeUpdate();
            System.out.println("Customer Registration successful!");
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username already taken. Try another.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public CustomerLogin findByUsername(String username) {
        String sql = "SELECT * FROM customer_login WHERE username = ?";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new CustomerLogin(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Collection<CustomerLogin> getAllCustomers() {
        List<CustomerLogin> list = new ArrayList<>();
        String sql = "SELECT * FROM customer_login";
        try (Connection con = CleverCloudDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new CustomerLogin(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}