package org.restaurant.repository.login;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.login.DeliveryBoyLogin;
import java.sql.*;
import java.util.*;

public class DeliveryBoyLoginRepo {

    // REGISTER with phone — saves to MySQL
    public boolean register(String username, String password, String phone) {
        String sql = "INSERT INTO delivery_boy_login (username, password, phone) VALUES (?, ?, ?)";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, phone);
            ps.executeUpdate();
            System.out.println("Delivery Boy Registration successful!");
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username already taken. Try another.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // FIND BY USERNAME
    public DeliveryBoyLogin findByUsername(String username) {
        String sql = "SELECT * FROM delivery_boy_login WHERE username = ?";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new DeliveryBoyLogin(
                    rs.getString("username"),
                    rs.getString("password")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // GET ALL
    public Collection<DeliveryBoyLogin> getAllDeliveryBoys() {
        List<DeliveryBoyLogin> list = new ArrayList<>();
        String sql = "SELECT * FROM delivery_boy_login";
        try (Connection con = CleverCloudDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new DeliveryBoyLogin(
                    rs.getString("username"),
                    rs.getString("password")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}