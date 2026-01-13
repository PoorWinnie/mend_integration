package com.example.back.domain;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "facility_options")
public class FacilityBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer facilityId;

    private String facilityName;

    private String description;

    private String iconName;

    private Integer displayOrder;
    
    
    @ManyToMany(mappedBy = "facilities")
    @JsonIgnore
    private List<CampAreaBean> campAreas;


	public Integer getFacilityId() {
		return facilityId;
	}


	public void setFacilityId(Integer facilityId) {
		this.facilityId = facilityId;
	}


	public String getFacilityName() {
		return facilityName;
	}


	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
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

    // Getter and Setter

    
}