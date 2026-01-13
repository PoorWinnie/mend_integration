package com.example.back.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "camp_areas")
public class CampAreaBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer campAreaId;

	private String campAreaName;
	private String description;
	private String location;

	@Column(name = "coordinates")
	private String coordinates;

	private String Address;
	private String Phone;
	private String Email;
	private String WebsiteUrl;

	private LocalDateTime createdDate = LocalDateTime.now();
	private LocalDateTime updatedDate;
	private String status = "Active";

	private String notice;
	private String transportation;
	private Integer altitude;

	@Column(name = "toilet_image")
	private String toiletImage;

	@OneToMany(mappedBy = "campArea", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<CampSiteBean> campSites;

	@OneToMany(mappedBy = "campArea", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CampAreaImageBean> campAreaImages;
	
	@JsonIgnoreProperties("campArea")
	@OneToMany(mappedBy = "campArea", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CampAreaOffWeekdayBean> offWeekdays;
	
	@JsonIgnoreProperties("campArea")
	@OneToMany(mappedBy = "campArea", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CampAreaOffDayBean> offDays;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
	@JsonIgnoreProperties("campArea") // 防止循環爆炸
	private UserBean user;

	@JsonIgnoreProperties("camp_areas")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "camp_area_facility_options", joinColumns = @JoinColumn(name = "camp_area_id"), inverseJoinColumns = @JoinColumn(name = "facility_id"))
	private List<FacilityBean> facilities;

	@JsonIgnoreProperties("camp_areas")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "camp_area_wifi_options", joinColumns = @JoinColumn(name = "camp_area_id"), inverseJoinColumns = @JoinColumn(name = "wifi_id"))
	private List<WifiBean> wifis;

	@JsonIgnoreProperties("camp_areas")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "camp_area_bathroom_options", joinColumns = @JoinColumn(name = "camp_area_id"), inverseJoinColumns = @JoinColumn(name = "bathroom_id"))
	private List<BathroomBean> bathrooms;

	@JsonIgnoreProperties("camp_areas")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "camp_area_parking_options", joinColumns = @JoinColumn(name = "camp_area_id"), inverseJoinColumns = @JoinColumn(name = "parking_id"))
	private List<ParkingBean> parkings;

	@JsonIgnoreProperties("camp_areas")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "camp_area_pet_options", joinColumns = @JoinColumn(name = "camp_area_id"), inverseJoinColumns = @JoinColumn(name = "policy_id"))
	private List<PetBean> petPolicies;

	@JsonIgnoreProperties("camp_areas")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "camp_area_features_options", joinColumns = @JoinColumn(name = "camp_area_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<FeatureTagBean> features;

	public Integer getCampAreaId() {
		return campAreaId;
	}

	public void setCampAreaId(Integer campAreaId) {
		this.campAreaId = campAreaId;
	}

	public String getCampAreaName() {
		return campAreaName;
	}

	public void setCampAreaAame(String campAreaName) {
		this.campAreaName = campAreaName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getWebsiteUrl() {
		return WebsiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		WebsiteUrl = websiteUrl;
	}

	public void setCampAreaName(String campAreaName) {
		this.campAreaName = campAreaName;
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

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public String getTransportation() {
		return transportation;
	}

	public void setTransportation(String transportation) {
		this.transportation = transportation;
	}

	public Integer getAltitude() {
		return altitude;
	}

	public void setAltitude(Integer altitude) {
		this.altitude = altitude;
	}

	public String getToiletImage() {
		return toiletImage;
	}

	public void setToiletImage(String toiletImage) {
		this.toiletImage = toiletImage;
	}

	public Set<CampSiteBean> getCampSites() {
		return campSites;
	}

	public void setCampSites(Set<CampSiteBean> campSites) {
		this.campSites = campSites;
	}

	public List<CampAreaImageBean> getCampAreaImages() {
		return campAreaImages;
	}

	public void setCampAreaImages(List<CampAreaImageBean> campAreaImages) {
		this.campAreaImages = campAreaImages;
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public List<FacilityBean> getFacilities() {
		return facilities;
	}

	public void setFacilities(List<FacilityBean> facilities) {
		this.facilities = facilities;
	}

	public List<WifiBean> getWifis() {
		return wifis;
	}

	public void setWifis(List<WifiBean> wifis) {
		this.wifis = wifis;
	}

	public List<BathroomBean> getBathrooms() {
		return bathrooms;
	}

	public void setBathrooms(List<BathroomBean> bathrooms) {
		this.bathrooms = bathrooms;
	}

	public List<ParkingBean> getParkings() {
		return parkings;
	}

	public void setParkings(List<ParkingBean> parkings) {
		this.parkings = parkings;
	}

	public List<PetBean> getPetPolicies() {
		return petPolicies;
	}

	public void setPetPolicies(List<PetBean> petPolicies) {
		this.petPolicies = petPolicies;
	}

	public List<FeatureTagBean> getFeatures() {
		return features;
	}

	public void setFeatures(List<FeatureTagBean> features) {
		this.features = features;
	}
	
	public List<CampAreaOffWeekdayBean> getOffWeekdays() {
	    return offWeekdays;
	}

	public void setOffWeekdays(List<CampAreaOffWeekdayBean> offWeekdays) {
	    this.offWeekdays = offWeekdays;
	}

	public List<CampAreaOffDayBean> getOffDays() {
	    return offDays;
	}

	public void setOffDays(List<CampAreaOffDayBean> offDays) {
	    this.offDays = offDays;
	}
	// getter setter

}