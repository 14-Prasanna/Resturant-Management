package org.restaurant.repository.order;

import org.restaurant.model.order.Order;
import java.util.*;

public class OrderRepository {

    private static OrderRepository instance;
    private Map<String, Order> orders = new HashMap<>();

    private OrderRepository() {}

    public static OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    public void saveOrder(Order order) {
        orders.put(order.getOrderId(), order);
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getCustomerId().equals(customerId)) {
                result.add(order);
            }
        }
        return result;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }
}