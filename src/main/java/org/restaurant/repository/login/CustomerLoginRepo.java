package org.restaurant.repository.login;

import org.restaurant.model.login.CustomerLogin;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class CustomerLoginRepo {
    private static CustomerLoginRepo instance;
    private Map<String, CustomerLogin> customerMap = new HashMap<>();

    private CustomerLoginRepo() {}

    public static CustomerLoginRepo getInstance() {
        if (instance == null) {
            instance = new CustomerLoginRepo();
        }
        return instance;
    }

    public boolean register(String username, String password) {
        if (customerMap.containsKey(username)) {
            System.out.println("Username already taken. Try another.");
            return false;
        }
        customerMap.put(username, new CustomerLogin(username, password));
        System.out.println("Registration successful!");
        return true;
    }

    public CustomerLogin findByUsername(String username) {
        return customerMap.get(username);
    }

    public Collection<CustomerLogin> getAllCustomers() {
        return customerMap.values();
    }
}