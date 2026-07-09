package org.restaurant.util;

/**
 * Utility class for input validation
 * Demonstrates Encapsulation and Single Responsibility Principle (SRP)
 */
public class InputValidator {

    /**
     * Validates if a string contains only alphabetic characters and no numbers
     * @param input - the input string to validate
     * @return true if input contains only letters, false if it contains numbers or special chars
     */
    public static boolean isValidString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return input.matches("^[a-zA-Z0-9_]*$") && !input.matches(".*\\d+.*");
    }

    /**
     * Checks if string contains any digits
     * @param input - the input string to check
     * @return true if string contains digits
     */
    public static boolean containsNumbers(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return input.matches(".*\\d+.*");
    }

    /**
     * Validates username - letters, numbers, and underscore allowed (but no pure numbers)
     * @param username - the username to validate
     * @return true if valid username format
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty() || username.length() < 3) {
            return false;
        }
        return username.matches("^[a-zA-Z][a-zA-Z0-9_]*$"); // Must start with letter
    }

    /**
     * Validates password - should be at least 5 characters
     * @param password - the password to validate
     * @return true if valid password
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.length() >= 5;
    }
}
