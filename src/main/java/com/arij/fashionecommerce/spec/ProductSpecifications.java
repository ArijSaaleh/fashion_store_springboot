package com.arij.fashionecommerce.spec;

import com.arij.fashionecommerce.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecifications {

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, q, cb) ->
                categoryId == null ? cb.conjunction() : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> nameOrDescriptionContains(String qText) {
        return (root, q, cb) -> {
            if (qText == null || qText.isBlank()) return cb.conjunction();
            String like = "%" + qText.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }

    public static Specification<Product> priceGte(BigDecimal min) {
        return (root, q, cb) -> min == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    public static Specification<Product> priceLte(BigDecimal max) {
        return (root, q, cb) -> max == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("price"), max);
    }
}