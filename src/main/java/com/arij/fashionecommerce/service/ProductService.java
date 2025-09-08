package com.arij.fashionecommerce.service;

import com.arij.fashionecommerce.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse create(ProductCreateRequest req);
    ProductResponse update(Long id, ProductUpdateRequest req);
    void delete(Long id);
    ProductResponse get(Long id);

    Page<ProductResponse> search(
            Long categoryId, String q,
            java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
            Pageable pageable
    );

    void addDiscount(Long productId, DiscountRequest req);
    void removeDiscount(Long productId, Long discountId);
}