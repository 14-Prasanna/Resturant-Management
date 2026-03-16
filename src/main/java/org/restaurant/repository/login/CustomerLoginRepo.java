package org.restaurant.repository.login;

import org.restaurant.model.login.CustomerLogin;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class CustomerLoginRepo {

    // Key = username, Value = CustomerLogin object (which has ArrayList inside)
    private Map<String, CustomerLogin> customerMap = new HashMap<>();

    // Register new customer (any username & password)
    public boolean register(String username, String password) {
        if (customerMap.containsKey(username)) {
            System.out.println("Username already taken. Try another.");
            return false;
        }
        customerMap.put(username, new CustomerLogin(username, password));
        System.out.println("Registration successful!");
        return true;
    }

    // Find customer by username
    public CustomerLogin findByUsername(String username) {
        return customerMap.get(username);
    }

    // Get all customers (for Admin & Manager view)
    public Collection<CustomerLogin> getAllCustomers() {
        return customerMap.values();
    }
}