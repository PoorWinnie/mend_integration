package com.example.back.repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.back.domain.CampAreaOffDayBean;

public interface CampAreaOffDayRepository extends JpaRepository<CampAreaOffDayBean, Integer> {

    @Query("SELECT COUNT(d) FROM CampAreaOffDayBean d " +
           "WHERE d.campArea.campAreaId = :campAreaId " +
           "AND d.offDate >= :checkIn AND d.offDate < :checkOut")
    int countOffDaysBetween(Integer campAreaId, LocalDate checkIn, LocalDate checkOut);
    
    void deleteByCampArea_CampAreaId(Integer campAreaId);
    
    @Query("SELECT c.offDate FROM CampAreaOffDayBean c WHERE c.campArea.id = :areaId AND c.offDate BETWEEN :start AND :end")
    List<LocalDate> findOffDaysBetween(@Param("areaId") Integer id,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

}

