package com.arij.fashionecommerce.service;

import com.arij.fashionecommerce.dto.*;
import com.arij.fashionecommerce.model.*;
import com.arij.fashionecommerce.repository.*;
import com.arij.fashionecommerce.spec.ProductSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final DiscountRepository discountRepo;

    public ProductServiceImpl(ProductRepository productRepo, CategoryRepository categoryRepo, DiscountRepository discountRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.discountRepo = discountRepo;
    }

    @Override
    public ProductResponse create(ProductCreateRequest req) {
        Product p = new Product();
        applyUpsert(p, req.name, req.description, req.price, req.brand, req.categoryId, req.details, req.variants, req.imageUrls);
        return toResponse(productRepo.save(p));
    }

    @Override
    public ProductResponse update(Long id, ProductUpdateRequest req) {
        Product p = productRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        applyUpsert(p, req.name, req.description, req.price, req.brand, req.categoryId, req.details, req.variants, req.imageUrls);
        return toResponse(p);
    }

    @Override
    public void delete(Long id) {
        if (!productRepo.existsById(id)) throw new EntityNotFoundException("Product not found");
        productRepo.deleteById(id);
    }

    @Override
    public ProductResponse get(Long id) {
        return productRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    @Override
    public Page<ProductResponse> search(Long categoryId, String q, BigDecimal min, BigDecimal max, Pageable pageable) {
        Specification<Product> spec = Specification
                .where(ProductSpecifications.hasCategory(categoryId))
                .and(ProductSpecifications.nameOrDescriptionContains(q))
                .and(ProductSpecifications.priceGte(min))
                .and(ProductSpecifications.priceLte(max));

        return productRepo.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    public void addDiscount(Long productId, DiscountRequest req) {
        Product p = productRepo.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if ((req.percentage == null && req.fixedAmount == null) ||
                (req.percentage != null && req.fixedAmount != null)) {
            throw new IllegalArgumentException("Provide exactly one of percentage or fixedAmount");
        }
        if (!req.endAt.isAfter(req.startAt)) {
            throw new IllegalArgumentException("endAt must be after startAt");
        }

        Discount d = new Discount();
        d.setProduct(p);
        d.setPercentage(req.percentage);
        d.setFixedAmount(req.fixedAmount);
        d.setStartAt(req.startAt);
        d.setEndAt(req.endAt);

        p.getDiscounts().add(d);
        discountRepo.save(d);
    }

    @Override
    public void removeDiscount(Long productId, Long discountId) {
        Product p = productRepo.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Discount d = p.getDiscounts().stream().filter(x -> x.getId().equals(discountId))
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Discount not found on product"));
        p.getDiscounts().remove(d);
        discountRepo.delete(d);
    }

    /* ---------- helpers ---------- */

    private void applyUpsert(
            Product p,
            String name, String description, BigDecimal price, String brand, Long categoryId,
            ProductDetailsDTO detailsDto, List<ProductVariantDTO> variants,
            @Valid List<String> imageUrls) {
        if (name != null) p.setName(name);
        if (description != null) p.setDescription(description);
        if (price != null) p.setPrice(price);
        if (brand != null) p.setBrand(brand);
        if (categoryId != null) {
            Category c = categoryRepo.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            p.setCategory(c);
        }
        if (detailsDto != null) {
            ProductDetails details = p.getDetails() == null ? new ProductDetails() : p.getDetails();
            details.setFabric(detailsDto.fabric);
            details.setColor(detailsDto.color);
            details.setCareInstructions(detailsDto.careInstructions);
            details.setSizeGuideUrl(detailsDto.sizeGuideUrl);
            p.setDetails(details);
        }
        if (variants != null) {
            p.getVariants().clear();
            p.setStockQuantity(0);
            for (ProductVariantDTO v : variants) {
                ProductVariant pv = new ProductVariant();
                pv.setProduct(p);
                pv.setSize(v.size);
                pv.setColor(v.color);
                pv.setSku(v.sku);
                pv.setStockQuantity(v.stockQuantity == null ? 0 : v.stockQuantity);
                p.getVariants().add(pv);
                p.setStockQuantity(p.getStockQuantity() + pv.getStockQuantity());
            }
        }
        if (imageUrls != null) {
            p.setImageUrls(new ArrayList<>(imageUrls));
        }

    }

    private ProductResponse toResponse(Product p) {
        ProductResponse r = new ProductResponse();
        r.id = p.getId();
        r.name = p.getName();
        r.description = p.getDescription();
        r.price = p.getPrice();
        r.brand = p.getBrand();
        r.categoryId = p.getCategory() != null ? p.getCategory().getId() : null;
        r.categoryName = p.getCategory() != null ? p.getCategory().getName() : null;
        r.createdAt = p.getCreatedAt();
        r.updatedAt = p.getUpdatedAt();

        // Image URLs
        r.imageUrls = p.getImageUrls() != null ? new ArrayList<>(p.getImageUrls()) : new ArrayList<>();

        // Details
        if (p.getDetails() != null) {
            ProductDetailsDTO details = new ProductDetailsDTO();
            details.fabric = p.getDetails().getFabric();
            details.color = p.getDetails().getColor();
            details.careInstructions = p.getDetails().getCareInstructions();
            details.sizeGuideUrl = p.getDetails().getSizeGuideUrl();
            r.details = details;
        }

        // Variants
        r.variants = new ArrayList<>();
        for (ProductVariant pv : p.getVariants()) {
            ProductVariantDTO dv = new ProductVariantDTO();
            dv.id = pv.getId();
            dv.size = pv.getSize();
            dv.color = pv.getColor();
            dv.stockQuantity = pv.getStockQuantity();
            dv.sku = pv.getSku();
            r.variants.add(dv);
        }

        // Discount calculation (best active discount)
        var now = LocalDateTime.now();
        BigDecimal bestDiscount = BigDecimal.ZERO;
        boolean hasDiscount = false;
        for (Discount d : p.getDiscounts()) {
            if (!d.isActive(now)) continue;
            BigDecimal candidate = d.getPercentage() != null
                    ? p.getPrice().multiply(d.getPercentage()).divide(BigDecimal.valueOf(100))
                    : d.getFixedAmount() != null ? d.getFixedAmount() : BigDecimal.ZERO;
            if (candidate.compareTo(bestDiscount) > 0) {
                bestDiscount = candidate;
                hasDiscount = true;
            }
        }

        r.discounted = hasDiscount;
        r.finalPrice = hasDiscount ? p.getPrice().subtract(bestDiscount).max(BigDecimal.ZERO) : p.getPrice();

        return r;
    }
}