package com.example.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.OrderRequest;
import com.example.back.service.EcpayService;

@RestController
@RequestMapping("/api/ecpay")
public class EcpayController {

    @Autowired
    private EcpayService ecpayService;
    
    @PostMapping("/checkout")
    public ResponseEntity<String> ecpayCheckout(@RequestBody OrderRequest orderRequest) {
        String htmlForm = ecpayService.generateEcpayCheckoutForm(orderRequest);
        return ResponseEntity.ok(htmlForm);
    }
      
}
