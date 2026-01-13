package com.example.back.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "pet_policies")
public class PetBean {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policyId")
    private Integer policyId;

    @Column(name = "policy_name", nullable = false, unique = true)
    private String policyName;

    @Column(name = "description")
    private String description;

    @Column(name = "icon_name")
    private String iconName;

    @Column(name = "display_order", columnDefinition = "int default 0")
    private Integer displayOrder;
    
    @JsonIgnoreProperties("pet_policies")
    @ManyToMany(mappedBy = "petPolicies")
    @JsonIgnore
    private List<CampAreaBean> campAreas;

	public Integer getPolicyId() {
		return policyId;
	}

	public void setPolicyId(Integer policyId) {
		this.policyId = policyId;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
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

    // === Getter and Setter ===
    
    
    
}
