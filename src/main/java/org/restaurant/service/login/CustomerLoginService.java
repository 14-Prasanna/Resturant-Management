package org.restaurant.service.login;

import org.restaurant.model.login.CustomerLogin;
import org.restaurant.repository.login.CustomerLoginRepo;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;

public class CustomerLoginService {

    private CustomerLoginRepo customerLoginRepo = CustomerLoginRepo.getInstance();

    // Register with email + phone + hashed password
    public boolean register(String username, String password, String email, String phone) {
        if (phone == null || phone.length() != 10) {
            System.out.println("Phone must be 10 digits!");
            return false;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return customerLoginRepo.register(username, hashedPassword, email, phone);
    }

    // Login with BCrypt check
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

    public void addReport(String username, String report) {
        CustomerLogin customer = customerLoginRepo.findByUsername(username);
        if (customer != null) {
            customer.addReport(report);
        }
    }

    public Collection<CustomerLogin> getAllCustomers() {
        return customerLoginRepo.getAllCustomers();
    }
}