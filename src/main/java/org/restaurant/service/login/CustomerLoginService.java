package org.restaurant.service.login;

import org.restaurant.model.login.CustomerLogin;
import org.restaurant.repository.login.CustomerLoginRepo;
import java.util.Collection;

public class CustomerLoginService {
    private CustomerLoginRepo customerLoginRepo = CustomerLoginRepo.getInstance();

    public boolean register(String username, String password) {
        return customerLoginRepo.register(username, password);
    }

    public CustomerLogin login(String username, String password) {
        CustomerLogin customer = customerLoginRepo.findByUsername(username);
        if (customer != null && customer.getPassword().equals(password)) {
            return customer;
        }
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