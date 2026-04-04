package org.restaurant.repository.cart;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.cart.Cart;
import org.restaurant.model.cart.Cart.CartItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartRepository {

    // ─────────────────────────────────────────────────────────────────────────
    // ADD ITEM  –  INSERT or increment quantity if already in cart
    // ─────────────────────────────────────────────────────────────────────────
    public void addItem(String username, CartItem item) {
        String sql = """
                INSERT INTO cart (customer_id, menu_item_id, quantity)
                VALUES (
                    (SELECT id FROM customer_login WHERE username = ?),
                    (SELECT id FROM menu_items WHERE product_id = ?),
                    ?
                )
                ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)
                """;

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, item.getProductId());
            ps.setInt   (3, item.getQuantity());
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("❌ Error adding item to cart: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE QUANTITY  –  set exact quantity; removes row if qty <= 0
    // ─────────────────────────────────────────────────────────────────────────
    public boolean updateItemQuantity(String username, String productId, int newQuantity) {
        if (newQuantity <= 0) {
            return removeItem(username, productId);
        }

        String sql = """
                UPDATE cart SET quantity = ? 
                WHERE customer_id = (SELECT id FROM customer_login WHERE username = ?)
                  AND menu_item_id = (SELECT id FROM menu_items WHERE product_id = ?)
                """;

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt   (1, newQuantity);
            ps.setString(2, username);
            ps.setString(3, productId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error updating cart item: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REMOVE ITEM  –  delete one row by username + product_id
    // ─────────────────────────────────────────────────────────────────────────
    public boolean removeItem(String username, String productId) {
        String sql = """
                DELETE FROM cart 
                WHERE customer_id = (SELECT id FROM customer_login WHERE username = ?)
                  AND menu_item_id = (SELECT id FROM menu_items WHERE product_id = ?)
                """;

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, productId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error removing cart item: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET CART  –  fetch all rows for this customer and build a Cart object
    // ─────────────────────────────────────────────────────────────────────────
    public Cart getCart(String username) {
        Cart cart = new Cart(username);
        String sql = """
                SELECT m.product_id, m.name, 'Any' as meal_time, m.price, c.quantity
                FROM cart c
                JOIN menu_items m ON c.menu_item_id = m.id
                JOIN customer_login cl ON c.customer_id = cl.id
                WHERE cl.username = ?
                """;

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cart.getItems().add(new CartItem(
                            rs.getString("product_id"),
                            rs.getString("name"),
                            rs.getString("meal_time"),
                            rs.getDouble("price"),
                            rs.getInt   ("quantity")
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching cart: " + e.getMessage());
        }
        return cart;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CLEAR CART  –  delete all rows for this customer (called after checkout)
    // ─────────────────────────────────────────────────────────────────────────
    public void clearCart(String username) {
        String sql = "DELETE FROM cart WHERE customer_id = (SELECT id FROM customer_login WHERE username = ?)";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("❌ Error clearing cart: " + e.getMessage());
        }
    }
}
