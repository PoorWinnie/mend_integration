package com.example.back.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
	
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;

	public String getUsername() {
		return username;
	}

	// Getters and Setters
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
}
