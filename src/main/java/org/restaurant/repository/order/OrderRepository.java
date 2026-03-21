package org.restaurant.repository.order;

import org.restaurant.model.order.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {

    // key = orderId
    private Map<String, Order> orders = new HashMap<>();

    /** Persists a new order. */
    public void saveOrder(Order order) {
        orders.put(order.getOrderId(), order);
    }

    /** Returns all orders placed by a specific customer. */
    public List<Order> getOrdersByCustomer(String customerId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getCustomerId().equals(customerId)) {
                result.add(order);
            }
        }
        return result;
    }

    /** Returns every order across all customers (admin / manager view). */
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }
}