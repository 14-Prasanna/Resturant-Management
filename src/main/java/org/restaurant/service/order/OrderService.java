package org.restaurant.service.order;

import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.order.Order;
import org.restaurant.repository.order.OrderRepository;
import org.restaurant.service.cart.CartService;

import java.util.*;

public class OrderService {

    private OrderRepository orderRepository = OrderRepository.getInstance();
    private CartService cartService;

    public OrderService(CartService cartService) {
        this.cartService = cartService;
    }

    public String placeOrder(String customerId) {
        List<CartItem> cartItems = cartService.getCartItems(customerId);

        if (cartItems.isEmpty()) {
            return "Your cart is empty. Please add items before placing an order.";
        }

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

    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.getOrdersByCustomer(customerId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }
}