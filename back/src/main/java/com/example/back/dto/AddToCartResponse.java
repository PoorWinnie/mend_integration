package com.example.back.dto;

import com.example.back.domain.ShoppingCartItem;

public class AddToCartResponse {
	
	private int statusCode;
    private String message;
    private ShoppingCartItem item;
    
	public AddToCartResponse(int statusCode, String message, ShoppingCartItem item) {
		super();
		this.statusCode = statusCode;
		this.message = message;
		this.item = item;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ShoppingCartItem getItem() {
		return item;
	}
	public void setItem(ShoppingCartItem item) {
		this.item = item;
	}

}
