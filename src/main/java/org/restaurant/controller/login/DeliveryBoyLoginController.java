package org.restaurant.controller.login;

import org.restaurant.model.login.DeliveryBoyLogin;
import org.restaurant.service.login.DeliveryBoyLoginService;
import org.restaurant.service.otp.OtpService;
import org.restaurant.model.order.Order;
import org.restaurant.repository.order.OrderRepository;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Collection;
import java.util.Scanner;


public class DeliveryBoyLoginController {

    private Scanner scanner;
    private DeliveryBoyLoginService deliveryBoyLoginService;
    private OtpService otpService = new OtpService();
    private OrderRepository orderRepository = OrderRepository.getInstance();

    public DeliveryBoyLoginController(Scanner scanner,
                                         DeliveryBoyLoginService deliveryBoyLoginService) {
        this.scanner = scanner;
        this.deliveryBoyLoginService = deliveryBoyLoginService;
    }

    public void start() {
        System.out.println("\n--- Delivery Boy Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (deliveryBoyLoginService.login(username, password) != null) {
            System.out.println("Login Successful! Welcome " + username);
            DeliveryBoyDashboardController dashboard = new DeliveryBoyDashboardController(scanner, username, deliveryBoyLoginService);
            dashboard.start();
        } else {
            System.out.println("❌ Invalid Delivery Boy credentials.");
        }
    }

    public void adminManageDelivery() {
        while (true) {
            System.out.println("\n--- Delivery Assignment Management ---");
            System.out.println("1. View All Delivery Boys");
            System.out.println("2. Assign Order to Delivery Boy");
            System.out.println("3. Add Delivery Boy");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewAllDeliveryBoys();
                case 2 -> assignOrder();
                case 3 -> addDeliveryBoy();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addDeliveryBoy() {
        System.out.println("\n--- Add Delivery Boy ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        // OTP WORKFLOW WITH FALLBACK
        System.out.println("Sending OTP to " + phone + "...");
        boolean smsSent = otpService.sendOtp(phone);
        
        if (smsSent) {
            System.out.print("Enter OTP: ");
            String enteredOtp = scanner.nextLine();
            if (!otpService.verifyOtp(phone, enteredOtp)) {
                System.out.println("Invalid OTP! Registration failed.");
                return;
            }
        } else {
            System.out.println("[WARNING] Twilio SMS failed. Bypassing OTP for testing...");
        }

        if (deliveryBoyLoginService.register(username, password, phone)) {
            System.out.println("Delivery Boy added successfully.");
        } else {
            System.out.println("Failed to add Delivery Boy. Username might be taken.");
        }
    }


    private void viewAllDeliveryBoys() {
        Collection<DeliveryBoyLogin> boys = deliveryBoyLoginService.getAllDeliveryBoys();
        if (boys.isEmpty()) {
            System.out.println("No delivery boys registered yet.");
            return;
        }
        System.out.println("\n--- All Delivery Boys ---");
        for (DeliveryBoyLogin boy : boys) {
            System.out.println("---------------------------");
            System.out.println("Username       : " + boy.getUsername());
            System.out.println("Assigned Orders: " + 
                (boy.getAssignedOrders().isEmpty() ? "None" : boy.getAssignedOrders()));
            System.out.println("Status         : " + 
                (boy.getAssignedOrders().isEmpty() ? "Available" : "Busy"));
        }
        System.out.println("---------------------------");
    }

    private void assignOrder() {
        // Step 1 — show available delivery boys
        Collection<DeliveryBoyLogin> boys = deliveryBoyLoginService.getAllDeliveryBoys();
        if (boys.isEmpty()) {
            System.out.println("No delivery boys available!");
            return;
        }

        System.out.println("\n--- Available Delivery Boys ---");
        for (DeliveryBoyLogin boy : boys) {
            if (boy.getAssignedOrders().isEmpty()) {
                System.out.println("- " + boy.getUsername());
            }
        }

        // Step 2 — enter delivery boy username
        System.out.print("\nEnter Delivery Boy Username: ");
        String username = scanner.nextLine();

        // Let's find orders waiting for pickup (READY)
        List<Order> preparedOrders = orderRepository.getAllOrders().stream()
                .filter(o -> o.getStatus().equals("READY"))
                .collect(Collectors.toList());

        if (preparedOrders.isEmpty()) {
            System.out.println("No Orders are 'READY' and waiting for delivery.");
            return;
        }

        System.out.println("\n--- Orders Ready for Pickup ---");
        for (Order o : preparedOrders) {
            System.out.println("[Order ID: " + o.getOrderId() + "] Total: " + o.getTotalAmount());
        }

        System.out.print("\nEnter Order ID to assign: ");
        String orderId = scanner.nextLine();

        // Step 4 — assign
        deliveryBoyLoginService.addAssignedOrder(username, orderId);
        System.out.println("Order " + orderId + " assigned to " + username + " successfully! Waiting for Delivery Boy to pick it up.");
    }

    private void viewAssignedOrders() {
        Collection<DeliveryBoyLogin> boys = deliveryBoyLoginService.getAllDeliveryBoys();
        if (boys.isEmpty()) {
            System.out.println("No delivery boys found.");
            return;
        }
        System.out.println("\n--- All Assigned Orders ---");
        for (DeliveryBoyLogin boy : boys) {
            if (!boy.getAssignedOrders().isEmpty()) {
                System.out.println("Delivery Boy : " + boy.getUsername());
                for (String order : boy.getAssignedOrders()) {
                    System.out.println("  Order: " + order);
                }
            }
        }
    }

    private void markAsDelivered() {
        System.out.print("Enter Delivery Boy Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Order to mark as delivered: ");
        String order = scanner.nextLine();

        // Move from assigned to history
        deliveryBoyLoginService.addDeliveryHistory(username, order);
        orderRepository.updateOrderStatus(order, "DELIVERED");
        System.out.println("Order marked as delivered and moved to history! Global order status is now DELIVERED.");
    }
}