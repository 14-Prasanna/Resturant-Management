package org.restaurant.controller.cart;

import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.menu.MenuItem;
import org.restaurant.service.cart.CartService;
import org.restaurant.service.menu.MenuService;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class CartController {

    // -------------------------------------------------------------------------
    // Tax and delivery charge constants
    // -------------------------------------------------------------------------
    private static final double TAX_RATE             = 0.05;   // 5 % GST
    private static final double DELIVERY_CHARGE      = 40.00;  // flat ₹40
    private static final double FREE_DELIVERY_ABOVE  = 500.00; // free if subtotal > ₹500

    private Scanner     scanner;
    private CartService cartService;
    private MenuService menuService;

    public CartController(Scanner scanner, CartService cartService, MenuService menuService) {
        this.scanner     = scanner;
        this.cartService = cartService;
        this.menuService = menuService;
    }

    // =========================================================================
    // ADD TO CART
    // =========================================================================

    /**
     * Displays the filtered menu, then prompts the customer to enter a
     * Product ID and quantity to add to their cart.
     */
    public void addToCart(String customerId, String mealTimeFilter) {
        displayMenuForCategory(mealTimeFilter);

        System.out.print("\nEnter Product ID to add to cart: ");
        String productId = scanner.nextLine().trim();

        System.out.print("Enter quantity: ");
        int quantity = readPositiveInt();
        if (quantity == -1) return;

        boolean added = cartService.addToCart(customerId, productId, quantity);
        System.out.println(added
                ? "Item added to cart successfully!"
                : "Product ID not found in menu. Please try again.");
    }

    // =========================================================================
    // UPDATE CART ITEM
    // =========================================================================

    /**
     * Displays the current cart, then lets the customer change the quantity
     * of any item. Entering 0 removes the item from the cart.
     */
    public void updateCartItem(String customerId) {
        List<CartItem> items = cartService.getCartItems(customerId);
        if (items.isEmpty()) {
            System.out.println("Your cart is empty. Nothing to update.");
            return;
        }

        viewCart(customerId);   // show current cart first

        System.out.print("\nEnter Product ID to update: ");
        String productId = scanner.nextLine().trim();

        System.out.print("Enter new quantity (enter 0 to remove item): ");
        int newQuantity = readNonNegativeInt();
        if (newQuantity == -1) return;

        boolean updated = cartService.updateCartItem(customerId, productId, newQuantity);
        if (updated) {
            System.out.println(newQuantity == 0
                    ? "Item removed from cart (quantity set to 0)."
                    : "Cart item updated successfully! New quantity: " + newQuantity);
        } else {
            System.out.println("Product ID not found in your cart.");
        }
    }

    // =========================================================================
    // REMOVE FROM CART
    // =========================================================================

    /** Shows the current cart and asks the customer which product to remove. */
    public void removeFromCart(String customerId) {
        List<CartItem> items = cartService.getCartItems(customerId);
        if (items.isEmpty()) {
            System.out.println("Your cart is empty. Nothing to remove.");
            return;
        }

        viewCart(customerId);

        System.out.print("\nEnter Product ID to remove: ");
        String productId = scanner.nextLine().trim();

        boolean removed = cartService.removeFromCart(customerId, productId);
        System.out.println(removed
                ? "Item removed from cart."
                : "Product ID not found in your cart.");
    }

    // =========================================================================
    // VIEW CART  (with tax + delivery breakdown)
    // =========================================================================

    /**
     * Prints all cart items followed by a full cost breakdown:
     *   Subtotal → Tax (5%) → Delivery Charge → Grand Total
     */
    public void viewCart(String customerId) {
        List<CartItem> items = cartService.getCartItems(customerId);

        if (items.isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }

        // ── Item table ──────────────────────────────────────────────────────
        System.out.println("\n--- YOUR CART ---");
        System.out.printf("%-12s %-20s %-12s %8s %5s %12s%n",
                "Product ID", "Name", "Meal Time", "Price", "Qty", "Subtotal");
        System.out.println("-".repeat(75));

        double subtotal = 0;
        for (CartItem item : items) {
            System.out.printf("%-12s %-20s %-12s %8.2f %5d %12.2f%n",
                    item.getProductId(),
                    item.getName(),
                    item.getMealTime(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getTotalPrice());
            subtotal += item.getTotalPrice();
        }

        // ── Cost breakdown ───────────────────────────────────────────────────
        double tax              = subtotal * TAX_RATE;
        double delivery         = (subtotal > FREE_DELIVERY_ABOVE) ? 0.0 : DELIVERY_CHARGE;
        double grandTotal       = subtotal + tax + delivery;

        System.out.println("-".repeat(75));
        System.out.printf("%-60s ₹%10.2f%n", "Subtotal",                       subtotal);
        System.out.printf("%-60s ₹%10.2f%n", "Tax (GST 5%)",                   tax);
        System.out.printf("%-60s ₹%10.2f%n",
                delivery == 0
                        ? "Delivery Charge (FREE – order above ₹" + FREE_DELIVERY_ABOVE + ")"
                        : "Delivery Charge",
                delivery);
        System.out.println("=".repeat(75));
        System.out.printf("%-60s ₹%10.2f%n", "GRAND TOTAL",                    grandTotal);
        System.out.println("=".repeat(75));
    }

    // =========================================================================
    // HELPER — display menu by category
    // =========================================================================

    private void displayMenuForCategory(String mealTimeFilter) {
        Collection<MenuItem> items = menuService.getMenuItemsByMealTime(mealTimeFilter);

        if (items.isEmpty()) {
            System.out.println("No items available for category: " + mealTimeFilter);
            return;
        }

        System.out.println("\n--- " + mealTimeFilter.toUpperCase() + " MENU ---");
        System.out.printf("%-12s %-20s %-30s %8s %6s%n",
                "Product ID", "Name", "Description", "Price", "Rating");
        System.out.println("-".repeat(80));
        for (MenuItem item : items) {
            System.out.printf("%-12s %-20s %-30s %8.2f %6.1f%n",
                    item.getProductId(),
                    item.getName(),
                    item.getDescription(),
                    item.getPrice(),
                    item.getRating());
        }
    }

    // =========================================================================
    // INPUT HELPER
    // =========================================================================

    /** Reads a positive integer (>0). Returns -1 on invalid input. */
    private int readPositiveInt() {
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            if (val <= 0) { System.out.println("Quantity must be greater than zero."); return -1; }
            return val;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }

    /** Reads a non-negative integer (>=0). Returns -1 on invalid input. */
    private int readNonNegativeInt() {
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            if (val < 0) { System.out.println("Quantity cannot be negative."); return -1; }
            return val;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }
}
