package org.restaurant.repository.checkout;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.checkout.Checkout;

import java.sql.*;

public class CheckoutRepository {

    // ─────────────────────────────────────────────────────────────────────────
    // SAVE  –  insert checkout row
    // ─────────────────────────────────────────────────────────────────────────
    public String save(Checkout checkout) {
        String sql = """
                INSERT INTO checkout
                    (customer_id, delivery_address, city, state, pincode, phone, subtotal, discount_id, discount_amount, final_amount)
                VALUES ((SELECT id FROM customer_login WHERE username = ?), ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, checkout.getCustomerId()); // customerId in checkout is actually the username!
            ps.setString(2, checkout.getAddress());
            ps.setString(3, checkout.getCity());
            ps.setString(4, checkout.getState());
            ps.setString(5, checkout.getPincode());
            ps.setString(6, checkout.getPhone());
            ps.setDouble(7, checkout.getSubtotal());
            
            if (checkout.getDiscountId() != null) {
                ps.setInt(8, checkout.getDiscountId());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            
            ps.setDouble(9, checkout.getDiscountAmount());
            ps.setDouble(10, checkout.getFinalAmount());
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return String.valueOf(rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println("❌ Error saving checkout: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE STATUS
    // ─────────────────────────────────────────────────────────────────────────
    public boolean updateStatus(String checkoutId, String status) {
        // checkout_status removed from schema; handled by customer_orders table status directly
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIND BY ID
    // ─────────────────────────────────────────────────────────────────────────
    public String findStatusById(String checkoutId) {
        // Not needed anymore; order status controls payment fulfillment flow
        return null;
    }
}
