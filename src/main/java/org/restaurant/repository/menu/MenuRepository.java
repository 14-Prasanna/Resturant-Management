package org.restaurant.repository.menu;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.menu.MenuItem;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MenuRepository {

    private static MenuRepository instance;

    private MenuRepository() {}

    public static MenuRepository getInstance() {
        if (instance == null) {
            instance = new MenuRepository();
        }
        return instance;
    }

    // ─────────────────────────────────────────────
    // READ – fetch all items from DB
    // ─────────────────────────────────────────────
    public Collection<MenuItem> getAllItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT product_id, name, description, rating, price, meal_time FROM menu_items";

        try (Connection con = CleverCloudDB.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapRow(rs));
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching menu items: " + e.getMessage());
        }
        return items;
    }

    // ─────────────────────────────────────────────
    // READ – filter by meal time
    // ─────────────────────────────────────────────
    public Collection<MenuItem> getItemsByMealTime(String mealTime) {
        if (mealTime.equalsIgnoreCase("All")) {
            return getAllItems();
        }

        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT product_id, name, description, rating, price, meal_time "
                + "FROM menu_items WHERE LOWER(meal_time) = LOWER(?)";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mealTime);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching items by meal time: " + e.getMessage());
        }
        return items;
    }

    // ─────────────────────────────────────────────
    // READ – get single item by product_id
    // ─────────────────────────────────────────────
    public MenuItem getItemByProductId(String productId) {
        String sql = "SELECT product_id, name, description, rating, price, meal_time "
                + "FROM menu_items WHERE product_id = ?";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching item by ID: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // CHECK – duplicate name + mealTime combo
    // ─────────────────────────────────────────────
    public boolean existsByNameAndMealTime(String name, String mealTime) {
        String sql = "SELECT COUNT(*) FROM menu_items "
                + "WHERE LOWER(name) = LOWER(?) AND LOWER(meal_time) = LOWER(?)";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, mealTime);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error checking duplicate: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // CHECK – get all meal times a name exists in
    // ─────────────────────────────────────────────
    public List<String> getMealTimesForName(String name) {
        List<String> mealTimes = new ArrayList<>();
        String sql = "SELECT meal_time FROM menu_items WHERE LOWER(name) = LOWER(?)";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mealTimes.add(rs.getString("meal_time"));
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching meal times for name: " + e.getMessage());
        }
        return mealTimes;
    }

    // ─────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────
    public boolean addItem(MenuItem item) {
        String sql = "INSERT INTO menu_items (product_id, name, description, rating, price, meal_time) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, item.getProductId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getDescription());
            ps.setDouble(4, item.getRating());
            ps.setDouble(5, item.getPrice());
            ps.setString(6, item.getMealTime());

            ps.executeUpdate();
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            // product_id already exists (duplicate PK)
            return false;
        } catch (Exception e) {
            System.out.println("❌ Error adding item: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // UPDATE – update a single field
    // ─────────────────────────────────────────────
    public boolean updateField(String productId, String field, String value) {
        // Map Java field names → DB column names
        String column = switch (field) {
            case "name"        -> "name";
            case "description" -> "description";
            case "rating"      -> "rating";
            case "price"       -> "price";
            case "mealTime"    -> "meal_time";
            default -> null;
        };

        if (column == null) {
            System.out.println("❌ Unknown field: " + field);
            return false;
        }

        String sql = "UPDATE menu_items SET " + column + " = ? WHERE product_id = ?";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, value);
            ps.setString(2, productId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error updating field: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────
    public boolean deleteItem(String productId) {
        String sql = "DELETE FROM menu_items WHERE product_id = ?";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, productId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error deleting item: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────
    // HELPER – map a ResultSet row → MenuItem
    // ─────────────────────────────────────────────
    private MenuItem mapRow(ResultSet rs) throws SQLException {
        return new MenuItem(
                rs.getString("product_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("rating"),
                rs.getDouble("price"),
                rs.getString("meal_time")
        );
    }
}
