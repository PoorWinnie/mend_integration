package com.example.back.dto;

public class ParkingDTO {
    private Integer parkingId;
    private String parkingName;

    public ParkingDTO(Integer parkingId, String parkingName) {
        this.parkingId = parkingId;
        this.parkingName = parkingName;
    }

    public Integer getParkingId() {
        return parkingId;
    }

    public String getParkingName() {
        return parkingName;
    }
}