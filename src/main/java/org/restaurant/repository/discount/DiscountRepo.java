package org.restaurant.repository.discount;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.discount.Discount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountRepo {

    public boolean createDiscount(String code, double percent, double minAmount, Integer maxUses, Date validFrom, Date validUntil) {
        String sql = "INSERT INTO discounts (code, discount_percent, min_order_amount, max_uses, valid_from, valid_until) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setDouble(2, percent);
            ps.setDouble(3, minAmount);
            if (maxUses == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, maxUses);
            }
            ps.setDate(5, validFrom);
            ps.setDate(6, validUntil);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Failed to create discount: " + e.getMessage());
            return false;
        }
    }

    public List<Discount> getAll() {
        List<Discount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts";
        try (Connection con = CleverCloudDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                discounts.add(new Discount(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getDouble("discount_percent"),
                        rs.getDouble("min_order_amount"),
                        rs.getObject("max_uses") != null ? rs.getInt("max_uses") : null,
                        rs.getInt("used_count"),
                        rs.getDate("valid_from"),
                        rs.getDate("valid_until"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return discounts;
    }

    public Discount getByCode(String code) {
        String sql = "SELECT * FROM discounts WHERE code = ? AND is_active = TRUE";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Discount(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getDouble("discount_percent"),
                            rs.getDouble("min_order_amount"),
                            rs.getObject("max_uses") != null ? rs.getInt("max_uses") : null,
                            rs.getInt("used_count"),
                            rs.getDate("valid_from"),
                            rs.getDate("valid_until"),
                            rs.getBoolean("is_active"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching discount: " + e.getMessage());
        }
        return null;
    }
    
    public void incrementUsedCount(int discountId) {
        String sql = "UPDATE discounts SET used_count = used_count + 1 WHERE id = ?";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
             ps.setInt(1, discountId);
             ps.executeUpdate();
             
        } catch (Exception e) {
            System.out.println("❌ Error updating discount count: " + e.getMessage());
        }
    }
}
