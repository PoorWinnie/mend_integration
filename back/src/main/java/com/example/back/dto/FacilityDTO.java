package com.example.back.dto;

public class FacilityDTO {
    private Integer facilityId;
    private String facilityName;

    public FacilityDTO(Integer facilityId, String facilityName) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
    }

    public Integer getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }
}
