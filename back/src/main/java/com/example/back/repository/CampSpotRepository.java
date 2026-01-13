package com.example.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.back.domain.CampSpotBean;
@Repository
public interface CampSpotRepository extends JpaRepository<CampSpotBean, Integer> , CampSpotCustomRepository{
	List<CampSpotBean> findByCampSite_CampSiteId(Integer campSiteId);
	
	@Query("SELECT cs FROM CampSpotBean cs " +
		       "JOIN cs.campSite site " +
		       "JOIN site.campArea area " +
		       "WHERE SUBSTRING(area.location, 1, 3) = :region " +
		       "AND area.altitude BETWEEN :minAltitude AND :maxAltitude " +	
		       "AND cs.maxTents >= :requiredTents")
		List<CampSpotBean> findByRegionAndAltitudeAndTentCount(
		    String region, Integer minAltitude, Integer maxAltitude, Integer requiredTents);
	
	@Query("SELECT cs FROM CampSpotBean cs " +
	           "JOIN FETCH cs.campSite csite " +
	           "JOIN FETCH csite.campArea carea " +
	           "WHERE cs.campSpotId = :campSpotId")
	    CampSpotBean findFullSpotInfoById(@Param("campSpotId") Integer campSpotId);
	
	 @Query("SELECT c FROM CampSpotBean c WHERE c.campSite.campArea.id = :areaId")
	    List<CampSpotBean> findByCampAreaId(@Param("areaId") Integer areaId);

}
