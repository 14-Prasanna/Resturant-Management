package org.restaurant.service.login;

import org.restaurant.model.login.ChefLogin;
import org.restaurant.repository.login.ChefLoginRepository;
import java.util.List;

public class ChefLoginService {
    private ChefLoginRepository repository = new ChefLoginRepository();

    public boolean addChef(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Username and password cannot be empty.");
            return false;
        }
        return repository.addChef(username, password);
    }

    public boolean deleteChef(String username) {
        return repository.deleteChef(username);
    }

    public List<ChefLogin> getAllChefs() {
        return repository.getAllChefs();
    }

    public boolean login(String username, String password) {
        return repository.validateLogin(username, password);
    }
}
