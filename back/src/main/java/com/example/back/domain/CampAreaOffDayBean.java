package com.example.back.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "camp_area_off_days")
public class CampAreaOffDayBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camp_area_id")
    @JsonIgnoreProperties("offDays")
    private CampAreaBean campArea;

    @Column(name = "off_date")
    private LocalDate offDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CampAreaBean getCampArea() {
		return campArea;
	}

	public void setCampArea(CampAreaBean campArea) {
		this.campArea = campArea;
	}

	public LocalDate getOffDate() {
		return offDate;
	}

	public void setOffDate(LocalDate offDate) {
		this.offDate = offDate;
	}

    // getters/setters
    
    
}
