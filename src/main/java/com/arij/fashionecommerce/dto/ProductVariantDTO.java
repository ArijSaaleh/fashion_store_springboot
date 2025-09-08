package com.arij.fashionecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductVariantDTO {
    public Long id;
    @NotBlank public String size;
    @NotBlank public String color;
    @NotNull public Integer stockQuantity;
    public String sku;
}