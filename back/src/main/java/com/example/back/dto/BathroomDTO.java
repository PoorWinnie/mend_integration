package com.example.back.dto;

public class BathroomDTO {
    private Integer bathroomId;
    private String bathroomName;

    public BathroomDTO(Integer bathroomId, String bathroomName) {
        this.bathroomId = bathroomId;
        this.bathroomName = bathroomName;
    }

    public Integer getBathroomId() {
        return bathroomId;
    }

    public String getBathroomName() {
        return bathroomName;
    }
}