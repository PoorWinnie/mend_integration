package com.example.back.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.back.domain.BookingBean;

public interface BookingRepository extends JpaRepository<BookingBean, Integer> {

    // 查詢某營位在特定時間範圍內是否已有預約（用於避免重複訂位）
    @Query("SELECT b FROM BookingBean b " +
           "WHERE b.campSpot.campSpotId = :campSpotId " +
           "AND (:checkIn < b.checkOutDate AND :checkOut > b.checkInDate)")
    List<BookingBean> findOverlappingBookings(Integer campSpotId, LocalDate checkIn, LocalDate checkOut);

    // 查詢某段期間內，全部重疊的訂單（用於計算帳數） (查出所有資料，程式中再加總)
    @Query("SELECT b FROM BookingBean b " +
           "WHERE b.campSpot.campSpotId IN :spotIds " +
           "AND (:checkIn < b.checkOutDate AND :checkOut > b.checkInDate)")
    List<BookingBean> findAllOverlappingInSpots(List<Integer> spotIds, LocalDate checkIn, LocalDate checkOut);
    
    
    //用來計算某個營位在指定日期範圍內已經被預約了幾帳  (資料庫中直接加總)
    @Query("SELECT COALESCE(SUM(b.tentCount), 0) FROM BookingBean b " +
    	       "WHERE b.campSpot.campSpotId = :campSpotId " +
    	       "AND (:checkIn < b.checkOutDate AND :checkOut > b.checkInDate)")
    	int sumTentCountInDateRange(Integer campSpotId, LocalDate checkIn, LocalDate checkOut);

}
