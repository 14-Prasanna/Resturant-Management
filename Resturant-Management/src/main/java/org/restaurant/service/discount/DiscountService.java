package org.restaurant.service.discount;

import org.restaurant.model.discount.Discount;
import org.restaurant.repository.discount.DiscountRepo;

import java.sql.Date;
import java.util.List;

public class DiscountService {
    private DiscountRepo repo = new DiscountRepo();

    public boolean createDiscount(String code, double percent, double minAmount, Integer maxUses, Date from, Date until) {
        if (code == null || code.trim().isEmpty() || percent <= 0 || percent > 100) return false;
        return repo.createDiscount(code, percent, minAmount, maxUses, from, until);
    }

    public List<Discount> getAllDiscounts() {
        return repo.getAll();
    }
    
    public Discount getValidDiscount(String code, double cartSubtotal) {
        Discount d = repo.getByCode(code);
        if (d == null) {
            System.out.println("❌ Discount code not found or inactive.");
            return null;
        }
        
        long now = System.currentTimeMillis();
        if (d.getValidFrom() != null && d.getValidFrom().getTime() > now) {
            System.out.println("❌ Discount code is not yet valid.");
            return null;
        }
        if (d.getValidUntil() != null && d.getValidUntil().getTime() < now) {
            System.out.println("❌ Discount code has expired.");
            return null;
        }
        
        if (d.getMaxUses() != null && d.getUsedCount() >= d.getMaxUses()) {
            System.out.println("❌ Discount code usage limit reached.");
            return null;
        }
        
        if (cartSubtotal < d.getMinOrderAmount()) {
            System.out.println("❌ Minimum order amount to use this discount is ₹" + d.getMinOrderAmount());
            return null;
        }
        
        return d;
    }
    
    public void recordDiscountUsed(int discountId) {
        repo.incrementUsedCount(discountId);
    }
}
