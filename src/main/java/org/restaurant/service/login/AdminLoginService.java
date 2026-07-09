package org.restaurant.service.login;

import org.restaurant.model.login.AdminLogin;
import org.restaurant.repository.login.AdminLoginRepo;

/**
 * AdminLoginService - Business logic layer
 * Demonstrates Separation of Concerns principle
 */
public class AdminLoginService {
    private AdminLoginRepo adminLoginRepo = new AdminLoginRepo();

    /**
     * Validates and performs admin login
     * @param username - the username
     * @param password - the password
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {
        // Validate inputs before processing
        if (!isValidInput(username, password)) {
            return false;
        }

        AdminLogin admin = adminLoginRepo.findByUsername(username);
        return admin != null && admin.getPassword().equals(password);
    }

    /**
     * Validates username and password inputs
     * @param username - the username to validate
     * @param password - the password to validate
     * @return true if both are valid
     */
    private boolean isValidInput(String username, String password) {
        // Check if username is purely numeric
        if (username != null && username.matches("^[0-9]+$")) {
            System.out.println("❌ Error: Only string value is allowed. Numbers are not permitted as username.");
            return false;
        }

        // Check basic validation
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("❌ Error: Username and password cannot be empty.");
            return false;
        }

        return true;
    }

    /**
     * Registers a new admin (optional functionality)
     * @param username - the username
     * @param password - the password
     * @return true if registration successful
     */
    public boolean registerAdmin(String username, String password) {
        try {
            AdminLogin newAdmin = new AdminLogin(username, password);
            // In a real scenario, add to repo
            System.out.println("✓ Admin registered successfully!");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Registration failed: " + e.getMessage());
            return false;
        }
    }
}