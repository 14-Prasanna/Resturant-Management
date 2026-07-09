package org.restaurant.service.login;

import org.restaurant.model.login.DeliveryBoyLogin;
import org.restaurant.repository.login.DeliveryBoyLoginRepo;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;

public class DeliveryBoyLoginService {

    private DeliveryBoyLoginRepo deliveryBoyLoginRepo =
            new DeliveryBoyLoginRepo();

    // REGISTER
    public boolean register(String username,
                            String password,
                            String phone) {

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

        // Hash password
        String hashedPassword =
                BCrypt.hashpw(password, BCrypt.gensalt());

        return deliveryBoyLoginRepo.register(
                username,
                hashedPassword,
                phone
        );
    }

    // LOGIN
    public DeliveryBoyLogin login(String username,
                                  String password) {

        DeliveryBoyLogin boy =
                deliveryBoyLoginRepo.findByUsername(username);

        if (boy != null &&
                BCrypt.checkpw(password, boy.getPassword())) {

            return boy;
        }

        return null;
    }

    // ASSIGN ORDER
    public void addAssignedOrder(String username,
                                 String orderId) {

        DeliveryBoyLogin boy =
                deliveryBoyLoginRepo.findByUsername(username);

        if (boy != null) {
            boy.addAssignedOrder(orderId);
        }
    }

    // DELIVERY HISTORY
    public void addDeliveryHistory(String username,
                                   String orderId) {

        DeliveryBoyLogin boy =
                deliveryBoyLoginRepo.findByUsername(username);

        if (boy != null) {

            boy.addDeliveryHistory(orderId);

            // Remove from active assigned orders
            boy.getAssignedOrders().remove(orderId);
        }
    }

    // UPDATE ASSIGNMENT STATUS
    public boolean updateAssignmentStatus(String username,
                                          String orderId,
                                          String status) {

        DeliveryBoyLogin boy =
                deliveryBoyLoginRepo.findByUsername(username);

        if (boy == null) {
            return false;
        }

        // Check assignment ownership
        if (!boy.getAssignedOrders().contains(orderId)) {
            return false;
        }

        System.out.println(
                "Order " + orderId +
                        " updated to status: " + status
        );

        return true;
    }

    // GET ALL DELIVERY BOYS
    public Collection<DeliveryBoyLogin> getAllDeliveryBoys() {
        return deliveryBoyLoginRepo.getAllDeliveryBoys();
    }
}