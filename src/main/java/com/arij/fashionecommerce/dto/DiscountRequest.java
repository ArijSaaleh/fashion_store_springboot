package com.arij.fashionecommerce.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DiscountRequest {
    @DecimalMin("0.0") @DecimalMax("100.0")
    public BigDecimal percentage;   // one of percentage or fixedAmount must be provided

    @DecimalMin("0.0")
    public BigDecimal fixedAmount;

    @NotNull public LocalDateTime startAt;
    @NotNull public LocalDateTime endAt;
}