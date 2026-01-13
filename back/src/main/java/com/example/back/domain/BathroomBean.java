package com.example.back.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "bathroom_options")
public class BathroomBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bathroomId;

	private String bathroomName;
	private String description;
	private String iconName;
	private Integer displayOrder;
	
    @JsonIgnoreProperties("bathroom_options")
    @ManyToMany(mappedBy = "bathrooms")
    @JsonIgnore
    private List<CampAreaBean> campAreas;

	public Integer getBathroomId() {
		return bathroomId;
	}

	public void setBathroomId(Integer bathroomId) {
		this.bathroomId = bathroomId;
	}

	public String getBathroomName() {
		return bathroomName;
	}

	public void setBathroomName(String bathroomName) {
		this.bathroomName = bathroomName;
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
