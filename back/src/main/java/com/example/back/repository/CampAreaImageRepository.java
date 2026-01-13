package com.example.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.back.domain.CampAreaImageBean;


@Repository
public interface CampAreaImageRepository extends JpaRepository< CampAreaImageBean, Integer> {

}
