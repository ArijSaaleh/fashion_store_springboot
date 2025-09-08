package com.arij.fashionecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class ProductCreateRequest {
    @NotBlank public String name;
    public String description;
    @NotNull public BigDecimal price;
    public String brand;
    @NotNull public Long categoryId;
    @Valid public ProductDetailsDTO details;
    @Valid public List<ProductVariantDTO> variants;
    @Valid public List<String> imageUrls;
}