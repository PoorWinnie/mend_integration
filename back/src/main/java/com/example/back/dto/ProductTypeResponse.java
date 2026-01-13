package com.example.back.dto;

import java.util.List;

import com.example.back.domain.ProductTypeBean;

public class ProductTypeResponse {
    private boolean success;
    private String message;
    private List<ProductTypeBean> list;
    private ProductTypeBean productType;

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public List<ProductTypeBean> getList() {
        return list;
    }
    public void setList(List<ProductTypeBean> list) {
        this.list = list;
    }
    public ProductTypeBean getProductType() {
        return productType;
    }
    public void setProductType(ProductTypeBean productType) {
        this.productType = productType;
    }

    @Override
    public String toString() {
        return "ProductTypeResponse [success=" + success + ", message=" + message
                + ", list=" + list + ", productType=" + productType + "]";
    }
}
