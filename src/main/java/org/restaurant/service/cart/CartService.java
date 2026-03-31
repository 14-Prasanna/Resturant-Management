package org.restaurant.service.cart;

import org.restaurant.model.cart.Cart;
import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.menu.MenuItem;
import org.restaurant.repository.cart.CartRepository;
import org.restaurant.service.menu.MenuService;

import java.util.List;

public class CartService {

    private CartRepository cartRepository = new CartRepository();
    private MenuService    menuService    = new MenuService();

    // ─────────────────────────────────────────────────────────────────────────
    // ADD TO CART
    // Looks up the menu item by productId, then persists it to DB.
    // If the item already exists, the repo increments quantity automatically.
    // ─────────────────────────────────────────────────────────────────────────
    public boolean addToCart(String username, String productId, int quantity) {
        MenuItem menuItem = menuService.getItemByProductId(productId);
        if (menuItem == null) {
            return false;   // product doesn't exist in menu
        }

        CartItem cartItem = new CartItem(
                menuItem.getProductId(),
                menuItem.getName(),
                menuItem.getMealTime(),
                menuItem.getPrice(),
                quantity
        );
        cartRepository.addItem(username, cartItem);
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE QUANTITY
    // Setting quantity to 0 removes the item.
    // ─────────────────────────────────────────────────────────────────────────
    public boolean updateCartItem(String username, String productId, int newQuantity) {
        return cartRepository.updateItemQuantity(username, productId, newQuantity);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // REMOVE ITEM
    // ─────────────────────────────────────────────────────────────────────────
    public boolean removeFromCart(String username, String productId) {
        return cartRepository.removeItem(username, productId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET ITEMS  –  returns the list of cart items for display
    // ─────────────────────────────────────────────────────────────────────────
    public List<CartItem> getCartItems(String username) {
        return cartRepository.getCart(username).getItems();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET CART OBJECT  –  needed by CheckoutService / OrderService
    // ─────────────────────────────────────────────────────────────────────────
    public Cart getCart(String username) {
        return cartRepository.getCart(username);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CLEAR CART  –  called after a successful order / checkout
    // ─────────────────────────────────────────────────────────────────────────
    public void clearCart(String username) {
        cartRepository.clearCart(username);
    }
}
