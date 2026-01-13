package com.example.back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back.domain.ProductBean;
import com.example.back.domain.ShoppingCartItem;
import com.example.back.domain.UserBean;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Integer> {
	List<ShoppingCartItem> findByUser_Username(String username);
	Optional<ShoppingCartItem> findByUserAndProduct(UserBean user, ProductBean product);
}
