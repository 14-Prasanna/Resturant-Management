package org.restaurant.repository.payment;

import org.restaurant.config.CleverCloudDB;
import org.restaurant.model.payment.Payment;
import org.restaurant.model.payment.Payment.PaymentMethod;
import org.restaurant.model.payment.Payment.PaymentStatus;

import java.sql.*;
import java.util.*;

public class PaymentRepository {

    private static PaymentRepository instance;
    private PaymentRepository() {}

    public static PaymentRepository getInstance() {
        if (instance == null) instance = new PaymentRepository();
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SAVE  –  insert new payment row (PENDING)
    // ─────────────────────────────────────────────────────────────────────────
    public boolean savePayment(Payment payment) {
        String sql = """
                INSERT INTO payments
                    (payment_id, order_id, customer_id, payment_method,
                     payment_status, transaction_id, amount)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, payment.getPaymentId());
            ps.setString(2, payment.getOrderId());
            ps.setString(3, payment.getCustomerId());
            ps.setString(4, payment.getPaymentMethod().name());
            ps.setString(5, payment.getPaymentStatus().name());
            ps.setString(6, payment.getTransactionId());       // null for PENDING
            ps.setDouble(7, payment.getAmountPaid());
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("❌ Error saving payment: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE STATUS + TRANSACTION ID  –  called after gateway response
    // ─────────────────────────────────────────────────────────────────────────
    public boolean updatePaymentStatus(String paymentId, String status, String transactionId) {
        String sql = "UPDATE payments SET payment_status = ?, transaction_id = ? WHERE payment_id = ?";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, transactionId);
            ps.setString(3, paymentId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Error updating payment status: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIND BY ORDER ID
    // ─────────────────────────────────────────────────────────────────────────
    public Payment getPaymentByOrderId(String orderId) {
        String sql = "SELECT * FROM payments WHERE order_id = ?";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching payment by order: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIND BY PAYMENT ID
    // ─────────────────────────────────────────────────────────────────────────
    public Payment getPaymentById(String paymentId) {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching payment by ID: " + e.getMessage());
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ALL PAYMENTS FOR A CUSTOMER
    // ─────────────────────────────────────────────────────────────────────────
    public List<Payment> getPaymentsByCustomer(String customerId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE customer_id = ? ORDER BY payment_date DESC";

        try (Connection con = CleverCloudDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching customer payments: " + e.getMessage());
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPER  –  map ResultSet row → Payment object
    // ─────────────────────────────────────────────────────────────────────────
    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment(
                rs.getString("payment_id"),
                rs.getString("order_id"),
                rs.getString("customer_id"),
                rs.getDouble("amount"),
                PaymentMethod.valueOf(rs.getString("payment_method"))
        );
        p.setPaymentStatus(PaymentStatus.valueOf(rs.getString("payment_status")));
        p.setTransactionId(rs.getString("transaction_id"));
        return p;
    }
}
