package org.restaurant.service.login;

import org.restaurant.model.login.CustomerLogin;
import org.restaurant.repository.login.CustomerLoginRepo;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Collection;

public class CustomerLoginService {
    private CustomerLoginRepo customerLoginRepo = new CustomerLoginRepo();

    public boolean register(String username, String password, String email, String phone) {

        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty!");
            return false;
        }
        if (password == null || password.length() < 6) {
            System.out.println("Password must be at least 6 characters!");
            return false;
        }
        if (email == null || !email.contains("@")) {
            System.out.println("Invalid email address!");
            return false;
        }
        if (phone == null || phone.length() != 10) {
            System.out.println("Phone must be 10 digits!");
            return false;
        }

        // Hash password before storing
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return customerLoginRepo.register(username, hashedPassword, email, phone);
    }

    public CustomerLogin login(String username, String password) {
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