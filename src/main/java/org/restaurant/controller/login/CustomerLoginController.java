package org.restaurant.controller.login;

import org.restaurant.controller.cart.CartController;
import org.restaurant.controller.checkout.CheckoutController;
import org.restaurant.controller.menu.MenuController;
import org.restaurant.controller.order.OrderController;
import org.restaurant.controller.payment.PaymentController;
import org.restaurant.model.login.CustomerLogin;
import org.restaurant.service.cart.CartService;
import org.restaurant.service.checkout.CheckoutService;
import org.restaurant.service.login.CustomerLoginService;
import org.restaurant.service.menu.MenuService;
import org.restaurant.service.order.OrderService;
import org.restaurant.service.otp.OtpService;
import org.restaurant.service.payment.PaymentService;
import org.restaurant.service.discount.DiscountService;

import java.util.Scanner;

public class CustomerLoginController {

    private Scanner              scanner;
    private CustomerLoginService customerLoginService;
    private OtpService           otpService = new OtpService();

    // Services (declared ONCE)
    private MenuService     menuService     = new MenuService();
    private CartService     cartService     = new CartService();
    private OrderService    orderService    = new OrderService(cartService);
    private CheckoutService checkoutService = new CheckoutService(cartService);
    private PaymentService  paymentService  = new PaymentService();
    private DiscountService discountService = new DiscountService();

    // Controllers (declared ONCE)
    private CartController     cartController;
    private MenuController     menuController;
    private OrderController    orderController;
    private PaymentController  paymentController;
    private CheckoutController checkoutController;

    public CustomerLoginController(Scanner scanner, CustomerLoginService customerLoginService) {
        this.scanner              = scanner;
        this.customerLoginService = customerLoginService;

        this.cartController    = new CartController(scanner, cartService, menuService);
        this.menuController    = new MenuController(scanner);
        this.orderController   = new OrderController(scanner, orderService);

        // PaymentController must be created before CheckoutController (it is injected)
        this.paymentController  = new PaymentController(scanner, paymentService);
        this.checkoutController = new CheckoutController(scanner, checkoutService,
                orderService, paymentController, paymentService, cartService, discountService);
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
        System.out.print("Enter Full Name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
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
            System.out.println("[WARNING] Twilio SMS failed (Setup .env to fix). Bypassing OTP for testing...");
        }

        boolean registered = customerLoginService.register(username, password, fullName, email, phone);
        if (registered) {
            CustomerLogin customer = customerLoginService.login(username, password);
            if (customer != null) {
                System.out.println("Welcome, " + customer.getUsername() + "!");
                customerDashboard(customer);
            }
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
            System.out.println("6. Checkout & Pay");
            System.out.println("7. View My Past Orders");
            System.out.println("8. Submit Feedback / Report Issue");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> menuController.displayMenu();
                case 2 -> cartController.addToCart(customer.getUsername(), menuController.selectSingleMealTime());
                case 3 -> cartController.viewCart(customer.getUsername());
                case 4 -> cartController.updateCartItem(customer.getUsername());
                case 5 -> cartController.removeFromCart(customer.getUsername());
                case 6 -> checkoutController.startCheckout(customer.getUsername());
                case 7 -> orderController.viewMyOrders(customer.getUsername());
                case 8 -> submitFeedback(customer.getUsername());
                case 0 -> {
                    System.out.println("Logged out. Returning to main menu...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void submitFeedback(String username) {
        System.out.println("\n--- Submit Feedback / Report Issue ---");
        System.out.print("Provide an Order ID (or press Enter to skip): ");
        String orderId = scanner.nextLine();
        
        System.out.print("Enter your message/feedback: ");
        String message = scanner.nextLine();

        if (customerLoginService.addFeedback(username, orderId, message)) {
            System.out.println("Thank you for your feedback! It has been recorded.");
        } else {
            System.out.println("Failed to submit feedback.");
        }
    }
}