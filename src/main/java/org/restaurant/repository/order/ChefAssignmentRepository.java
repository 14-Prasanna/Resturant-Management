package org.restaurant.repository.order;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.order.ChefAssignment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChefAssignmentRepository {

    public boolean assignOrder(String chefUsername, String orderId) {
        // chef_id must be fetched via subquery from chef_login
        String sql = "INSERT INTO chef_assignments (chef_id, order_id, status) VALUES ((SELECT id FROM chef_login WHERE username = ?), ?, 'PREPARING')";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, chefUsername);
            ps.setString(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Failed to assign order: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAssignmentStatus(String orderId, String status) {
        String sql = "UPDATE chef_assignments SET status = ? WHERE order_id = ?";
        if (status.equals("PREPARED")) {
            sql = "UPDATE chef_assignments SET status = ?, prepared_at = CURRENT_TIMESTAMP WHERE order_id = ?";
        }
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<ChefAssignment> getAssignmentsByChef(String chefUsername) {
        List<ChefAssignment> assignments = new ArrayList<>();
        String sql = """
            SELECT (SELECT username FROM chef_login WHERE id = c.chef_id) as chef_username, 
                   c.order_id, c.status, c.assigned_at 
            FROM chef_assignments c 
            WHERE c.chef_id = (SELECT id FROM chef_login WHERE username = ?) 
            ORDER BY c.assigned_at DESC
        """;
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, chefUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assignments.add(new ChefAssignment(
                            rs.getString("chef_username"),
                            rs.getString("order_id"),
                            rs.getString("status"),
                            rs.getString("assigned_at")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assignments;
    }
}
