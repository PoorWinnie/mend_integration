package com.example.back.dto;

import lombok.Data;

@Data
public class CouponResponseDTO {
	
	private Integer couponId;
    private String name;
    private Integer discountAmount;
    
    
    
	public CouponResponseDTO(Integer id, String name2, Integer discountAmount2) {
		// TODO Auto-generated constructor stub
	}
	public Integer getCouponId() {
		return couponId;
	}
	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(Integer discountAmount) {
		this.discountAmount = discountAmount;
	}
    
}
