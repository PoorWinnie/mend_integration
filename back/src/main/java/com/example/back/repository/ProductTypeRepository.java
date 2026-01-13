package com.example.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.back.domain.ProductTypeBean;

public interface ProductTypeRepository 
        extends JpaRepository<ProductTypeBean, Integer> {
}
