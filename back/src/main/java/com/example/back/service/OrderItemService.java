package com.example.back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.domain.OrderItemBean;
import com.example.back.repository.OrderItemRepository;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderItemBean> findAll() {
        return orderItemRepository.findAll();
    }

    public List<OrderItemBean> findByOrderId(Integer orderId) {
        return orderItemRepository.findByOrder_OrderId(orderId);
    }

    public Optional<OrderItemBean> findById(Integer id) {
        return orderItemRepository.findById(id);
    }

    public OrderItemBean save(OrderItemBean orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public void deleteById(Integer id) {
        orderItemRepository.deleteById(id);
    }
}
