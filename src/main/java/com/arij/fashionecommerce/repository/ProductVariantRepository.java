package com.arij.fashionecommerce.repository;


import com.arij.fashionecommerce.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {}