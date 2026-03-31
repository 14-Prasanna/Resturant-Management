package org.restaurant.service.login;

import org.restaurant.model.login.DeliveryBoyLogin;
import org.restaurant.repository.login.DeliveryBoyLoginRepo;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Collection;

public class DeliveryBoyLoginService {
    private DeliveryBoyLoginRepo deliveryBoyLoginRepo = new DeliveryBoyLoginRepo();

    // REGISTER with phone
    public boolean register(String username, String password, String phone) {

        // Validations
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty!");
            return false;
        }
        if (password == null || password.length() < 6) {
            System.out.println("Password must be at least 6 characters!");
            return false;
        }
        if (phone == null || phone.length() != 10) {
            System.out.println("Phone must be 10 digits!");
            return false;
        }

        // Hash password before storing
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return deliveryBoyLoginRepo.register(username, hashedPassword, phone);
    }

    // LOGIN
    public DeliveryBoyLogin login(String username, String password) {
        DeliveryBoyLogin boy = deliveryBoyLoginRepo.findByUsername(username);
        if (boy != null && BCrypt.checkpw(password, boy.getPassword())) {
            return boy;
        }
        return null;
    }

    public void addAssignedOrder(String username, String order) {
        DeliveryBoyLogin boy = deliveryBoyLoginRepo.findByUsername(username);
        if (boy != null) boy.addAssignedOrder(order);
    }

    public void addDeliveryHistory(String username, String history) {
        DeliveryBoyLogin boy = deliveryBoyLoginRepo.findByUsername(username);
        if (boy != null) boy.addDeliveryHistory(history);
    }

    public Collection<DeliveryBoyLogin> getAllDeliveryBoys() {
        return deliveryBoyLoginRepo.getAllDeliveryBoys();
    }
}