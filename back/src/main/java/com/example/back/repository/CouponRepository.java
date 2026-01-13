package com.example.back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back.domain.CouponBean;

public interface CouponRepository extends JpaRepository<CouponBean, Integer> {
    Optional<CouponBean> findByCouponCodeAndStatus(String couponCode, String status);
}
