package org.restaurant.repository.payment;

import org.restaurant.model.payment.Payment;
import java.util.*;

public class PaymentRepository {
    private static PaymentRepository instance;
    private Map<String, Payment> payments = new HashMap<>();

    private PaymentRepository() {}

    public static PaymentRepository getInstance() {
        if (instance == null) instance = new PaymentRepository();
        return instance;
    }

    public void savePayment(Payment payment) {
        payments.put(payment.getPaymentId(), payment);
    }

    public Payment getPaymentById(String paymentId) {
        return payments.get(paymentId);
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }
}