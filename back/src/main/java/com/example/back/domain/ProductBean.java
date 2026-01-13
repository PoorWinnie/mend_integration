package com.example.back.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductBean {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;
    
    @Column(name = "name", columnDefinition = "varchar")
    private String name;
    
    @JsonIgnoreProperties("products")
    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductTypeBean productType;
    
    @Column(name = "description", columnDefinition = "varchar")
    private String description;
    
    @Column(name = "price")
    private BigDecimal price;
    
    @Column(name = "stock")
    private Integer stock;
    
    @Column(name = "img_url", columnDefinition = "varchar")
    private String imgUrl;
    
    @Column(name = "click_count")
    private Integer clickCount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "ProductBean [productId=" + productId + ", name=" + name + ", price=" + price + "]";
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductTypeBean getProductType() {
        return productType;
    }

    public void setProductType(ProductTypeBean productType) {
        this.productType = productType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getClickCount() {
        return clickCount;
    }

    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
