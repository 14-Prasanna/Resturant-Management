package org.restaurant.controller.login;

import org.restaurant.model.order.ChefAssignment;
import org.restaurant.service.login.ChefLoginService;
import org.restaurant.service.order.ChefAssignmentService;

import java.util.List;
import java.util.Scanner;

public class ChefDashboardController {
    private Scanner scanner;
    private ChefLoginService loginService = new ChefLoginService();
    private ChefAssignmentService assignmentService = new ChefAssignmentService();

    public ChefDashboardController(Scanner scanner) {
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("\n--- Chef Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (loginService.login(username, password)) {
            System.out.println("Login Successful! Welcome Chef " + username);
            chefDashboard(username);
        } else {
            System.out.println("Invalid Chef credentials.");
        }
    }

    private void chefDashboard(String username) {
        while (true) {
            System.out.println("\n--- Chef Dashboard (" + username + ") ---");
            System.out.println("1. View My Active Orders (PREPARING)");
            System.out.println("2. Complete Order Preparation (Mark PREPARED)");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewActiveOrders(username);
                case 2 -> completeOrder(username);
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewActiveOrders(String username) {
        List<ChefAssignment> orders = assignmentService.getAssignments(username);
        boolean found = false;
        System.out.println("\n--- Your Active Orders ---");
        for (ChefAssignment a : orders) {
            if (a.getStatus().equals("PREPARING")) {
                System.out.println("[Order ID: " + a.getOrderId() + "]  Assigned: " + a.getAssignedAt() + " | Wait: 15 mins");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No active orders. You can relax!");
        }
    }

    private void completeOrder(String username) {
        System.out.print("Enter Order ID you have finished preparing: ");
        String orderId = scanner.nextLine();
        
        System.out.println("⏳ Simulating preparation check...");
        
        if (assignmentService.completeOrder(orderId)) {
            System.out.println("✅ Order " + orderId + " is now marked as PREPARED and ready for pickup!");
        } else {
            System.out.println("❌ Failed to complete order. (Maybe it does not exist)");
        }
    }
}
