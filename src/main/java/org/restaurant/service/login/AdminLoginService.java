package org.restaurant.service.login;

import org.restaurant.model.login.AdminLogin;
import org.restaurant.repository.login.AdminLoginRepo;

public class AdminLoginService {
    private AdminLoginRepo adminLoginRepo = new AdminLoginRepo();

    public boolean login(String username, String password) {
        AdminLogin admin = adminLoginRepo.findByUsername(username);
        return admin != null && admin.getPassword().equals(password);
    }
}