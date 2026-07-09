package org.restaurant.repository.menu;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.menu.MenuItem;

import java.sql.*;
import java.util.*;

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
    // COMMON SELECT QUERY BASE
    // ─────────────────────────────────────────────
    private final String SELECT_BASE = 
        "SELECT m.product_id, m.name, m.description, m.rating, m.price, " +
        "GROUP_CONCAT(mt.name SEPARATOR ',') as meal_times " +
        "FROM menu_items m " +
        "LEFT JOIN menu_item_meal_times mmt ON m.id = mmt.menu_item_id " +
        "LEFT JOIN meal_times mt ON mmt.meal_time_id = mt.id ";

    // ─────────────────────────────────────────────
    // READ – fetch all items from DB
    // ─────────────────────────────────────────────
    public Collection<MenuItem> getAllItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = SELECT_BASE + "GROUP BY m.id";

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
        // Note: HAVING is used since meal_times is aggregated
        String sql = SELECT_BASE + "GROUP BY m.id HAVING LOWER(meal_times) LIKE LOWER(?) OR meal_times IS NULL";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + mealTime + "%");
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
        String sql = SELECT_BASE + "WHERE m.product_id = ? GROUP BY m.id";

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
    // CREATE
    // ─────────────────────────────────────────────
    public boolean addItem(MenuItem item, List<Integer> mealTimeIds) {
        String insertMenuSql = "INSERT INTO menu_items (product_id, name, description, rating, price) VALUES (?, ?, ?, ?, ?)";
        String insertMappingSql = "INSERT INTO menu_item_meal_times (menu_item_id, meal_time_id) VALUES (?, ?)";

        try (Connection con = CleverCloudDB.getConnection()) {
            con.setAutoCommit(false); // Start transaction

            int menuId = -1;
            try (PreparedStatement ps = con.prepareStatement(insertMenuSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, item.getProductId());
                ps.setString(2, item.getName());
                ps.setString(3, item.getDescription());
                ps.setDouble(4, item.getRating());
                ps.setDouble(5, item.getPrice());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        menuId = rs.getInt(1);
                    }
                }
            }

            if (menuId != -1 && mealTimeIds != null && !mealTimeIds.isEmpty()) {
                try (PreparedStatement ps2 = con.prepareStatement(insertMappingSql)) {
                    for (Integer mtId : mealTimeIds) {
                        ps2.setInt(1, menuId);
                        ps2.setInt(2, mtId);
                        ps2.addBatch();
                    }
                    ps2.executeBatch();
                }
            }

            con.commit(); // Commit transaction
            return true;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("❌ Constraint violation: " + e.getMessage());
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
        String column = switch (field) {
            case "name"        -> "name";
            case "description" -> "description";
            case "rating"      -> "rating";
            case "price"       -> "price";
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

    public boolean updateMealTimes(String productId, List<Integer> mealTimeIds) {
        String getMenuIdSql = "SELECT id FROM menu_items WHERE product_id = ?";
        String deleteMappingSql = "DELETE FROM menu_item_meal_times WHERE menu_item_id = ?";
        String insertMappingSql = "INSERT INTO menu_item_meal_times (menu_item_id, meal_time_id) VALUES (?, ?)";

        try (Connection con = CleverCloudDB.getConnection()) {
            con.setAutoCommit(false);

            int menuId = -1;
            try (PreparedStatement ps = con.prepareStatement(getMenuIdSql)) {
                ps.setString(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) menuId = rs.getInt("id");
                }
            }

            if (menuId == -1) return false;

            try (PreparedStatement ps = con.prepareStatement(deleteMappingSql)) {
                ps.setInt(1, menuId);
                ps.executeUpdate();
            }

            if (mealTimeIds != null && !mealTimeIds.isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement(insertMappingSql)) {
                    for (Integer mtId : mealTimeIds) {
                        ps.setInt(1, menuId);
                        ps.setInt(2, mtId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            
            con.commit();
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error updating meal times: " + e.getMessage());
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
        String mtString = rs.getString("meal_times");
        List<String> mealTimes = new ArrayList<>();
        if (mtString != null && !mtString.trim().isEmpty()) {
            mealTimes.addAll(Arrays.asList(mtString.split(",")));
        }

        return new MenuItem(
                rs.getString("product_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("rating"),
                rs.getDouble("price"),
                mealTimes
        );
    }
}
