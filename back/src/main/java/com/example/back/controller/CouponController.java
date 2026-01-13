package com.example.back.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.CouponResponseDTO;
import com.example.back.service.CouponService;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/{code}")
    public ResponseEntity<?> getCouponByCode(@PathVariable String code) {
        Optional<CouponResponseDTO> couponOpt = couponService.getCouponByCode(code);
        if (couponOpt.isPresent()) {
            return ResponseEntity.ok(couponOpt.get());
        } else {
            return ResponseEntity.status(404).body("優惠券無效或不存在");
        }
    }
}