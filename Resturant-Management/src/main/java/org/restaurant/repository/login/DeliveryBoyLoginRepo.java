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
                DeliveryBoyLogin boy = new DeliveryBoyLogin(
                    rs.getString("username"),
                    rs.getString("password")
                );
                boy.getAssignedOrders().addAll(getAssignedOrdersFor(username));
                return boy;
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
                DeliveryBoyLogin boy = new DeliveryBoyLogin(
                    rs.getString("username"),
                    rs.getString("password")
                );
                boy.getAssignedOrders().addAll(getAssignedOrdersFor(boy.getUsername()));
                list.add(boy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // DB INTERACTION FOR ASSIGNMENTS
    public boolean assignOrder(String deliveryBoyUsername, String orderId) {
        String sql = "INSERT INTO delivery_assignments (delivery_boy_id, order_id, status) VALUES ((SELECT id FROM delivery_boy_login WHERE username=?), ?, 'ASSIGNED')";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, deliveryBoyUsername);
            ps.setString(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAssignedOrdersFor(String username) {
        List<String> orders = new ArrayList<>();
        String sql = "SELECT da.order_id FROM delivery_assignments da JOIN delivery_boy_login db ON da.delivery_boy_id = db.id WHERE db.username = ? AND da.status != 'DELIVERED'";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(rs.getString("order_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean updateAssignmentStatus(String username, String orderId, String newStatus) {
        String sql;
        if (newStatus.equals("PICKED_UP")) {
             sql = "UPDATE delivery_assignments SET status = ?, picked_up_at = CURRENT_TIMESTAMP WHERE order_id = ? AND delivery_boy_id = (SELECT id FROM delivery_boy_login WHERE username=?)";
        } else {
             sql = "UPDATE delivery_assignments SET status = ? WHERE order_id = ? AND delivery_boy_id = (SELECT id FROM delivery_boy_login WHERE username=?)";
        }
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, orderId);
            ps.setString(3, username);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAsDelivered(String username, String orderId) {
        String updateStatus = "UPDATE delivery_assignments SET status = 'DELIVERED', delivered_at = CURRENT_TIMESTAMP WHERE order_id = ? AND delivery_boy_id = (SELECT id FROM delivery_boy_login WHERE username=?)";
        String insertHistory = "INSERT INTO delivery_history (delivery_boy_id, order_id) VALUES ((SELECT id FROM delivery_boy_login WHERE username=?), ?)";
        
        try (Connection con = CleverCloudDB.getConnection()) {
             con.setAutoCommit(false);
             try (PreparedStatement uPs = con.prepareStatement(updateStatus);
                  PreparedStatement iPs = con.prepareStatement(insertHistory)) {
                 
                 uPs.setString(1, orderId);
                 uPs.setString(2, username);
                 uPs.executeUpdate();

                 iPs.setString(1, username);
                 iPs.setString(2, orderId);
                 iPs.executeUpdate();
                 
                 con.commit();
                 return true;
             } catch (Exception e) {
                 con.rollback();
                 return false;
             }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}