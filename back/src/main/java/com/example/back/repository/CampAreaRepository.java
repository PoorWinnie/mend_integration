package com.example.back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.back.domain.CampAreaBean;
@Repository
public interface CampAreaRepository extends JpaRepository<CampAreaBean, Integer> , CampAreaInterface{
	List<CampAreaBean> findByUserId(Integer userId);
	
	@Modifying
	@Query("UPDATE CampAreaBean c SET c.status = :status WHERE c.campAreaId = :id AND c.user.username = :username")
	int updateStatusByIdAndUsername(@Param("id") Integer id,
	                                @Param("status") String status,
	                                @Param("username") String username);
	@Query("""
		    SELECT DISTINCT ca
		    FROM CampAreaBean ca
		    LEFT JOIN FETCH ca.campSites cs
		    LEFT JOIN FETCH cs.campSpots
		    WHERE ca.user.id = :userId
		""")
		List<CampAreaBean> findByUserIdWithSites(@Param("userId") Integer userId);

}
