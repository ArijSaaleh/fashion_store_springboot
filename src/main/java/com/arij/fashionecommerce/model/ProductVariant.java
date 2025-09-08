package com.arij.fashionecommerce.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
        name = "product_variants",
        uniqueConstraints = @UniqueConstraint(columnNames = { "product_id", "size", "color" })
)
public class ProductVariant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String size;     // e.g., XS, S, M, L, XL

    @NotBlank
    private String color;    // specific color for variant

    @NotNull
    @Column(nullable = false)
    private Integer stockQuantity = 0;

    private String sku;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // getters/setters
    public Long getId() { return id; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}