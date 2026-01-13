package com.example.back.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.domain.OrderBean;
import com.example.back.dto.OrderRequest;
import com.example.back.dto.OrderResponse;
import com.example.back.dto.OrderUpdateRequest;
import com.example.back.service.OrderService;

@RestController
@RequestMapping("/api/public/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<OrderResponse> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<OrderBean> findById(@PathVariable Integer id) {
        return orderService.findById(id);
    }

    @PostMapping
    public void createOrder(@RequestBody OrderRequest orderRequest) {
        orderService.createOrder(orderRequest);
    }
    
    @PutMapping("/{id}")
    public void updateOrder(@PathVariable Integer id, @RequestBody OrderUpdateRequest orderUpdateRequest) {
        orderService.updateOrder(id, orderUpdateRequest);
    }
    
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        orderService.deleteById(id);
    }
}
