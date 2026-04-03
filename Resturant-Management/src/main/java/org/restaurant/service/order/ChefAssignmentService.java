package org.restaurant.service.order;

import org.restaurant.model.order.ChefAssignment;
import org.restaurant.repository.order.ChefAssignmentRepository;
import org.restaurant.repository.order.OrderRepository;
import java.util.List;

public class ChefAssignmentService {
    private ChefAssignmentRepository repository = new ChefAssignmentRepository();
    private OrderRepository orderRepository = OrderRepository.getInstance();

    public boolean assignOrderToChef(String chefUsername, String orderId) {
        boolean assigned = repository.assignOrder(chefUsername, orderId);
        if (assigned) {
            orderRepository.updateOrderStatus(orderId, "PREPARING");

            // --- Simulated 15 Minute Background Wait ---
            new Thread(() -> {
                try {
                    System.out.println("\n[System] Chef " + chefUsername + " is preparing order " + orderId + " (Simulating 15 minutes...)");
                    Thread.sleep(15000); // 15 seconds for testing
                    completeOrder(orderId);
                    System.out.print("\n[System] Order " + orderId + " is now READY! (15 mins elapsed)\nChoice: ");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

            return true;
        }
        return false;
    }

    // After 15 minutes, or manually via pressing a button
    public boolean completeOrder(String orderId) {
        // Also update the global orders table back to standard flow (e.g. prepared waiting for pickup)
        orderRepository.updateOrderStatus(orderId, "READY");
        return repository.updateAssignmentStatus(orderId, "PREPARED");
    }

    public List<ChefAssignment> getAssignments(String chefUsername) {
        return repository.getAssignmentsByChef(chefUsername);
    }
}
