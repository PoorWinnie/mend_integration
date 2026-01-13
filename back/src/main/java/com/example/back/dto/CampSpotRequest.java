package com.example.back.dto;

import java.math.BigDecimal;

public class CampSpotRequest {
    private Integer campSiteId;
    private String spotNumber;
    private String spotName;
    private String description;
    private String areaSize;
    private Integer maxTents;
    private Integer maxPeople;
    private BigDecimal pricePerNight;
    private BigDecimal priceWeekend;
    private BigDecimal priceHoliday;
    private Boolean hasElectricity;
    private Boolean hasWater;
    private String toiletImage;
    
    private Integer campSpotId;

	public Integer getCampSpotId() {
		return campSpotId;
	}
	public void setCampSpotId(Integer campSpotId) {
		this.campSpotId = campSpotId;
	}
	public Integer getCampSiteId() {
		return campSiteId;
	}
	public void setCampSiteId(Integer campSiteId) {
		this.campSiteId = campSiteId;
	}
	public String getSpotNumber() {
		return spotNumber;
	}
	public void setSpotNumber(String spotNumber) {
		this.spotNumber = spotNumber;
	}
	public String getSpotName() {
		return spotName;
	}
	public void setSpotName(String spotName) {
		this.spotName = spotName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public Integer getMaxPeople() {
		return maxPeople;
	}
	public void setMaxPeople(Integer maxPeople) {
		this.maxPeople = maxPeople;
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
	public String getToiletImage() {
		return toiletImage;
	}
	public void setToiletImage(String toiletImage) {
		this.toiletImage = toiletImage;
	}
    
    
    
}