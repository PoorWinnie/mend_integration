//package com.example.back.dto;
//
//import java.math.BigDecimal;
//
//import com.example.back.domain.ProductBean;
//
//public class ProductDTO {
//    private Integer productId;
//    private String name;
//    private Integer productTypeId;
//    private String productTypeName;
//    private String description;
//    private BigDecimal price;
//    private Integer stock;
//    private String imgUrl;
//
//    public ProductDTO(ProductBean bean) {
//        this.productId = bean.getProductId();
//        this.name = bean.getName();
//        this.description = bean.getDescription();
//        this.price = bean.getPrice();
//        this.stock = bean.getStock();
//        this.imgUrl = bean.getImgUrl();
//
//        if (bean.getProductType() != null) {
//            this.productTypeId = bean.getProductType().getId();
//            this.productTypeName = bean.getProductType().getTypeName();
//        }
//    }
//
//	public Integer getProductId() {
//		return productId;
//	}
//
//	public void setProductId(Integer productId) {
//		this.productId = productId;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public Integer getProductTypeId() {
//		return productTypeId;
//	}
//
//	public void setProductTypeId(Integer productTypeId) {
//		this.productTypeId = productTypeId;
//	}
//
//	public String getProductTypeName() {
//		return productTypeName;
//	}
//
//	public void setProductTypeName(String productTypeName) {
//		this.productTypeName = productTypeName;
//	}
//
//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}
//
//	public BigDecimal getPrice() {
//		return price;
//	}
//
//	public void setPrice(BigDecimal price) {
//		this.price = price;
//	}
//
//	public Integer getStock() {
//		return stock;
//	}
//
//	public void setStock(Integer stock) {
//		this.stock = stock;
//	}
//
//	public String getImgUrl() {
//		return imgUrl;
//	}
//
//	public void setImgUrl(String imgUrl) {
//		this.imgUrl = imgUrl;
//	}
//
//    // Getters and Setters 省略
//    
//}
//
