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
    // SAVE ORDER  –  inserts into orders + order_items in ONE DB transaction
    // If anything fails, both inserts are rolled back automatically.
    // ─────────────────────────────────────────────────────────────────────────
    public boolean saveOrder(Order order) {
        String insertOrder = """
                INSERT INTO orders
                    (order_id, customer_id, checkout_id, total_amount, order_status)
                VALUES (?, ?, ?, ?, ?)
                """;

        String insertItem = """
                INSERT INTO order_items
                    (order_id, product_id, name, meal_time, price, quantity, item_total)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        Connection con = null;
        try {
            con = CleverCloudDB.getConnection();
            con.setAutoCommit(false);                           // BEGIN TRANSACTION

            // 1. Insert parent order row
            try (PreparedStatement ps = con.prepareStatement(insertOrder)) {
                ps.setString(1, order.getOrderId());
                ps.setString(2, order.getCustomerId());
                ps.setString(3, order.getCheckoutId());        // nullable
                ps.setDouble(4, order.getTotalAmount());
                ps.setString(5, order.getStatus());
                ps.executeUpdate();
            }

            // 2. Insert each child item row (batch for efficiency)
            try (PreparedStatement ps = con.prepareStatement(insertItem)) {
                for (CartItem item : order.getItems()) {
                    ps.setString(1, order.getOrderId());
                    ps.setString(2, item.getProductId());
                    ps.setString(3, item.getName());
                    ps.setString(4, item.getMealTime());
                    ps.setDouble(5, item.getPrice());
                    ps.setInt   (6, item.getQuantity());
                    ps.setDouble(7, item.getTotalPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
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
        List<Order> list = fetchOrders("WHERE o.order_id = ?", orderId);
        return list.isEmpty() ? null : list.get(0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET ORDERS FOR ONE CUSTOMER
    // ─────────────────────────────────────────────────────────────────────────
    public List<Order> getOrdersByCustomer(String customerId) {
        return fetchOrders("WHERE o.customer_id = ?", customerId);
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
        String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";

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
    // PRIVATE HELPER  –  JOIN orders + order_items and assemble Order objects
    //
    // Each order has multiple items, so the JOIN produces multiple rows per
    // order. We use a LinkedHashMap to group items under their order_id
    // and preserve insertion (date DESC) order.
    // ─────────────────────────────────────────────────────────────────────────
    private List<Order> fetchOrders(String whereClause, String param) {

        String sql = """
                SELECT  o.order_id,
                        o.customer_id,
                        o.checkout_id,
                        o.total_amount,
                        o.order_status,
                        o.created_at,
                        i.product_id,
                        i.name,
                        i.meal_time,
                        i.price,
                        i.quantity
                FROM    orders o
                JOIN    order_items i ON o.order_id = i.order_id
                """
                + (whereClause != null ? whereClause + " " : "")
                + "ORDER BY o.created_at DESC, i.id ASC";

        Map<String, Order> map = new LinkedHashMap<>();

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (param != null) ps.setString(1, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String orderId = rs.getString("order_id");

                    // First time we see this orderId → create the Order shell
                    if (!map.containsKey(orderId)) {
                        map.put(orderId, new Order(
                                orderId,
                                rs.getString("customer_id"),
                                new ArrayList<>(),
                                rs.getDouble("total_amount"),
                                rs.getString("order_status"),
                                rs.getString("checkout_id"),
                                rs.getString("created_at")
                        ));
                    }

                    // Always append the item to the existing Order
                    map.get(orderId).getItems().add(new CartItem(
                            rs.getString("product_id"),
                            rs.getString("name"),
                            rs.getString("meal_time"),
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
