package com.example.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.back.domain.CampSiteBean;
@Repository
public interface CampSiteRepository extends JpaRepository<CampSiteBean, Integer> {
	List<CampSiteBean> findByCampArea_CampAreaId(Integer campAreaId); //jpa幫寫 select*from camp_sites where camp_area_id = ?
}	
