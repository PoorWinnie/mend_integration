package com.example.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.back.domain.CampAreaOffWeekdayBean;

public interface CampAreaOffWeekdayRepository extends JpaRepository<CampAreaOffWeekdayBean, Integer> {

    @Query("SELECT w.weekday FROM CampAreaOffWeekdayBean w WHERE w.campArea.campAreaId = :campAreaId")
    List<Integer> findWeekdaysByCampAreaId(Integer campAreaId);
    
    void deleteByCampArea_CampAreaId(Integer campAreaId);
    
    
}
