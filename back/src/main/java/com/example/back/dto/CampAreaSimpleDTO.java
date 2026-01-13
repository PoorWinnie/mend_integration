package com.example.back.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.back.domain.CampAreaBean;
import com.example.back.domain.CampAreaImageBean;
import com.example.back.domain.FeatureTagBean;

public class CampAreaSimpleDTO {
    private Integer campAreaId;
    private String campAreaName;
    private String location;
    private String address;
    private String phone;
    private Integer altitude;
    private String status;
    private Integer userId;
    private List<CampAreaImageBean> mainImageUrl;
    private List<String> featureTagNames;

    // constructor
    public CampAreaSimpleDTO(CampAreaBean bean) {
        this.campAreaId = bean.getCampAreaId();
        this.campAreaName = bean.getCampAreaName();
        this.location = bean.getLocation();
        this.address = bean.getAddress();
        this.phone = bean.getPhone();
        this.altitude = bean.getAltitude();
        this.mainImageUrl = bean.getCampAreaImages();
        this.userId = bean.getUser().getId();
        this.status = bean.getStatus();


        if (bean.getFeatures() != null) {
            this.featureTagNames = bean.getFeatures()
                .stream()
                .map((FeatureTagBean tag) -> tag.getTagName()) // 強制型別
                .collect(Collectors.toList());
        }

    }
    
    
    
	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public Integer getCampAreaId() {
		return campAreaId;
	}

	public void setCampAreaId(Integer campAreaId) {
		this.campAreaId = campAreaId;
	}

	public String getCampAreaName() {
		return campAreaName;
	}

	public void setCampAreaName(String campAreaName) {
		this.campAreaName = campAreaName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public Integer getAltitude() {
		return altitude;
	}

	public void setAltitude(Integer altitude) {
		this.altitude = altitude;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public List<CampAreaImageBean> getMainImageUrl() {
		return mainImageUrl;
	}

	public void setMainImageUrl(List<CampAreaImageBean> mainImageUrl) {
		this.mainImageUrl = mainImageUrl;
	}

	public List<String> getFeatureTagNames() {
		return featureTagNames;
	}

	public void setFeatureTagNames(List<String> featureTagNames) {
		this.featureTagNames = featureTagNames;
	}
        
        
    // Getters & Setters 
    
    




    
}
