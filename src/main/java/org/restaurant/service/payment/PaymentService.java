package org.restaurant.service.payment;

import org.restaurant.model.cart.Cart.CartItem;
import org.restaurant.model.payment.Payment;
import org.restaurant.model.payment.Payment.PaymentMethod;
import org.restaurant.model.payment.Payment.PaymentStatus;
import org.restaurant.repository.payment.PaymentRepository;
import java.util.*;

public class PaymentService {

    private PaymentRepository paymentRepository = PaymentRepository.getInstance();

    public PaymentService() {}   // ✅ no-arg constructor

    public boolean processPayment(String customerId,
                                  List<CartItem> cartItems,
                                  double totalAmount,
                                  int methodChoice) {
        PaymentMethod method = switch (methodChoice) {
            case 1 -> PaymentMethod.CASH;
            case 2 -> PaymentMethod.CARD;
            case 3 -> PaymentMethod.UPI;
            default -> PaymentMethod.CASH;
        };

        String paymentId = "PAY-" + UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase();

        Payment payment = new Payment(paymentId, "ORDER", totalAmount, method);
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.savePayment(payment);

        System.out.println("Payment successful!");
        System.out.println("Payment ID : " + paymentId);
        System.out.println("Method     : " + method);
        System.out.println("Amount     : ₹" + totalAmount);
        return true;
    }
}