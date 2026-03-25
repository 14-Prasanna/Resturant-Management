package org.restaurant.controller.login;

import org.restaurant.controller.menu.MenuController;
import org.restaurant.model.login.CustomerLogin;
import org.restaurant.model.login.DeliveryBoyLogin;
import org.restaurant.model.order.Order;
import org.restaurant.service.login.AdminLoginService;
import org.restaurant.service.login.CustomerLoginService;
import org.restaurant.service.login.DeliveryBoyLoginService;
import org.restaurant.service.order.OrderService;
import java.util.List;
import java.util.Scanner;

public class AdminLoginController {
    private Scanner scanner;
    private AdminLoginService adminLoginService = new AdminLoginService();
    private CustomerLoginService customerLoginService;
    private DeliveryBoyLoginService deliveryBoyLoginService;
    private OrderService orderService;

    public AdminLoginController(Scanner scanner,
                                CustomerLoginService customerLoginService,
                                DeliveryBoyLoginService deliveryBoyLoginService,
                                OrderService orderService) {
        this.scanner                 = scanner;
        this.customerLoginService    = customerLoginService;
        this.deliveryBoyLoginService = deliveryBoyLoginService;
        this.orderService            = orderService;
    }

    public void start() {
        System.out.println("\n--- Admin Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        boolean success = adminLoginService.login(username, password);

        if (success) {
            System.out.println("Login successful! Welcome, " + username);
            adminDashboard(username);
        } else {
            System.out.println("Invalid credentials. Returning to main menu...");
        }
    }

    private void adminDashboard(String username) {
        while (true) {
            System.out.println("\n--- Admin Dashboard ---");
            System.out.println("Logged in as: " + username);
            System.out.println("1. Manage Menu");
            System.out.println("2. View All Customers");
            System.out.println("3. View All Delivery Boys");
            System.out.println("4. View All Orders");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    MenuController menuController = new MenuController(scanner);
                    menuController.showMenu();
                }
                case 2 -> viewAllCustomers();
                case 3 -> viewAllDeliveryBoys();
                case 4 -> viewAllOrders();
                case 0 -> {
                    System.out.println("Logged out successfully. Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewAllCustomers() {
        System.out.println("\n--- All Customers ---");
        if (customerLoginService.getAllCustomers().isEmpty()) {
            System.out.println("No customers registered yet.");
            return;
        }
        for (CustomerLogin c : customerLoginService.getAllCustomers()) {
            System.out.println("---------------------------");
            System.out.println("Username : " + c.getUsername());

            List<Order> orders = orderService.getOrdersByCustomer(c.getUsername());
            if (orders.isEmpty()) {
                System.out.println("Orders   : No orders");
            } else {
                System.out.println("Orders   :");
                for (Order o : orders) {
                    System.out.println("  Order ID : " + o.getOrderId()
                            + " | Total: ₹" + o.getTotalAmount()
                            + " | Status: "  + o.getStatus()
                            + " | Placed At: " + o.getPlacedAt());
                }
            }
            System.out.println("Reports  : " + (c.getReports().isEmpty() ? "No reports" : c.getReports()));
        }
        System.out.println("---------------------------");
    }

    private void viewAllDeliveryBoys() {
        System.out.println("\n--- All Delivery Boys ---");
        if (deliveryBoyLoginService.getAllDeliveryBoys().isEmpty()) {
            System.out.println("No delivery boys registered yet.");
            return;
        }
        for (DeliveryBoyLogin d : deliveryBoyLoginService.getAllDeliveryBoys()) {
            System.out.println("---------------------------");
            System.out.println("Username         : " + d.getUsername());
            System.out.println("Assigned Orders  : " + (d.getAssignedOrders().isEmpty()  ? "No assigned orders"  : d.getAssignedOrders()));
            System.out.println("Delivery History : " + (d.getDeliveryHistory().isEmpty() ? "No delivery history" : d.getDeliveryHistory()));
        }
        System.out.println("---------------------------");
    }

    private void viewAllOrders() {
        System.out.println("\n--- All Orders ---");
        List<Order> allOrders = orderService.getAllOrders();
        if (allOrders.isEmpty()) {
            System.out.println("No orders placed yet.");
            return;
        }
        for (Order o : allOrders) {
            System.out.println("---------------------------");
            System.out.println("Order ID   : " + o.getOrderId());
            System.out.println("Customer   : " + o.getCustomerId());
            System.out.println("Total      : ₹" + o.getTotalAmount());
            System.out.println("Status     : " + o.getStatus());
            System.out.println("Placed At  : " + o.getPlacedAt());
            System.out.println("Items      :");
            o.getItems().forEach(item ->
                    System.out.println("  - " + item.getName()
                            + " x" + item.getQuantity()
                            + " | ₹" + item.getTotalPrice()));
        }
        System.out.println("---------------------------");
    }
}