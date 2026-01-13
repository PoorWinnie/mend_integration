package com.example.back.dto;

import lombok.Data;

@Data
public class OrderUpdateRequest {
	
	private String paymentMethod;
    private String status;
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
    
}
