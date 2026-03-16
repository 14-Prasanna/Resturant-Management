package org.restaurant.service.login;

import org.restaurant.model.login.DeliveryBoyLogin;
import org.restaurant.repository.login.DeliveryBoyLoginRepo;
import java.util.Collection;

public class DeliveryBoyLoginService {

    private DeliveryBoyLoginRepo deliveryBoyLoginRepo = new DeliveryBoyLoginRepo();

    public boolean register(String username, String password) {
        return deliveryBoyLoginRepo.register(username, password);
    }

    public DeliveryBoyLogin login(String username, String password) {
        DeliveryBoyLogin boy = deliveryBoyLoginRepo.findByUsername(username);
        if (boy != null && boy.getPassword().equals(password)) {
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