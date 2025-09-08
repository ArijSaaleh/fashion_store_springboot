package com.arij.fashionecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
public class Discount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Choose one: percentage OR fixedAmount (null for the other)
    @DecimalMin("0.0") @DecimalMax("100.0")
    private BigDecimal percentage;     // e.g., 20 (%)

    @DecimalMin("0.0")
    private BigDecimal fixedAmount;    // e.g., 10.00

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public boolean isActive(LocalDateTime now) {
        return (now.isEqual(startAt) || now.isAfter(startAt)) && now.isBefore(endAt);
    }

    // getters/setters
    public Long getId() { return id; }
    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    public BigDecimal getFixedAmount() { return fixedAmount; }
    public void setFixedAmount(BigDecimal fixedAmount) { this.fixedAmount = fixedAmount; }
    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}