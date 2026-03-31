package org.restaurant.repository.checkout;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.checkout.Checkout;

import java.sql.*;

public class CheckoutRepository {

    // ─────────────────────────────────────────────────────────────────────────
    // SAVE  –  insert checkout row with PENDING status
    // ─────────────────────────────────────────────────────────────────────────
    public boolean save(Checkout checkout) {
        String sql = """
                INSERT INTO checkout
                    (checkout_id, customer_id, customer_name, email, address,
                     subtotal, tax, delivery_charge, total_amount, checkout_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')
                """;

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, checkout.getCheckoutId());
            ps.setString(2, checkout.getCustomerId());
            ps.setString(3, checkout.getCustomerName());
            ps.setString(4, checkout.getEmail());
            ps.setString(5, checkout.getAddress());
            ps.setDouble(6, checkout.getSubtotal());
            ps.setDouble(7, checkout.getTax());
            ps.setDouble(8, checkout.getDeliveryCharge());
            ps.setDouble(9, checkout.getGrandTotal());
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error saving checkout: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE STATUS  –  PENDING → COMPLETED or FAILED
    // ─────────────────────────────────────────────────────────────────────────
    public boolean updateStatus(String checkoutId, String status) {
        String sql = "UPDATE checkout SET checkout_status = ? WHERE checkout_id = ?";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, checkoutId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error updating checkout status: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIND BY ID
    // ─────────────────────────────────────────────────────────────────────────
    public String findStatusById(String checkoutId) {
        String sql = "SELECT checkout_status FROM checkout WHERE checkout_id = ?";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, checkoutId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("checkout_status");
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching checkout: " + e.getMessage());
        }
        return null;
    }
}
