package com.example.back.dto;

import java.time.LocalDate;

public class CampSpotSearchRequest {
    private String region;
    private Integer minAltitude;
    private Integer maxAltitude;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer requiredTents;

    // 分頁與排序用
    private Integer start;
    private Integer rows;
    private String sort;
    private Boolean dir;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getMinAltitude() {
        return minAltitude;
    }

    public void setMinAltitude(Integer minAltitude) {
        this.minAltitude = minAltitude;
    }

    public Integer getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(Integer maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Integer getRequiredTents() {
        return requiredTents;
    }

    public void setRequiredTents(Integer requiredTents) {
        this.requiredTents = requiredTents;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Boolean getDir() {
        return dir;
    }

    public void setDir(Boolean dir) {
        this.dir = dir;
    }
}  
