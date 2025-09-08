package com.arij.fashionecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class ProductUpdateRequest {
    @NotNull public Long id;
    public String name;
    public String description;
    public BigDecimal price;
    public String brand;
    public Long categoryId;
    @Valid public ProductDetailsDTO details;
    @Valid public List<ProductVariantDTO> variants; // full replace for simplicity
    public @Valid List<String> imageUrls;
}