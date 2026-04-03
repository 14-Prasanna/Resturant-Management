package org.restaurant.model.payment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Payment {

    public enum PaymentMethod { CASH_ON_DELIVERY, CARD, UPI }
    public enum PaymentStatus { PENDING, SUCCESS, FAILED }

    private String        paymentId;
    private String        orderId;
    private String        customerId;
    private double        amountPaid;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String        transactionId;
    private String        paidAt;

    public Payment(String paymentId, String orderId, String customerId,
                   double amountPaid, PaymentMethod paymentMethod) {
        this.paymentId     = paymentId;
        this.orderId       = orderId;
        this.customerId    = customerId;
        this.amountPaid    = amountPaid;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = PaymentStatus.PENDING;
        this.transactionId = null;
        this.paidAt        = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String        getPaymentId()     { return paymentId;     }
    public String        getOrderId()       { return orderId;       }
    public String        getCustomerId()    { return customerId;    }
    public double        getAmountPaid()    { return amountPaid;    }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public String        getTransactionId() { return transactionId; }
    public String        getPaidAt()        { return paidAt;        }

    public void setPaymentStatus(PaymentStatus status)  { this.paymentStatus = status;        }
    public void setTransactionId(String transactionId)  { this.transactionId = transactionId; }
}
