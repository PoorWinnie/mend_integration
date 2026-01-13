package com.example.back.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.back.domain.BookingBean;


public interface BookingCalendarRepository extends JpaRepository<BookingBean, Integer> {
    @Query("""
        SELECT b.checkInDate, SUM(b.tentCount)
        FROM BookingBean b
        WHERE b.campSpot.campSite.campArea.id = :areaId AND b.checkInDate BETWEEN :start AND :end
        GROUP BY b.checkInDate
    """)
    List<Object[]> sumTentCountByCampAreaAndDateRange(@Param("areaId") Integer areaId,
                                                       @Param("start") LocalDate start,
                                                       @Param("end") LocalDate end);
}

