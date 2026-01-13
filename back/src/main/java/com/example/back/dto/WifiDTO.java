package com.example.back.dto;

public class WifiDTO {
    private Integer wifiId;
    private String wifiName;

    public WifiDTO(Integer wifiId, String wifiName) {
        this.wifiId = wifiId;
        this.wifiName = wifiName;
    }

    public Integer getWifiId() {
        return wifiId;
    }

    public String getWifiName() {
        return wifiName;
    }
}
