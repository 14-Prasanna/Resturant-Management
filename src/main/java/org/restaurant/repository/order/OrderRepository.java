package org.restaurant.repository.order;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.order.Order;

import java.sql.*;
import java.util.*;

public class OrderRepository {

    private static OrderRepository instance;
    private OrderRepository() {}

    public static OrderRepository getInstance() {
        if (instance == null) instance = new OrderRepository();
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SAVE ORDER  –  inserts into customer_orders + order_items in ONE DB transaction
    // If anything fails, both inserts are rolled back automatically.
    // ─────────────────────────────────────────────────────────────────────────
    public boolean saveOrder(Order order) {
        String insertOrder = """
                INSERT INTO customer_orders
                    (id, customer_id, checkout_id, total_amount, final_amount, status)
                VALUES (?, (SELECT id FROM customer_login WHERE username = ?), ?, ?, ?, ?)
                """;

        String insertItem = """
                INSERT INTO order_items
                    (order_id, menu_item_id, quantity, unit_price)
                VALUES (?, (SELECT id FROM menu_items WHERE product_id = ?), ?, ?)
                """;

        Connection con = null;
        try {
            con = CleverCloudDB.getConnection();
            con.setAutoCommit(false);                           // BEGIN TRANSACTION

            // 1. Insert parent order row
            try (PreparedStatement ps = con.prepareStatement(insertOrder)) {
                ps.setString(1, order.getOrderId());
                // We assume customer_id in Order is numeric or maps to id in customer_login
                // If it is a string id, we need to find numeric ID. But let's assume it maps correctly or is an int as string.
                ps.setString(2, order.getCustomerId());
                ps.setString(3, order.getCheckoutId() != null && !order.getCheckoutId().isEmpty() ? order.getCheckoutId() : null);
                ps.setDouble(4, order.getTotalAmount());
                ps.setDouble(5, order.getTotalAmount()); // Set final amount same as total
                ps.setString(6, order.getStatus());
                ps.executeUpdate();
            }

            // 2. Insert each child item row (batch for efficiency)
            try (PreparedStatement ps = con.prepareStatement(insertItem)) {
                for (CartItem item : order.getItems()) {
                    ps.setString(1, order.getOrderId());
                    ps.setString(2, item.getProductId());
                    ps.setInt   (3, item.getQuantity());
                    ps.setDouble(4, item.getPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 3. Check and deduct inventory
            String updateInv = "UPDATE inventory SET quantity = quantity - ? WHERE product_id = ? AND quantity >= ?";
            try (PreparedStatement psInv = con.prepareStatement(updateInv)) {
                for (CartItem item : order.getItems()) {
                    psInv.setInt(1, item.getQuantity());
                    psInv.setString(2, item.getProductId());
                    psInv.setInt(3, item.getQuantity());
                    int updated = psInv.executeUpdate();
                    if (updated == 0) {
                        throw new SQLException("Insufficient inventory for: " + item.getName());
                    }
                    System.out.println("✅ Inventory Reduction: Deducted " + item.getQuantity() + " unit(s) of " + item.getName() + " (Product ID: " + item.getProductId() + ").");
                }
            }

            // 4. Delete cart rows for this customer atomically within the same transaction
            //    so that if order placement fails the cart is NOT cleared (rollback covers it)
            String deleteCart = """
                    DELETE FROM cart
                    WHERE customer_id = (SELECT id FROM customer_login WHERE username = ?)
                    """;
            try (PreparedStatement psCart = con.prepareStatement(deleteCart)) {
                psCart.setString(1, order.getCustomerId());
                int deleted = psCart.executeUpdate();
                System.out.println("🛒 Cart cleared from DB: " + deleted + " item(s) removed for customer '" + order.getCustomerId() + "'.");
            }

            con.commit();                                       // COMMIT
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error saving order: " + e.getMessage());
            try { if (con != null) con.rollback(); } catch (Exception ignored) {}
            return false;

        } finally {
            try {
                if (con != null) { con.setAutoCommit(true); con.close(); }
            } catch (Exception ignored) {}
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIND BY ORDER ID
    // ─────────────────────────────────────────────────────────────────────────
    public Order findById(String orderId) {
        List<Order> list = fetchOrders("WHERE o.id = ?", orderId);
        return list.isEmpty() ? null : list.get(0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET ORDERS FOR ONE CUSTOMER
    // ─────────────────────────────────────────────────────────────────────────
    public List<Order> getOrdersByCustomer(String customerUsername) {
        // Find customer.id via subquery since Order stores username in `customer_id` for some reason, but let's query customer_login by username.
        List<Order> list = fetchOrders("WHERE o.customer_id = (SELECT id FROM customer_login WHERE username = ?)", customerUsername);
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET ALL ORDERS  (admin / manager view)
    // ─────────────────────────────────────────────────────────────────────────
    public List<Order> getAllOrders() {
        return fetchOrders(null, null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE ORDER STATUS
    // ─────────────────────────────────────────────────────────────────────────
    public boolean updateOrderStatus(String orderId, String newStatus) {
        String sql = "UPDATE customer_orders SET status = ? WHERE id = ?";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, orderId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error updating order status: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPER  –  JOIN customer_orders + order_items + menu_items
    // ─────────────────────────────────────────────────────────────────────────
    private List<Order> fetchOrders(String whereClause, String param) {

        String sql = """
                SELECT  o.id as order_id,
                        (SELECT username FROM customer_login WHERE id = o.customer_id) as customer_username,
                        o.checkout_id,
                        o.total_amount,
                        o.status as order_status,
                        o.ordered_at as created_at,
                        m.product_id,
                        m.name,
                        i.unit_price as price,
                        i.quantity
                FROM    customer_orders o
                JOIN    order_items i ON o.id = i.order_id
                JOIN    menu_items m ON i.menu_item_id = m.id
                """
                + (whereClause != null ? whereClause + " " : "")
                + "ORDER BY o.ordered_at DESC, i.id ASC";

        Map<String, Order> map = new LinkedHashMap<>();

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (param != null) ps.setString(1, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String orderId = rs.getString("order_id");

                    if (!map.containsKey(orderId)) {
                        map.put(orderId, new Order(
                                orderId,
                                rs.getString("customer_username"), // Pass username back
                                new ArrayList<>(),
                                rs.getDouble("total_amount"),
                                rs.getString("order_status"),
                                rs.getString("checkout_id"),
                                rs.getString("created_at")
                        ));
                    }

                    map.get(orderId).getItems().add(new CartItem(
                            rs.getString("product_id"),
                            rs.getString("name"),
                            "Any", // Default MealTime to avoid nulls
                            rs.getDouble("price"),
                            rs.getInt   ("quantity")
                    ));
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error fetching orders: " + e.getMessage());
        }

        return new ArrayList<>(map.values());
    }
}
