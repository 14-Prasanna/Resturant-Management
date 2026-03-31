package org.restaurant.model.payment;

public class Payment {
    public enum PaymentMethod {
        CASH, CARD, UPI
    }

    public enum PaymentStatus {
        PENDING, SUCCESS, FAILED
    }

    private String paymentId;
    private String orderId;
    private double amount;
    private PaymentMethod method;
    private PaymentStatus status;

    public Payment(String paymentId, String orderId, double amount, PaymentMethod method) {
        this.paymentId = paymentId;
        this.orderId   = orderId;
        this.amount    = amount;
        this.method    = method;
        this.status    = PaymentStatus.PENDING;
    }

    public String getPaymentId()      { return paymentId; }
    public String getOrderId()        { return orderId; }
    public double getAmount()         { return amount; }
    public PaymentMethod getMethod()  { return method; }
    public PaymentStatus getStatus()  { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
}