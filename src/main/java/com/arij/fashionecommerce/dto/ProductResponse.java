package com.arij.fashionecommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class ProductResponse {
    public Long id;
    public String name;
    public String description;
    public BigDecimal price;
    public BigDecimal finalPrice; // price after active discount (if any)
    public boolean discounted;
    public String brand;
    public Long categoryId;
    public String categoryName;
    public ProductDetailsDTO details;
    public List<ProductVariantDTO> variants;
    public List<String> imageUrls;
    public Instant createdAt;
    public Instant updatedAt;
}