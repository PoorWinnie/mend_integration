package com.example.back.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CampSpotInfoDTO {
    private String spotName;
    private String campSiteName;
    private String campAreaName;
    private BigDecimal priceWeekend;
    private String imageUrl;

    // Constructor
    public CampSpotInfoDTO(String spotName, String campSiteName, String campAreaName,
                           BigDecimal priceWeekend, String imageUrl) {
        this.spotName = spotName;
        this.campSiteName = campSiteName;
        this.campAreaName = campAreaName;
        this.priceWeekend = priceWeekend;
        this.imageUrl = imageUrl;
    }

	public String getSpotName() {
		return spotName;
	}

	public void setSpotName(String spotName) {
		this.spotName = spotName;
	}

	public String getCampSiteName() {
		return campSiteName;
	}

	public void setCampSiteName(String campSiteName) {
		this.campSiteName = campSiteName;
	}

	public String getCampAreaName() {
		return campAreaName;
	}

	public void setCampAreaName(String campAreaName) {
		this.campAreaName = campAreaName;
	}

	public BigDecimal getPriceWeekend() {
		return priceWeekend;
	}

	public void setPriceWeekend(BigDecimal priceWeekend) {
		this.priceWeekend = priceWeekend;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
    
    

}
