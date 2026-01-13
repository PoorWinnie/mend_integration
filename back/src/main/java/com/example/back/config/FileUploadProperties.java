package com.example.back.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file.upload-dir")
public class FileUploadProperties {
    private String campArea;
    private String toilet;
	public String getCampArea() {
		return campArea;
	}
	public void setCampArea(String campArea) {
		this.campArea = campArea;
	}
	public String getToilet() {
		return toilet;
	}
	public void setToilet(String toilet) {
		this.toilet = toilet;
	}

    // Getter & Setter

}
