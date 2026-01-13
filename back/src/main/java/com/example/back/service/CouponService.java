package com.example.back.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.dto.CouponResponseDTO;
import com.example.back.repository.CouponRepository;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    public Optional<CouponResponseDTO> getCouponByCode(String code) {
        return couponRepository.findByCouponCodeAndStatus(code, "active")
        		.map(coupon -> new CouponResponseDTO(coupon.getId(), coupon.getName(), coupon.getDiscountAmount()));

    }
}