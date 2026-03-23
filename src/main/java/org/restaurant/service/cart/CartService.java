package org.restaurant.service.cart;

import org.restaurant.model.cart.Cart;
import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.menu.MenuItem;
import org.restaurant.repository.cart.CartRepository;
import org.restaurant.service.menu.MenuService;

import java.util.List;

public class CartService {

    private CartRepository cartRepository = new CartRepository();
    private MenuService menuService       = new MenuService();

    /**
     * Looks up the menu item by productId and adds it to the customer's cart.
     *
     * @return true if the item was found in the menu and added; false if the productId is invalid.
     */
    public boolean addToCart(String customerId, String productId, int quantity) {
        MenuItem menuItem = menuService.getItemByProductId(productId);
        if (menuItem == null) {
            return false;
        }

        CartItem cartItem = new CartItem(
                menuItem.getProductId(),
                menuItem.getName(),
                menuItem.getMealTime(),
                menuItem.getPrice(),
                quantity
        );
        cartRepository.addItem(customerId, cartItem);
        return true;
    }

    /**
     * Updates the quantity of an already-added cart item.
     * Entering 0 removes the item entirely.
     *
     * @return true  — item found and quantity updated
     *         false — productId not found in the customer's cart
     */
    public boolean updateCartItem(String customerId, String productId, int newQuantity) {
        return cartRepository.updateItemQuantity(customerId, productId, newQuantity);
    }

    /**
     * Removes an item from the cart.
     *
     * @return true if removed; false if the product was not in the cart.
     */
    public boolean removeFromCart(String customerId, String productId) {
        return cartRepository.removeItem(customerId, productId);
    }

    /** Returns all items currently in the customer's cart. */
    public List<CartItem> getCartItems(String customerId) {
        Cart cart = cartRepository.getOrCreateCart(customerId);
        return cart.getItems();
    }

    /** Clears the cart — called by OrderService after a successful order. */
    public void clearCart(String customerId) {
        cartRepository.clearCart(customerId);
    }

    /** Convenience: returns the cart object (needed by OrderService to snapshot items). */
    public Cart getCart(String customerId) {
        return cartRepository.getOrCreateCart(customerId);
    }
}
