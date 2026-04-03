package org.restaurant.model.login;

import java.util.ArrayList;
import java.util.List;

public class DeliveryBoyLogin {
    private String username;
    private String password;
    private List<String> assignedOrders;
    private List<String> deliveryHistory;

    public DeliveryBoyLogin(String username, String password) {
        this.username = username;
        this.password = password;
        this.assignedOrders = new ArrayList<>();
        this.deliveryHistory = new ArrayList<>();
    }

    public String getUsername()               { return username; }
    public String getPassword()               { return password; }
    public List<String> getAssignedOrders()   { return assignedOrders; }
    public List<String> getDeliveryHistory()  { return deliveryHistory; }

    public void addAssignedOrder(String order)     { assignedOrders.add(order); }
    public void addDeliveryHistory(String history) { deliveryHistory.add(history); }
}