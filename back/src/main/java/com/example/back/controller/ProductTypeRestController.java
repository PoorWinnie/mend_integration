package com.example.back.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.back.domain.ProductTypeBean;
import com.example.back.service.ProductTypeService;

@RestController
@RequestMapping("/api/product-types")
public class ProductTypeRestController {
    @Autowired
    private ProductTypeService productTypeService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<ProductTypeBean> types = productTypeService.findAll();
        if(types != null && !types.isEmpty()) {
            return ResponseEntity.ok(types);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        ProductTypeBean type = productTypeService.findById(id);
        if (type != null) {
            return ResponseEntity.ok(type);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody String json) {
        ProductTypeBean type = productTypeService.create(json);
        if (type != null) {
            URI uri = URI.create("/api/product-types/" + type.getId());
            return ResponseEntity.created(uri).body(type);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<?> modify(@RequestBody String json) {
        ProductTypeBean type = productTypeService.modify(json);
        if (type != null) {
            return ResponseEntity.ok(type);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Integer id) {
        if(productTypeService.remove(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
