package org.restaurant.controller.login;

import org.restaurant.controller.cart.CartController;
import org.restaurant.controller.menu.MenuController;
import org.restaurant.controller.order.OrderController;
import org.restaurant.model.login.CustomerLogin;
import org.restaurant.model.menu.MenuItem;
import org.restaurant.service.cart.CartService;
import org.restaurant.service.login.CustomerLoginService;
import org.restaurant.service.menu.MenuService;
import org.restaurant.service.order.OrderService;

import java.util.Collection;
import java.util.Scanner;

public class CustomerLoginController {

    private Scanner scanner;
    private CustomerLoginService customerLoginService;
    private MenuService  menuService   = new MenuService();
    private CartService  cartService   = new CartService();
    private OrderService orderService  = new OrderService(cartService);
    private CartController  cartController;
    private MenuController  menuController;
    private OrderController orderController;

    public CustomerLoginController(Scanner scanner, CustomerLoginService customerLoginService) {
        this.scanner              = scanner;
        this.customerLoginService = customerLoginService;
        this.cartController       = new CartController(scanner, cartService, menuService);
        this.menuController       = new MenuController(scanner);
        this.orderController      = new OrderController(scanner, orderService);
    }

    public void start() {
        while (true) {
            System.out.println("\n--- Customer Portal ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- Customer Register ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        boolean registered = customerLoginService.register(username, password);
        if (registered) {
            System.out.println("Registration successful! Please login to continue.");
        }
    }

    private void login() {
        System.out.println("\n--- Customer Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        CustomerLogin customer = customerLoginService.login(username, password);
        if (customer != null) {
            System.out.println("Welcome, " + customer.getUsername() + "!");
            customerDashboard(customer);
        } else {
            System.out.println("Invalid credentials. Try again.");
        }
    }

    private void loginWithCredentials(String username, String password) {
        CustomerLogin customer = customerLoginService.login(username, password);
        if (customer != null) {
            System.out.println("Welcome, " + customer.getUsername() + "!");
            customerDashboard(customer);
        }
    }

    private void customerDashboard(CustomerLogin customer) {
        while (true) {
            System.out.println("\n--- Customer Dashboard ---");
            System.out.println("Logged in as: " + customer.getUsername());
            System.out.println("1. View Menu");
            System.out.println("2. Add to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Update Cart Item");
            System.out.println("5. Remove Item from Cart");
            System.out.println("6. Place Order");
            System.out.println("7. View My Past Orders");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewMenuAsCustomer();
                case 2 -> cartController.addToCart(customer.getUsername(), menuController.selectMealTime());
                case 3 -> cartController.viewCart(customer.getUsername());
                case 4 -> cartController.updateCartItem(customer.getUsername());
                case 5 -> cartController.removeFromCart(customer.getUsername());
                case 6 -> orderController.placeOrder(customer.getUsername());
                case 7 -> orderController.viewMyOrders(customer.getUsername());
                case 0 -> {
                    System.out.println("Logged out. Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewMenuAsCustomer() {
        System.out.println("\nView menu for which meal time?");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        System.out.println("3. Night");
        System.out.println("4. Snacks");
        System.out.println("5. All");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String filter = switch (choice) {
            case 1 -> "Morning";
            case 2 -> "Afternoon";
            case 3 -> "Night";
            case 4 -> "Snacks";
            default -> "All";
        };

        displayMenu(filter);
    }

    private void displayMenu(String filter) {
        Collection<MenuItem> items = menuService.getMenuItemsByMealTime(filter);

        if (items.isEmpty()) {
            System.out.println("No items available for " + filter);
            return;
        }

        System.out.println("\n--- " + filter.toUpperCase() + " MENU ---");
        int i = 1;
        for (MenuItem item : items) {
            System.out.println(i++ + ". [" + item.getMealTime() + "] "
                    + item.getName()
                    + " | " + item.getDescription()
                    + " | Price: ₹" + item.getPrice()
                    + " | Rating: " + item.getRating());
        }
    }

    private void viewPastOrders(CustomerLogin customer) {
        System.out.println("\n--- My Past Orders ---");
        if (customer.getPastOrders().isEmpty()) {
            System.out.println("No orders yet.");
        } else {
            for (String order : customer.getPastOrders()) {
                System.out.println("- " + order);
            }
        }
    }

    private void viewReports(CustomerLogin customer) {
        System.out.println("\n--- My Reports ---");
        if (customer.getReports().isEmpty()) {
            System.out.println("No reports yet.");
        } else {
            for (String report : customer.getReports()) {
                System.out.println("- " + report);
            }
        }
    }
}