package com.example.back.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "coupons")
public class CouponBean {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 
	
	@Column(name = "coupon_code", nullable = false, unique = true, length = 50)
    private String couponCode;

    @Column(nullable = false, length = 100)
    private String name; 

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount; 

    @Column(nullable = false)
    private String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	} 

    
}
