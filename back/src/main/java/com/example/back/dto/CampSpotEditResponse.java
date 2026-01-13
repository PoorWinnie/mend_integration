package com.example.back.dto;

import java.math.BigDecimal;

public class CampSpotEditResponse {
    private Integer campSiteId;
    private String campSiteName;
    private String siteType;
    private CampSpotInnerDTO campSpot;
    private String status = "Available";

    public static class CampSpotInnerDTO {
        private Integer campSpotId;
        private String spotName;
        private String areaSize;
        private Integer maxTents;
        private BigDecimal pricePerNight;
        private BigDecimal priceHoliday;
        private Boolean hasElectricity;
        private Boolean hasWater;
        private String description;
        private String toiletImage;
        
        public String getToiletImage() {
            return toiletImage;
        }

        public void setToiletImage(String toiletImage) {
            this.toiletImage = toiletImage;
        }
		public Integer getCampSpotId() {
			return campSpotId;
		}
		public void setCampSpotId(Integer campSpotId) {
			this.campSpotId = campSpotId;
		}
		public String getSpotName() {
			return spotName;
		}
		public void setSpotName(String spotName) {
			this.spotName = spotName;
		}
		public String getAreaSize() {
			return areaSize;
		}
		public void setAreaSize(String areaSize) {
			this.areaSize = areaSize;
		}
		public Integer getMaxTents() {
			return maxTents;
		}
		public void setMaxTents(Integer maxTents) {
			this.maxTents = maxTents;
		}
		public BigDecimal getPricePerNight() {
			return pricePerNight;
		}
		public void setPricePerNight(BigDecimal pricePerNight) {
			this.pricePerNight = pricePerNight;
		}
		public BigDecimal getPriceHoliday() {
			return priceHoliday;
		}
		public void setPriceHoliday(BigDecimal priceHoliday) {
			this.priceHoliday = priceHoliday;
		}
		public Boolean getHasElectricity() {
			return hasElectricity;
		}
		public void setHasElectricity(Boolean hasElectricity) {
			this.hasElectricity = hasElectricity;
		}
		public Boolean getHasWater() {
			return hasWater;
		}
		public void setHasWater(Boolean hasWater) {
			this.hasWater = hasWater;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
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

	public CampSpotInnerDTO getCampSpot() {
		return campSpot;
	}

	public void setCampSpot(CampSpotInnerDTO campSpot) {
		this.campSpot = campSpot;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

   
}
