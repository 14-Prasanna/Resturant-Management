package org.restaurant.repository.login;

import org.restaurant.model.login.AdminLogin;
import java.util.ArrayList;
import java.util.List;

public class AdminLoginRepo {
    private List<AdminLogin> admins = new ArrayList<>();

    public AdminLoginRepo() {
        admins.add(new AdminLogin("admin", "admin123"));
    }

    public AdminLogin findByUsername(String username) {
        for (AdminLogin admin : admins) {
            if (admin.getUsername().equals(username)) {
                return admin;
            }
        }
        return null;
    }
}