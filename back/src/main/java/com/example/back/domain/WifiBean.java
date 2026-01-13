package com.example.back.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "wifi_options")
public class WifiBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer wifiId;

	private String wifiName;
	private String description;
	private String iconName;
	private Integer displayOrder;
	
    @JsonIgnoreProperties("wifi_options")
    @ManyToMany(mappedBy = "wifis")
    @JsonIgnore
    private List<CampAreaBean> campAreas;

	public Integer getWifiId() {
		return wifiId;
	}

	public void setWifiId(Integer wifiId) {
		this.wifiId = wifiId;
	}

	public String getWifiName() {
		return wifiName;
	}

	public void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public List<CampAreaBean> getCampAreas() {
		return campAreas;
	}

	public void setCampAreas(List<CampAreaBean> campAreas) {
		this.campAreas = campAreas;
	}

	// Getters and Setters
	
}
