package org.restaurant.repository.cart;

import org.restaurant.model.cart.Cart;
import org.restaurant.model.cart.Cart.CartItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CartRepository {

    // One cart per customer — key is customerId (username)
    private Map<String, Cart> carts = new HashMap<>();

    /** Returns the cart for this customer, creating one if it does not exist yet. */
    public Cart getOrCreateCart(String customerId) {
        carts.putIfAbsent(customerId, new Cart(customerId));
        return carts.get(customerId);
    }

    /**
     * Adds an item to the customer's cart.
     * If the same productId already exists, increments its quantity instead.
     */
    public void addItem(String customerId, CartItem newItem) {
        Cart cart = getOrCreateCart(customerId);

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(newItem.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + newItem.getQuantity());
        } else {
            cart.getItems().add(newItem);
        }
    }

    /**
     * Updates the quantity of an existing cart item to an exact new value.
     * If newQuantity is 0, the item is removed automatically.
     *
     * @return true  — item found and updated (or removed when qty = 0)
     *         false — productId not found in this customer's cart
     */
    public boolean updateItemQuantity(String customerId, String productId, int newQuantity) {
        Cart cart = getOrCreateCart(customerId);

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst();

        if (existing.isEmpty()) return false;

        if (newQuantity <= 0) {
            // treat qty = 0 as a remove
            cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        } else {
            existing.get().setQuantity(newQuantity);
        }
        return true;
    }

    /**
     * Removes an item from the cart by productId.
     *
     * @return true if an item was found and removed, false otherwise.
     */
    public boolean removeItem(String customerId, String productId) {
        Cart cart = getOrCreateCart(customerId);
        return cart.getItems().removeIf(i -> i.getProductId().equals(productId));
    }

    /** Empties the customer's cart (called after order is placed). */
    public void clearCart(String customerId) {
        carts.put(customerId, new Cart(customerId));
    }
}
