package org.restaurant.service.payment;

import org.restaurant.model.order.Order;
import org.restaurant.model.payment.Payment;
import org.restaurant.model.payment.Payment.PaymentMethod;
import org.restaurant.model.payment.Payment.PaymentStatus;
import org.restaurant.repository.order.OrderRepository;
import org.restaurant.repository.payment.PaymentRepository;

import java.util.UUID;

/**
 * PaymentService — processes payment for a placed order.
 *
 * Responsibilities:
 *   1. Receive orderId + chosen PaymentMethod from PaymentController.
 *   2. Look up the Order from OrderRepository (reusing existing repo).
 *   3. Simulate payment (Cash on Delivery always succeeds; Card/UPI
 *      are simulated with a fixed success response).
 *   4. Save the Payment record in PaymentRepository.
 *   5. Return the Payment result to the controller.
 *
 * No real payment gateway is integrated — this is a clean simulation.
 */
public class PaymentService {

    private OrderRepository   orderRepository   = OrderRepository.getInstance();
    private PaymentRepository paymentRepository = PaymentRepository.getInstance();

    /**
     * Processes payment for the given order using the specified method.
     *
     * @param orderId       The ID of the order to pay for.
     * @param customerId    The customer making the payment.
     * @param paymentMethod The chosen payment method.
     * @return Payment object with SUCCESS or FAILED status.
     *         Returns null if the orderId does not exist.
     */
    public Payment processPayment(String orderId, String customerId, PaymentMethod paymentMethod) {
        // Look up the order — reuse existing OrderRepository.getInstance()
        Order order = orderRepository.getAllOrders().stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);

        if (order == null) {
            return null;  // Order not found — controller will handle this
        }

        String  paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Payment payment   = new Payment(paymentId, orderId, customerId,
                order.getTotalAmount(), paymentMethod);

        // Simulate payment processing
        boolean success = simulatePayment(paymentMethod);

        payment.setPaymentStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);

        // Save the payment record
        paymentRepository.savePayment(payment);

        return payment;
    }

    /**
     * Simulates the payment gateway response.
     * - Cash on Delivery: always succeeds (no real-time processing needed).
     * - Card / UPI: simulated as always successful for this demo.
     *
     * In a real system, this method would call an external payment API.
     */
    private boolean simulatePayment(PaymentMethod method) {
        switch (method) {
            case CASH_ON_DELIVERY:
                return true;   // No processing required — always succeeds
            case CARD:
                return true;   // Simulated card payment — always succeeds
            case UPI:
                return true;   // Simulated UPI payment — always succeeds
            default:
                return false;
        }
    }

    /**
     * Retrieves the payment record linked to an order.
     * Used by controllers to display payment confirmation.
     */
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.getPaymentByOrderId(orderId);
    }
}
