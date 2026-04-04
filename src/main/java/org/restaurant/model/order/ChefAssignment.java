package org.restaurant.model.order;

public class ChefAssignment {
    private String chefUsername;
    private String orderId;
    private String status;
    private String assignedAt;

    public ChefAssignment(String chefUsername, String orderId, String status, String assignedAt) {
        this.chefUsername = chefUsername;
        this.orderId = orderId;
        this.status = status;
        this.assignedAt = assignedAt;
    }

    public String getChefUsername() { return chefUsername; }
    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getAssignedAt() { return assignedAt; }
}
