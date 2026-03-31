package org.restaurant.service.login;

import org.restaurant.model.login.CustomerLogin;
import org.restaurant.repository.login.CustomerLoginRepo;
import org.mindrot.jbcrypt.BCrypt;
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
        if (phone == null || phone.length() != 10) {
            System.out.println("Phone must be 10 digits!");
            return false;
        }

        // Hash password before storing
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return customerLoginRepo.register(username, hashedPassword, email, phone);
    }

    public void addReport(String username, String report) {
        CustomerLogin customer = customerLoginRepo.findByUsername(username);
        if (customer != null && BCrypt.checkpw(password, customer.getPassword())) {
            return customer;
        }
        System.out.println("Invalid credentials. Try again.");
        return null;
    }

    public Collection<CustomerLogin> getAllCustomers() {
        return customerLoginRepo.getAllCustomers();
    }
}