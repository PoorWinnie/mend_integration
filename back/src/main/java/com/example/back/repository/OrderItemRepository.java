package com.example.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back.domain.OrderItemBean;

public interface OrderItemRepository extends JpaRepository<OrderItemBean, Integer> {
	List<OrderItemBean> findByOrder_OrderId(Integer orderId); 
}
