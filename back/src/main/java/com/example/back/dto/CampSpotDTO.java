package com.example.back.dto;

import java.math.BigDecimal;

import com.example.back.domain.CampSpotBean;

public class CampSpotDTO {
	private Integer campSpotId;
	private String spotName;
    private String areaSize;
    private Integer maxTents;
    private BigDecimal pricePerNight;
    private BigDecimal priceHoliday;
    private String toiletImage;
    private Boolean hasElectricity;
    private Boolean hasWater;
    private String description;
    

    public CampSpotDTO(CampSpotBean bean) {
    	this.campSpotId = bean.getCampSpotId();
        this.areaSize = bean.getAreaSize();
        this.maxTents = bean.getMaxTents();
        this.pricePerNight = bean.getPricePerNight();
        this.priceHoliday = bean.getPriceHoliday();
        this.spotName = bean.getSpotName();
        this.toiletImage = bean.getToiletImage();
        this.hasElectricity = bean.getHasElectricity();
        this.hasWater = bean.getHasWater();
        this.description = bean.getDescription();
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


	public String getToiletImage() {
		return toiletImage;
	}


	public void setToiletImage(String toiletImage) {
		this.toiletImage = toiletImage;
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