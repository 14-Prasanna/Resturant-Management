package org.restaurant.service.payment;

import org.restaurant.model.order.Order;
import org.restaurant.model.payment.Payment;
import org.restaurant.model.payment.Payment.PaymentMethod;
import org.restaurant.model.payment.Payment.PaymentStatus;
import org.restaurant.repository.order.OrderRepository;
import org.restaurant.repository.payment.PaymentRepository;

import java.util.List;
import java.util.UUID;

public class PaymentService {

    private OrderRepository   orderRepository   = OrderRepository.getInstance();
    private PaymentRepository paymentRepository = PaymentRepository.getInstance();

    // ─────────────────────────────────────────────────────────────────────────
    // PROCESS PAYMENT
    //  1. Fetch order from DB
    //  2. Create payment row as PENDING
    //  3. Simulate gateway
    //  4. Update payment row to SUCCESS / FAILED + set transaction_id
    //  5. On SUCCESS → advance order_status to PROCESSING
    // ─────────────────────────────────────────────────────────────────────────
    public Payment processPayment(String orderId, String customerId, PaymentMethod paymentMethod) {

        Order order = orderRepository.findById(orderId);
        if (order == null) {
            System.out.println("❌ Order not found: " + orderId);
            return null;
        }

        String  paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Payment payment   = new Payment(paymentId, orderId, customerId,
                order.getTotalAmount(), paymentMethod);

        // Persist PENDING first
        paymentRepository.savePayment(payment);

        // Simulate gateway response
        boolean success       = simulatePayment(paymentMethod);
        String  transactionId = success
                ? "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase()
                : null;

        PaymentStatus finalStatus = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        payment.setPaymentStatus(finalStatus);
        payment.setTransactionId(transactionId);

        // Update DB with final status
        paymentRepository.updatePaymentStatus(paymentId, finalStatus.name(), transactionId);

        // If paid → move order forward
        if (success) {
            orderRepository.updateOrderStatus(orderId, "PROCESSING");
        }

        return payment;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MANUAL STATUS UPDATE  (admin override)
    // ─────────────────────────────────────────────────────────────────────────
    public boolean updatePaymentStatus(String paymentId, String newStatus) {
        return paymentRepository.updatePaymentStatus(paymentId, newStatus, null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // QUERIES
    // ─────────────────────────────────────────────────────────────────────────
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.getPaymentByOrderId(orderId);
    }

    public Payment getPaymentById(String paymentId) {
        return paymentRepository.getPaymentById(paymentId);
    }

    public List<Payment> getPaymentsByCustomer(String customerId) {
        return paymentRepository.getPaymentsByCustomer(customerId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SIMULATE GATEWAY  –  swap with real API in production
    // ─────────────────────────────────────────────────────────────────────────
    private boolean simulatePayment(PaymentMethod method) {
        return switch (method) {
            case CASH_ON_DELIVERY -> true;
            case CARD             -> true;
            case UPI              -> true;
        };
    }
}
