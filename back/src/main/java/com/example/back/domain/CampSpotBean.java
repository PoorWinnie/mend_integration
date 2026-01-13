package com.example.back.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
@Table(name = "camp_spots")
public class CampSpotBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer campSpotId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "camp_site_id")
    private CampSiteBean campSite;

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

    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime updatedDate;
    private String status = "Available";
    
    @OneToMany(mappedBy = "campSpot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingBean> bookings;
    
    
    
    
    
	public List<BookingBean> getBookings() {
		return bookings;
	}
	public void setBookings(List<BookingBean> bookings) {
		this.bookings = bookings;
	}
	public Integer getCampSpotId() {
		return campSpotId;
	}
	public void setCampSpotId(Integer campSpotId) {
		this.campSpotId = campSpotId;
	}
	public CampSiteBean getCampSite() {
		return campSite;
	}
	public void setCampSite(CampSiteBean campSite) {
		this.campSite = campSite;
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
    
    
	

    
}