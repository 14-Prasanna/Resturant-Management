package org.restaurant.repository.payment;

import org.restaurant.model.payment.Payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PaymentRepository — in-memory store for Payment records.
 *
 * Uses the same Singleton pattern as OrderRepository so the same
 * payment data is accessible across the application.
 */
public class PaymentRepository {

    private static PaymentRepository instance;

    private Map<String, Payment> payments = new HashMap<>();

    private PaymentRepository() {}

    public static PaymentRepository getInstance() {
        if (instance == null) {
            instance = new PaymentRepository();
        }
        return instance;
    }

    /** Persists a payment record. */
    public void savePayment(Payment payment) {
        payments.put(payment.getPaymentId(), payment);
    }

    /** Retrieves a payment by its ID. Returns null if not found. */
    public Payment getPaymentById(String paymentId) {
        return payments.get(paymentId);
    }

    /** Retrieves the payment linked to a specific order. Returns null if not found. */
    public Payment getPaymentByOrderId(String orderId) {
        return payments.values().stream()
                .filter(p -> p.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    /** Returns all payments made by a customer. */
    public List<Payment> getPaymentsByCustomer(String customerId) {
        List<Payment> result = new ArrayList<>();
        for (Payment payment : payments.values()) {
            if (payment.getCustomerId().equals(customerId)) {
                result.add(payment);
            }
        }
        return result;
    }
}
