package com.example.back.dto;

import java.time.LocalDate;
import java.util.List;

public class CampAreaRequest {

    private String campAreaName;
    private String description;
    private String location;
    private String coordinates;
    private String address;
    private String phone;
    private String email;
    private String websiteUrl;
    private String notice;
    private String transportation;
    private Integer altitude;
    private String toiletImage;

    private List<Integer> featureTagIds;
    private List<Integer> parkingIds;
    private List<Integer> facilityIds;
    private List<Integer> wifiIds;
    private List<Integer> bathroomIds;
    private List<Integer> petPolicyIds;

    private List<String> mainImageUrls; // 上傳圖片時傳url路徑（可選）
    
    private List<Integer> offWeekdays; // 0=Sun ... 6=Sat
    private List<LocalDate> offDates;

    
    private boolean removeMainImage;
    private List<Integer> removeImageIds;
    private String status;


    private Integer userId; // 營主的ID

    // === Getters & Setters ===
    
    
    
    public boolean isRemoveMainImage() {
        return removeMainImage;
    }

    public List<Integer> getOffWeekdays() {
		return offWeekdays;
	}

	public void setOffWeekdays(List<Integer> offWeekdays) {
		this.offWeekdays = offWeekdays;
	}

	public List<LocalDate> getOffDates() {
		return offDates;
	}

	public void setOffDates(List<LocalDate> offDates) {
		this.offDates = offDates;
	}

	public void setRemoveMainImage(boolean removeMainImage) {
        this.removeMainImage = removeMainImage;
    }

    public List<Integer> getRemoveImageIds() {
        return removeImageIds;
    }

    public void setRemoveImageIds(List<Integer> removeImageIds) {
        this.removeImageIds = removeImageIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getCampAreaName() {
        return campAreaName;
    }

    public void setCampAreaName(String campAreaName) {
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
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
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

    public List<Integer> getFeatureTagIds() {
        return featureTagIds;
    }

    public void setFeatureTagIds(List<Integer> featureTagIds) {
        this.featureTagIds = featureTagIds;
    }

    public List<Integer> getParkingIds() {
        return parkingIds;
    }

    public void setParkingIds(List<Integer> parkingIds) {
        this.parkingIds = parkingIds;
    }

    public List<Integer> getFacilityIds() {
        return facilityIds;
    }

    public void setFacilityIds(List<Integer> facilityIds) {
        this.facilityIds = facilityIds;
    }

    public List<Integer> getWifiIds() {
        return wifiIds;
    }

    public void setWifiIds(List<Integer> wifiIds) {
        this.wifiIds = wifiIds;
    }

    public List<Integer> getBathroomIds() {
        return bathroomIds;
    }

    public void setBathroomIds(List<Integer> bathroomIds) {
        this.bathroomIds = bathroomIds;
    }

    public List<Integer> getPetPolicyIds() {
        return petPolicyIds;
    }

    public void setPetPolicyIds(List<Integer> petPolicyIds) {
        this.petPolicyIds = petPolicyIds;
    }

    public List<String> getMainImageUrls() {
        return mainImageUrls;
    }

    public void setMainImageUrls(List<String> mainImageUrls) {
        this.mainImageUrls = mainImageUrls;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
