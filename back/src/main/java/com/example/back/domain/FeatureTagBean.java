package com.example.back.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "feature_tags")
public class FeatureTagBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tagId;

    private String tagName;
    private String tagCategory;
    private String tagIcon;
    private String tagDescription;
    private Integer displayOrder;
    private Boolean isActive;
    
    @JsonIgnoreProperties("feature_tags")
    @ManyToMany(mappedBy = "features", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CampAreaBean> campAreas;

	public Integer getTagId() {
		return tagId;
	}

	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagCategory() {
		return tagCategory;
	}

	public void setTagCategory(String tagCategory) {
		this.tagCategory = tagCategory;
	}

	public String getTagIcon() {
		return tagIcon;
	}

	public void setTagIcon(String tagIcon) {
		this.tagIcon = tagIcon;
	}

	public String getTagDescription() {
		return tagDescription;
	}

	public void setTagDescription(String tagDescription) {
		this.tagDescription = tagDescription;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public List<CampAreaBean> getCampAreas() {
		return campAreas;
	}

	public void setCampAreas(List<CampAreaBean> campAreas) {
		this.campAreas = campAreas;
	}

	
    
}
