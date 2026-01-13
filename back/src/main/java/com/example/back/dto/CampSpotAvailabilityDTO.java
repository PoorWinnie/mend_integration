package com.example.back.dto;

import java.math.BigDecimal;

public class CampSpotAvailabilityDTO {
    private Integer campSpotId;
    private Integer campAreaId;
    private String campAreaName;
    private String campAreaImageUrl;
    private String spotName;
    private Integer maxTents;
    private Integer availableTents;
    private boolean isAvailable;
    private boolean hasOffDays;
    private BigDecimal pricePerNight;
    private BigDecimal priceWeekend;
    private String note;
    private String Address;
    
    
    
    
    public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getCampAreaName() {
		return campAreaName;
	}

	public void setCampAreaName(String campAreaName) {
		this.campAreaName = campAreaName;
	}

	public String getCampAreaImageUrl() {
		return campAreaImageUrl;
	}

	public void setCampAreaImageUrl(String campAreaImageUrl) {
		this.campAreaImageUrl = campAreaImageUrl;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public Integer getCampSpotId() {
        return campSpotId;
    }

    public void setCampSpotId(Integer campSpotId) {
        this.campSpotId = campSpotId;
    }

    public Integer getCampAreaId() {
        return campAreaId;
    }

    public void setCampAreaId(Integer campAreaId) {
        this.campAreaId = campAreaId;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public Integer getMaxTents() {
        return maxTents;
    }

    public void setMaxTents(Integer maxTents) {
        this.maxTents = maxTents;
    }

    public Integer getAvailableTents() {
        return availableTents;
    }

    public void setAvailableTents(Integer availableTents) {
        this.availableTents = availableTents;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public boolean isHasOffDays() {
        return hasOffDays;
    }

    public void setHasOffDays(boolean hasOffDays) {
        this.hasOffDays = hasOffDays;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public BigDecimal getPriceWeekend() {
        return priceWeekend;
    }

    public void setPriceWeekend(BigDecimal priceWeekend) {
        this.priceWeekend = priceWeekend;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}  
