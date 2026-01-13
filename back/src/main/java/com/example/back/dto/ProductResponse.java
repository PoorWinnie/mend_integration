package com.example.back.dto;

import java.util.List;

import com.example.back.domain.ProductBean;

public class ProductResponse {
    private boolean success;
    private String message;
    private long count;
    private List<ProductBean> list;
    private ProductBean product;

    // Getter & Setter
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
    public List<ProductBean> getList() { return list; }
    public void setList(List<ProductBean> list) { this.list = list; }
    public ProductBean getProduct() { return product; }
    public void setProduct(ProductBean product) { this.product = product; }

    @Override
    public String toString() {
        return "ProductResponse [success=" + success + ", message=" + message + ", count=" + count
                + ", list=" + list + ", product=" + product + "]";
    }
}
