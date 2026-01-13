package com.example.back.domain;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "camp_sites")
public class CampSiteBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer campSiteId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "camp_area_id")
    private CampAreaBean campArea;

    private String campSiteName;

   
    private String description;

    private String siteType;
    private Integer maxCapacity;
    private String recommendedFor;
    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime updatedDate;
    private String status = "Available";

    @OneToMany(mappedBy = "campSite", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<CampSpotBean> campSpots;

	public Integer getCampSiteId() {
		return campSiteId;
	}

	public void setCampSiteId(Integer campSiteId) {
		this.campSiteId = campSiteId;
	}

	public CampAreaBean getCampArea() {
		return campArea;
	}

	public void setCampArea(CampAreaBean campArea) {
		this.campArea = campArea;
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



	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Set<CampSpotBean> getCampSpots() {
		return campSpots;
	}

	public void setCampSpots(Set<CampSpotBean> campSpots) {
		this.campSpots = campSpots;
	}

	

    
}

