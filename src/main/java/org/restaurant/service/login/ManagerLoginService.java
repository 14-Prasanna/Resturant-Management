package org.restaurant.service.login;

import org.restaurant.model.login.ManagerLogin;
import org.restaurant.repository.login.ManagerLoginRepo;

public class ManagerLoginService {
    private ManagerLoginRepo managerLoginRepo = new ManagerLoginRepo();

    public boolean login(String username, String password) {
        ManagerLogin manager = managerLoginRepo.findByUsername(username);
        return manager != null && manager.getPassword().equals(password);
    }
}