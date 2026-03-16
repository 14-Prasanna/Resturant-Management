package org.restaurant.repository.login;

import org.restaurant.model.login.DeliveryBoyLogin;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DeliveryBoyLoginRepo {

    // Key = username, Value = DeliveryBoyLogin (contains ArrayList inside)
    private Map<String, DeliveryBoyLogin> deliveryBoyMap = new HashMap<>();

    public boolean register(String username, String password) {
        if (deliveryBoyMap.containsKey(username)) {
            System.out.println("Username already taken. Try another.");
            return false;
        }
        deliveryBoyMap.put(username, new DeliveryBoyLogin(username, password));
        System.out.println("Delivery Boy Registration successful!");
        return true;
    }

    public DeliveryBoyLogin findByUsername(String username) {
        return deliveryBoyMap.get(username);
    }

    public Collection<DeliveryBoyLogin> getAllDeliveryBoys() {
        return deliveryBoyMap.values();
    }
}