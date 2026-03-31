package org.restaurant.model.checkout;

public class Checkout {
    private String customerId;
    private double totalAmount;
    private String status;

    public Checkout(String customerId, double totalAmount) {
        this.customerId  = customerId;
        this.totalAmount = totalAmount;
        this.status      = "PENDING";
    }

    public String getCustomerId()  { return customerId; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus()      { return status; }
    public void setStatus(String status) { this.status = status; }
}