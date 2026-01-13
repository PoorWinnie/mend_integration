package com.example.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "camp_area_off_weekdays")
public class CampAreaOffWeekdayBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camp_area_id", nullable = false)
    @JsonIgnoreProperties("offWeekdays")
    private CampAreaBean campArea;

    @Column(name = "weekday", nullable = false)
    private Integer weekday; // 0 = Sunday, 1 = Monday, ..., 6 = Saturday

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

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }
} 
