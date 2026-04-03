package org.restaurant.controller.login;

import org.restaurant.model.login.ChefLogin;
import org.restaurant.model.order.Order;
import org.restaurant.repository.order.OrderRepository;
import org.restaurant.service.login.ChefLoginService;
import org.restaurant.service.order.ChefAssignmentService;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ChefController {
    private Scanner scanner;
    private ChefLoginService chefLoginService = new ChefLoginService();
    private ChefAssignmentService assignmentService = new ChefAssignmentService();
    private OrderRepository orderRepository = OrderRepository.getInstance();

    public ChefController(Scanner scanner) {
        this.scanner = scanner;
    }

    public void manageChefs() {
        while (true) {
            System.out.println("\n--- Manage Chefs ---");
            System.out.println("1. Add Chef");
            System.out.println("2. Delete Chef");
            System.out.println("3. View All Chefs");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addChef();
                case 2 -> deleteChef();
                case 3 -> viewAllChefs();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addChef() {
        System.out.print("Enter new Chef Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (chefLoginService.addChef(username, password)) {
            System.out.println("Chef " + username + " added successfully!");
        }
    }

    private void deleteChef() {
        System.out.print("Enter Chef Username to delete: ");
        String username = scanner.nextLine();

        if (chefLoginService.deleteChef(username)) {
            System.out.println("Chef deleted successfully.");
        } else {
            System.out.println("Failed to delete Chef. They may not exist or have active orders.");
        }
    }

    private void viewAllChefs() {
        List<ChefLogin> chefs = chefLoginService.getAllChefs();
        if (chefs.isEmpty()) {
            System.out.println("No Chefs hired yet.");
            return;
        }
        System.out.println("\n--- All Chefs ---");
        for (ChefLogin c : chefs) {
            System.out.println("- " + c.getUsername());
        }
    }

    public void assignOrdersToChef() {
        System.out.println("\n--- Assign Order to Chef ---");
        List<Order> placedOrders = orderRepository.getAllOrders().stream()
                .filter(o -> o.getStatus().equals("PROCESSING") || o.getStatus().equals("PLACED"))
                .collect(Collectors.toList());

        if (placedOrders.isEmpty()) {
            System.out.println("No Orders waiting for a Chef.");
            return;
        }

        System.out.println("Orders waiting for preparation:");
        for (Order o : placedOrders) {
            System.out.println("[Order ID: " + o.getOrderId() + "] Total: ₹" + o.getTotalAmount());
        }

        System.out.print("Enter Order ID to assign: ");
        String orderId = scanner.nextLine();

        List<ChefLogin> chefs = chefLoginService.getAllChefs();
        if (chefs.isEmpty()) {
            System.out.println("No Chefs available to cook!");
            return;
        }
        
        System.out.println("Available Chefs:");
        for (ChefLogin c : chefs) {
            System.out.println("- " + c.getUsername());
        }

        System.out.print("Enter Chef Username to assign this order to: ");
        String chefUsername = scanner.nextLine();

        if (assignmentService.assignOrderToChef(chefUsername, orderId)) {
            System.out.println("Successfully assigned Order " + orderId + " to Chef " + chefUsername + "!");
        } else {
            System.out.println("Failed to assign order.");
        }
    }
}
