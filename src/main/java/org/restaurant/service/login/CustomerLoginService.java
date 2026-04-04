package org.restaurant.service.login;

import org.mindrot.jbcrypt.BCrypt;
import org.restaurant.model.login.CustomerLogin;
import org.restaurant.repository.login.CustomerLoginRepo;

import java.util.Collection;

public class CustomerLoginService {
    private CustomerLoginRepo customerLoginRepo = CustomerLoginRepo.getInstance();

    public boolean register(String username, String password, String fullName, String email, String phone) {
        // Hash password before storing
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return customerLoginRepo.register(username, hashedPassword, fullName, email, phone);
    }

    public CustomerLogin login(String username, String password) {
        CustomerLogin customer = customerLoginRepo.findByUsername(username);
        if (customer != null && BCrypt.checkpw(password, customer.getPassword())) {
            return customer;
        }
        System.out.println("Invalid credentials. Try again.");
        return null;
    }

    public void addOrder(String username, String order) {
        CustomerLogin customer = customerLoginRepo.findByUsername(username);
        if (customer != null) {
            customer.addOrder(order);
        }
    }

    public boolean addFeedback(String username, String orderId, String message) {
        return customerLoginRepo.addFeedback(username, orderId, message);
    }

    public Collection<CustomerLogin> getAllCustomers() {
        return customerLoginRepo.getAllCustomers();
    }
}
