package com.example.back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.domain.ShoppingCartItem;
import com.example.back.dto.AddToCartRequest;
import com.example.back.dto.AddToCartResponse;
import com.example.back.dto.ShoppingCartItemDTO;
import com.example.back.service.ShoppingCartItemService;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartItemController {

	@Autowired
    private ShoppingCartItemService service;
	
    @GetMapping
    public List<ShoppingCartItem> getAllItems() {
        return service.getAll();
    }
    
    @GetMapping("/user/{name}")
    public List<ShoppingCartItemDTO> getCartItemsByUserName(@PathVariable String name) {
        return service.getCartItemsByUserName(name);
    }

    @PostMapping
    public ResponseEntity<?> addItem(@RequestBody AddToCartRequest request) {
        try {
            AddToCartResponse response = service.addItem(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AddToCartResponse errorResponse = new AddToCartResponse(-1, "加入購物車失敗：" + e.getMessage(), null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping
    public ShoppingCartItem updateItem(@RequestBody ShoppingCartItem item) {
        return service.updateItem(item);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Integer id) {
    	
        service.deleteItem(id);
    }
}