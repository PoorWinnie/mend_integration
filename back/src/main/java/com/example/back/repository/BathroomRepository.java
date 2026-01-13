package com.example.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.back.domain.BathroomBean;
@Repository
public interface BathroomRepository extends JpaRepository<BathroomBean, Integer> {

}
