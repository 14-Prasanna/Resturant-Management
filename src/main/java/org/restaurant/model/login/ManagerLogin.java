package org.restaurant.model.login;

import org.restaurant.util.InputValidator;

/**
 * ManagerLogin Model Class
 * Demonstrates Encapsulation principle - private fields with validation
 */
public class ManagerLogin {
    private String username;
    private String password;

    public ManagerLogin(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    /**
     * Setter for username with validation
     * @param username - the username to set
     * @throws IllegalArgumentException if username contains only numbers
     */
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        
        // Validate that username is not purely numeric
        if (username.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("❌ Only string value is allowed. Numbers are not permitted as username.");
        }
        
        if (!InputValidator.isValidUsername(username)) {
            throw new IllegalArgumentException("Username must start with a letter and contain only alphanumeric characters");
        }
        
        this.username = username;
    }

    /**
     * Setter for password with validation
     * @param password - the password to set
     * @throws IllegalArgumentException if password is invalid
     */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        if (!InputValidator.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 5 characters long");
        }
        
        this.password = password;
    }

    public String getUsername() { 
        return username; 
    }

    public String getPassword() { 
        return password; 
    }
}