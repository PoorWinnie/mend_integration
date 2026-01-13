package com.example.back.repository;

import java.time.LocalDate;
import java.util.List;

import com.example.back.domain.CampSpotBean;
import com.example.back.dto.CampSpotSearchRequest;

public interface CampSpotCustomRepository {

    List<CampSpotBean> findAvailableSpotsByFlexibleConditions(CampSpotSearchRequest req);
    
    long countAvailableSpotsByFlexibleConditions(CampSpotSearchRequest req);
}
