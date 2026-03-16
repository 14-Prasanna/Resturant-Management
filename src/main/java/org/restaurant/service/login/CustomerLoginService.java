package org.restaurant.service.login;

import org.restaurant.model.login.CustomerLogin;
import org.restaurant.repository.login.CustomerLoginRepo;
import java.util.Collection;

public class CustomerLoginService {

    private CustomerLoginRepo customerLoginRepo = new CustomerLoginRepo();

    // Register
    public boolean register(String username, String password) {
        return customerLoginRepo.register(username, password);
    }

    // Login
    public CustomerLogin login(String username, String password) {
        CustomerLogin customer = customerLoginRepo.findByUsername(username);
        if (customer != null && customer.getPassword().equals(password)) {
            return customer;
        }
        return null;
    }

    // Add order to customer
    public void addOrder(String username, String order) {
        CustomerLogin customer = customerLoginRepo.findByUsername(username);
        if (customer != null) {
            customer.addOrder(order);
        }
    }

    // Add report to customer
    public void addReport(String username, String report) {
        CustomerLogin customer = customerLoginRepo.findByUsername(username);
        if (customer != null) {
            customer.addReport(report);
        }
    }

    // Get all customers (Admin & Manager use)
    public Collection<CustomerLogin> getAllCustomers() {
        return customerLoginRepo.getAllCustomers();
    }
}