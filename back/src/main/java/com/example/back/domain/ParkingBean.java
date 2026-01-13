package com.example.back.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_options")
public class ParkingBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer parkingId;

	private String parkingName;
	private String description;
	private String iconName;
	private Integer displayOrder;
	
	@JsonIgnoreProperties("parking_options")
    @ManyToMany(mappedBy = "parkings")
    @JsonIgnore
    private List<CampAreaBean> campAreas;

	public Integer getParkingId() {
		return parkingId;
	}

	public void setParkingId(Integer parkingId) {
		this.parkingId = parkingId;
	}

	public String getParkingName() {
		return parkingName;
	}

	public void setParkingName(String parkingName) {
		this.parkingName = parkingName;
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