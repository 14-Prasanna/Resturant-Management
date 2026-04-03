package org.restaurant.repository.login;

import org.restaurant.model.login.ManagerLogin;
import java.util.ArrayList;
import java.util.List;

public class ManagerLoginRepo {
    private List<ManagerLogin> managers = new ArrayList<>();

    public ManagerLoginRepo() {
        managers.add(new ManagerLogin("manager", "manager123"));
    }

    public ManagerLogin findByUsername(String username) {
        for (ManagerLogin manager : managers) {
            if (manager.getUsername().equals(username)) {
                return manager;
            }
        }
        return null;
    }
}