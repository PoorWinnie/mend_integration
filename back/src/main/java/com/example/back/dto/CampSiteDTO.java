package com.example.back.dto;

import java.util.List;

import com.example.back.domain.CampSiteBean;

public class CampSiteDTO {
    private Integer campSiteId;
    private String campSiteName;
    private String siteType;
    private List<CampSpotDTO> campSpots;

    public CampSiteDTO(CampSiteBean bean) {
        this.campSiteId = bean.getCampSiteId();
        this.campSiteName = bean.getCampSiteName();
        this.siteType = bean.getSiteType();

        if (bean.getCampSpots() != null) {
            this.campSpots = bean.getCampSpots().stream()
                .map(CampSpotDTO::new)
                .toList();
        }
    }

	public Integer getCampSiteId() {
		return campSiteId;
	}

	public void setCampSiteId(Integer campSiteId) {
		this.campSiteId = campSiteId;
	}

	public String getCampSiteName() {
		return campSiteName;
	}

	public void setCampSiteName(String campSiteName) {
		this.campSiteName = campSiteName;
	}

	public String getSiteType() {
		return siteType;
	}

	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}

	public List<CampSpotDTO> getCampSpots() {
		return campSpots;
	}

	public void setCampSpots(List<CampSpotDTO> campSpots) {
		this.campSpots = campSpots;
	}

	

    // Getters & Setters ...
    
    
}
