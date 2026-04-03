package org.restaurant.repository.inventory;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.inventory.InventoryItem;
import java.sql.*;
import java.util.*;

public class InventoryRepository {
    private static InventoryRepository instance;

    private InventoryRepository() {}

    public static InventoryRepository getInstance() {
        if (instance == null) instance = new InventoryRepository();
        return instance;
    }

    // ADD item
    public boolean addItem(InventoryItem item) {
        String sql = "INSERT INTO inventory (product_id, name, unit, price, quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getProductId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getUnit());
            ps.setDouble(4, item.getPrice());
            ps.setInt(5, item.getQuantity());
            ps.executeUpdate();
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Product ID already exists!");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // GET ALL
    public Collection<InventoryItem> getAllItems() {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory";
        try (Connection con = CleverCloudDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) items.add(mapRow(rs));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    // GET BY PRODUCT ID
    public InventoryItem getByProductId(String productId) {
        String sql = "SELECT * FROM inventory WHERE product_id = ?";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE QUANTITY
    public boolean reduceQuantity(String productId, int qty) {
        String sql = "UPDATE inventory SET quantity = quantity - ? WHERE product_id = ? AND quantity >= ?";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, qty);
            ps.setString(2, productId);
            ps.setInt(3, qty);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE ITEM
    public boolean updateItem(String productId, String field, String value) {
        String column = switch (field) {
            case "name"        -> "name";
            case "unit"        -> "unit";
            case "price"       -> "price";
            case "quantity"    -> "quantity";
            default -> null;
        };
        if (column == null) return false;

        String sql = "UPDATE inventory SET " + column + " = ? WHERE product_id = ?";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, value);
            ps.setString(2, productId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteItem(String productId) {
        String sql = "DELETE FROM inventory WHERE product_id = ?";
        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, productId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // HELPER
    private InventoryItem mapRow(ResultSet rs) throws SQLException {
        return new InventoryItem(
            rs.getString("product_id"),
            rs.getString("name"),
            rs.getString("unit"),
            rs.getDouble("price"),
            rs.getInt("quantity")
        );
    }
}