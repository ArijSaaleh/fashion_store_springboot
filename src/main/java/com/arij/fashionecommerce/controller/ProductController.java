package com.arij.fashionecommerce.controller;

import com.arij.fashionecommerce.dto.*;
import com.arij.fashionecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    // Public list + search
    @GetMapping
    public Page<ProductResponse> search(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 12, sort = "createdAt,desc") Pageable pageable
    ) {
        return service.search(categoryId, q, minPrice, maxPrice, pageable);
    }

    // Public single
    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    // Admin: create
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    // Admin: update
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest req) {
        req.id = id;
        return service.update(id, req);
    }

    // Admin: delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Admin: discounts
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/discounts")
    public ResponseEntity<Void> addDiscount(@PathVariable Long id, @Valid @RequestBody DiscountRequest req) {
        service.addDiscount(id, req);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/discounts/{discountId}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id, @PathVariable Long discountId) {
        service.removeDiscount(id, discountId);
        return ResponseEntity.noContent().build();
    }
}