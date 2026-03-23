package org.restaurant.service.order;

import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.order.Order;
import org.restaurant.repository.order.OrderRepository;
import org.restaurant.service.cart.CartService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderService {

    private OrderRepository orderRepository = new OrderRepository();
    private CartService cartService;        // injected so both share the same CartService instance

    public OrderService(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Places an order for the customer:
     *  1. Validates that the cart is not empty.
     *  2. Snapshots the cart items into a new Order.
     *  3. Saves the order.
     *  4. Clears the cart.
     *
     * @return "Order placed successfully" on success, or an error message.
     */
    public String placeOrder(String customerId) {
        List<CartItem> cartItems = cartService.getCartItems(customerId);

        if (cartItems.isEmpty()) {
            return "Your cart is empty. Please add items before placing an order.";
        }

        // Snapshot — copy items so clearing the cart doesn't affect the stored order
        List<CartItem> snapshot = new ArrayList<>(cartItems);

        double total = snapshot.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = new Order(orderId, customerId, snapshot, total);

        orderRepository.saveOrder(order);
        cartService.clearCart(customerId);

        return "Order placed successfully";
    }

    /** Returns all past orders for a customer. */
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.getOrdersByCustomer(customerId);
    }
}