package org.restaurant.service.order;

import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.order.Order;
import org.restaurant.repository.order.OrderRepository;
import org.restaurant.service.cart.CartService;

import java.util.*;

public class OrderService {

    private OrderRepository orderRepository = OrderRepository.getInstance();
    private CartService     cartService;

    public OrderService(CartService cartService) {
        this.cartService = cartService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PLACE ORDER  (called by CheckoutController after confirmation)
    // Returns "Order placed successfully|ORD-XXXXXXXX" on success,
    // or an error message string on failure.
    // ─────────────────────────────────────────────────────────────────────────
    public String placeOrder(String customerId, String checkoutId) {
        List<CartItem> cartItems = cartService.getCartItems(customerId);

        if (cartItems == null || cartItems.isEmpty()) {
            return "Your cart is empty. Please add items before placing an order.";
        }

        List<CartItem> snapshot = new ArrayList<>(cartItems);

        double total = snapshot.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order  order   = new Order(orderId, customerId, snapshot, total);
        order.setCheckoutId(checkoutId);

        boolean saved = orderRepository.saveOrder(order);
        if (!saved) {
            return "Failed to save order. Please try again.";
        }

        // Cart is cleared by CheckoutController only after payment succeeds.
        return "Order placed successfully|" + orderId;
    }

    // Backward-compatible overload (no checkoutId)
    public String placeOrder(String customerId) {
        return placeOrder(customerId, null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE ORDER STATUS  (PLACED → PROCESSING → DELIVERED / CANCELLED)
    // ─────────────────────────────────────────────────────────────────────────
    public boolean updateOrderStatus(String orderId, String newStatus) {
        return orderRepository.updateOrderStatus(orderId, newStatus);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // QUERIES
    // ─────────────────────────────────────────────────────────────────────────
    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.getOrdersByCustomer(customerId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }
}