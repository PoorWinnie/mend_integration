package com.example.back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.back.domain.ProductBean;

public interface ProductRepository extends JpaRepository<ProductBean, Integer>, ProductInterface {
	Optional<ProductBean> findByName(String name);
}
