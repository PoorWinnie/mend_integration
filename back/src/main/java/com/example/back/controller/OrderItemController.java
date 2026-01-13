package com.example.back.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.domain.OrderItemBean;
import com.example.back.service.OrderItemService;

@RestController
@RequestMapping("/api/orderItems")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping
    public List<OrderItemBean> findAll() {
        return orderItemService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<OrderItemBean> findById(@PathVariable Integer id) {
        return orderItemService.findById(id);
    }

    @GetMapping("/order/{orderId}")
    public List<OrderItemBean> findByOrderId(@PathVariable Integer orderId) {
        return orderItemService.findByOrderId(orderId);
    }

    @PostMapping
    public OrderItemBean save(@RequestBody OrderItemBean orderItem) {
        return orderItemService.save(orderItem);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        orderItemService.deleteById(id);
    }
}