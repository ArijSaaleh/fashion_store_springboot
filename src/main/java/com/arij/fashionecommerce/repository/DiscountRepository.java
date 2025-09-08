package com.arij.fashionecommerce.repository;

import com.arij.fashionecommerce.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {}