package org.restaurant.model.payment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Payment model — represents a payment transaction linked to an Order.
 *
 * Fields only. No business logic here (follows the same convention as
 * Order, Cart, and MenuItem in the existing project).
 */
public class Payment {

    public enum PaymentMethod {
        CASH_ON_DELIVERY,
        CARD,
        UPI
    }

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED
    }

    private String        paymentId;
    private String        orderId;       // FK → Order.orderId
    private String        customerId;
    private double        amountPaid;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String        paidAt;

    public Payment(String paymentId, String orderId, String customerId,
                   double amountPaid, PaymentMethod paymentMethod) {
        this.paymentId     = paymentId;
        this.orderId       = orderId;
        this.customerId    = customerId;
        this.amountPaid    = amountPaid;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = PaymentStatus.PENDING;
        this.paidAt        = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // --- Getters ---
    public String        getPaymentId()     { return paymentId;     }
    public String        getOrderId()       { return orderId;       }
    public String        getCustomerId()    { return customerId;    }
    public double        getAmountPaid()    { return amountPaid;    }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public String        getPaidAt()        { return paidAt;        }

    // --- Setter (only status is mutable after construction) ---
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}