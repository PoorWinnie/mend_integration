package com.example.back.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CampSiteRequest {
    private Integer campAreaId;
    private String campSiteName;
    private String description;
    private String siteType;
    private Integer maxCapacity;
    private String recommendedFor;
    private String status;
    private LocalDateTime updatedDate;
    
    private List<CampSpotRequest> campSpots;
    

    public List<CampSpotRequest> getCampSpots() {
        return campSpots;
    }

    public void setCampSpots(List<CampSpotRequest> campSpots) {
        this.campSpots = campSpots;
    }
    
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Integer getCampAreaId() {
		return campAreaId;
	}
	public void setCampAreaId(Integer campAreaId) {
		this.campAreaId = campAreaId;
	}
	public String getCampSiteName() {
		return campSiteName;
	}
	public void setCampSiteName(String campSiteName) {
		this.campSiteName = campSiteName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSiteType() {
		return siteType;
	}
	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}
	public Integer getMaxCapacity() {
		return maxCapacity;
	}
	public void setMaxCapacity(Integer maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
	public String getRecommendedFor() {
		return recommendedFor;
	}
	public void setRecommendedFor(String recommendedFor) {
		this.recommendedFor = recommendedFor;
	}
    
    
}