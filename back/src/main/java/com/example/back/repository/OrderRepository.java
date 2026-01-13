package com.example.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back.domain.OrderBean;

public interface OrderRepository extends JpaRepository<OrderBean, Integer> {

}
