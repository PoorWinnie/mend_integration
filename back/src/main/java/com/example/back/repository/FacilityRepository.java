package com.example.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.back.domain.FacilityBean;
@Repository
public interface FacilityRepository extends JpaRepository<FacilityBean, Integer> {
}
