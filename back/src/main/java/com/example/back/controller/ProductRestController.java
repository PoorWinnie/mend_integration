package com.example.back.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.domain.ProductBean;
import com.example.back.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<ProductBean> products = productService.select(null);
        if(products != null && !products.isEmpty()) {
            return ResponseEntity.ok(products);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody String entity) {
        ProductBean product = productService.create(entity);
        if (product != null) {
            URI uri = URI.create("/api/products/" + product.getProductId());
            return ResponseEntity.created(uri).body(product);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> findById(@PathVariable Integer productId) {
        ProductBean product = productService.findById(productId);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(@PathVariable Integer productId) {
        if(productService.remove(productId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> modify(@PathVariable Integer productId, @RequestBody ProductBean body) {
        if(!productId.equals(body.getProductId())) {
            return ResponseEntity.badRequest().build();
        }
        ProductBean product = productService.update(body);
        if(product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{productId}/click")
    public ResponseEntity<?> incrementClickCount(@PathVariable Integer productId) {
        ProductBean product = productService.incrementClickCount(productId);
        if(product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
