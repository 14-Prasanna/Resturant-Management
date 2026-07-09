package org.restaurant.model.discount;

import java.sql.Date;
import java.sql.Timestamp;

public class Discount {
    private int id;
    private String code;
    private double discountPercent;
    private double minOrderAmount;
    private Integer maxUses;
    private int usedCount;
    private Date validFrom;
    private Date validUntil;
    private boolean isActive;
    private Timestamp createdAt;

    public Discount(int id, String code, double discountPercent, double minOrderAmount, 
                    Integer maxUses, int usedCount, Date validFrom, Date validUntil, 
                    boolean isActive, Timestamp createdAt) {
        this.id = id;
        this.code = code;
        this.discountPercent = discountPercent;
        this.minOrderAmount = minOrderAmount;
        this.maxUses = maxUses;
        this.usedCount = usedCount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public double getDiscountPercent() { return discountPercent; }
    public double getMinOrderAmount() { return minOrderAmount; }
    public Integer getMaxUses() { return maxUses; }
    public int getUsedCount() { return usedCount; }
    public Date getValidFrom() { return validFrom; }
    public Date getValidUntil() { return validUntil; }
    public boolean isActive() { return isActive; }
    public Timestamp getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("[%s] %.1f%% OFF | Min Order: %.2f | Valid: %s TO %s | Active: %b", 
                             code, discountPercent, minOrderAmount, validFrom, validUntil, isActive);
    }
}
