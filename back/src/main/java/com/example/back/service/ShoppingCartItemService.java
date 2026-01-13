package com.example.back.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.domain.ProductBean;
import com.example.back.domain.ShoppingCartItem;
import com.example.back.domain.UserBean;
import com.example.back.dto.AddToCartRequest;
import com.example.back.dto.AddToCartResponse;
import com.example.back.dto.ShoppingCartItemDTO;
import com.example.back.repository.ProductRepository;
import com.example.back.repository.ShoppingCartItemRepository;
import com.example.back.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShoppingCartItemService {

	@Autowired
    private ShoppingCartItemRepository repository;
	
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
	
	
    public List<ShoppingCartItem> getAll() {
        return repository.findAll();
    }
    
    public List<ShoppingCartItemDTO> getCartItemsByUserName(String name) {
    	
    	List<ShoppingCartItem> items = repository.findByUser_Username(name);
    	List<ShoppingCartItemDTO> dtoList = new ArrayList<>();
    	
    	for (ShoppingCartItem item : items) {
    		ShoppingCartItemDTO dto = new ShoppingCartItemDTO();
            dto.setName(item.getProduct().getName());
            dto.setPrice(item.getProduct().getPrice());
            dto.setQuantity(item.getQuantity());
            dto.setImageUrl(item.getProduct().getImgUrl());

            
            dtoList.add(dto);
        }
        return dtoList;
    }
    public AddToCartResponse addItem(AddToCartRequest request) {
        UserBean user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("使用者不存在"));

        ProductBean product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("產品不存在"));
        
        Optional<ShoppingCartItem> existingItemOpt = repository.findByUserAndProduct(user, product);
        if (existingItemOpt.isPresent()) {
            return new AddToCartResponse(1, "商品已存在於購物車中", existingItemOpt.get());
        }

        // 建立新的購物車項目
        ShoppingCartItem newItem = new ShoppingCartItem();
        newItem.setUser(user);
        newItem.setProduct(product);
        newItem.setQuantity(1);
        
        ShoppingCartItem savedItem = repository.save(newItem);
        return new AddToCartResponse(0, "商品成功加入購物車", savedItem);
    }

    public void deleteItem(Integer id) {
        repository.deleteById(id);
    }

    public ShoppingCartItem updateItem(ShoppingCartItem item) {
        return repository.save(item);
    }
}
