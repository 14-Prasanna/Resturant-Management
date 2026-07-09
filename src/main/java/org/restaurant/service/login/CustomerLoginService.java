package org.restaurant.service.login;

import org.mindrot.jbcrypt.BCrypt;
import org.restaurant.model.login.CustomerLogin;
import org.restaurant.repository.login.CustomerLoginRepo;

import java.util.Collection;

public class CustomerLoginService {

    private CustomerLoginRepo customerLoginRepo =
            CustomerLoginRepo.getInstance();

    // REGISTER
    public boolean register(String username,
                            String password,
                            String email,
                            String phone) {

        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty!");
            return false;
        }

        if (password == null || password.length() < 6) {
            System.out.println("Password must be at least 6 characters!");
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            System.out.println("Email cannot be empty!");
            return false;
        }

        if (phone == null || phone.length() != 10) {
            System.out.println("Phone number must be 10 digits!");
            return false;
        }

        String hashedPassword =
                BCrypt.hashpw(password, BCrypt.gensalt());

        return customerLoginRepo.register(
                username,
                hashedPassword,
                email,
                phone
        );
    }

    // LOGIN
    public CustomerLogin login(String username,
                               String password) {

        CustomerLogin customer =
                customerLoginRepo.findByUsername(username);

        if (customer != null &&
                BCrypt.checkpw(password,
                        customer.getPassword())) {

            return customer;
        }

        return null;
    }

    // GET ALL CUSTOMERS
    public Collection<CustomerLogin> getAllCustomers() {
        return customerLoginRepo.getAllCustomers();
    }
}