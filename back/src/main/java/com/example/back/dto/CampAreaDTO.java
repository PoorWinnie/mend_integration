package com.example.back.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.back.domain.BathroomBean;
import com.example.back.domain.CampAreaBean;
import com.example.back.domain.CampAreaImageBean;
import com.example.back.domain.CampAreaOffDayBean;
import com.example.back.domain.CampAreaOffWeekdayBean;
import com.example.back.domain.FacilityBean;
import com.example.back.domain.FeatureTagBean;
import com.example.back.domain.ParkingBean;
import com.example.back.domain.PetBean;
import com.example.back.domain.WifiBean;

public class CampAreaDTO {
    private Integer campAreaId;
    private String campAreaName;
    private String description;
    private String location;
    private String coordinates;
    private String address;
    private String phone;
    private String email;
    private String websiteUrl;
    private String status;
    private String notice;
    private String transportation;
    private Integer altitude;
    private Integer userId;
    private String ToiletImage;
    

    // 多對多關聯 ID 傳遞欄位
    private List<Integer> featureTagIds;
    private List<Integer> parkingIds;
    private List<Integer> facilityIds;
    private List<Integer> wifiIds;
    private List<Integer> bathroomIds;
    private List<Integer> petPolicyIds;
    
    private List<String> facilityNames;
    private List<String> featureTagNames;
    private List<String> wifiNames;
    private List<String> parkingNames;
    private List<String> petPolicyNames;
    private List<String> bathroomNames;
    
	//一對多關聯欄位
    private List<CampAreaImageBean> mainImageUrl;
    private List<CampSiteDTO> campSites;
    private List<Integer> offWeekdays;     // 只存 weekday 整數值，例如 0 = Sunday
    private List<LocalDate> offDates;      // 特定日期


    
    
    public CampAreaDTO(CampAreaBean entity) {
        if (entity == null) {
            return;
        }

        this.campAreaId = entity.getCampAreaId();
        this.campAreaName = entity.getCampAreaName();
        this.description = entity.getDescription();
        this.location = entity.getLocation();
        this.coordinates = entity.getCoordinates();
        this.address = entity.getAddress();
        this.phone = entity.getPhone();
        this.email = entity.getEmail();
        this.websiteUrl = entity.getWebsiteUrl();
        this.status = entity.getStatus();
        this.notice = entity.getNotice();
        this.transportation = entity.getTransportation();
        this.altitude = entity.getAltitude();
        this.ToiletImage = entity.getToiletImage();
        this.userId = (entity.getUser() != null) ? entity.getUser().getId() : null;
        this.mainImageUrl = entity.getCampAreaImages();


        // ⭐️ 多對多的 ID列表
        
        
        
        if (entity.getFeatures() != null) {
            this.featureTagIds = entity.getFeatures().stream()
                    .map(FeatureTagBean::getTagId)
                    .toList();
        }

        if (entity.getParkings() != null) {
            this.parkingIds = entity.getParkings().stream()
                    .map(ParkingBean::getParkingId)
                    .toList();
        }

        if (entity.getFacilities() != null) {
            this.facilityIds = entity.getFacilities().stream()
                    .map(FacilityBean::getFacilityId)
                    .toList();
        }

        if (entity.getWifis() != null) {
            this.wifiIds = entity.getWifis().stream()
                    .map(WifiBean::getWifiId)
                    .toList();
        }

        if (entity.getBathrooms() != null) {
            this.bathroomIds = entity.getBathrooms().stream()
                    .map(BathroomBean::getBathroomId)
                    .toList();
        }

        if (entity.getPetPolicies() != null) {
            this.petPolicyIds = entity.getPetPolicies().stream()
                    .map(PetBean::getPolicyId)
                    .toList();
        }
        if (entity.getCampSites() != null) {
            this.campSites = entity.getCampSites().stream()
                .map(CampSiteDTO::new)
                .toList();
        }
        
        if (entity.getFacilities() != null) {
            this.facilityIds = entity.getFacilities().stream()
                .map(FacilityBean::getFacilityId)
                .toList();

            this.facilityNames = entity.getFacilities().stream()
                .map(FacilityBean::getFacilityName)
                .toList();
        }
        if (entity.getFeatures() != null) {
            this.featureTagIds = entity.getFeatures().stream()
                .map(FeatureTagBean::getTagId)
                .toList();

            this.featureTagNames = entity.getFeatures().stream()
                .map(FeatureTagBean::getTagName)
                .toList();
        }
        if (entity.getWifis() != null) {
        	this.wifiIds = entity.getWifis().stream()
        			.map(WifiBean::getWifiId)
        			.toList();
        	
        	this.wifiNames = entity.getWifis().stream()
        			.map(WifiBean::getWifiName)
        			.toList();
        }
        if (entity.getPetPolicies() != null) {
        	this.petPolicyIds = entity.getPetPolicies().stream()
        			.map(PetBean::getPolicyId)
        			.toList();
        	
        	this.petPolicyNames = entity.getPetPolicies().stream()
        			.map(PetBean::getPolicyName)
        			.toList();
        }
        
        if (entity.getParkings() != null) {
        	this.parkingIds = entity.getParkings().stream()
        			.map(ParkingBean::getParkingId)
        			.toList();
        	
        	this.parkingNames = entity.getParkings().stream()
        			.map(ParkingBean::getParkingName)
        			.toList();
        }
        
     // ======= 固定休假 weekday 轉出 =======
        if (entity.getOffWeekdays() != null) {
            this.offWeekdays = entity.getOffWeekdays().stream()
                .map(CampAreaOffWeekdayBean::getWeekday)
                .toList();
        }

        // ======= 特定休假日期轉出 =======
        if (entity.getOffDays() != null) {
            this.offDates = entity.getOffDays().stream()
                .map(CampAreaOffDayBean::getOffDate)
                .toList();
        }

        

        

    }
    // ==== Getters & Setters ====

    
    
	public List<CampAreaImageBean> getMainImageUrl() {
		return mainImageUrl;
	}

	public List<Integer> getOffWeekdays() {
		return offWeekdays;
	}



	public void setOffWeekdays(List<Integer> offWeekdays) {
		this.offWeekdays = offWeekdays;
	}



	public List<LocalDate> getOffDates() {
		return offDates;
	}



	public void setOffDates(List<LocalDate> offDates) {
		this.offDates = offDates;
	}



	public void setMainImageUrl(List<CampAreaImageBean> mainImageUrl) {
		this.mainImageUrl = mainImageUrl;
	}



	public void setToiletImage(String toiletImage) {
		ToiletImage = toiletImage;
	}
	
    public String getToiletImage() {
		return ToiletImage;
	}


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Integer> getFeatureTagIds() {
        return featureTagIds;
    }

    public void setFeatureTagIds(List<Integer> featureTagIds) {
        this.featureTagIds = featureTagIds;
    }

    public List<Integer> getParkingIds() {
        return parkingIds;
    }

    public void setParkingIds(List<Integer> parkingIds) {
        this.parkingIds = parkingIds;
    }

    public List<Integer> getFacilityIds() {
        return facilityIds;
    }

    public void setFacilityIds(List<Integer> facilityIds) {
        this.facilityIds = facilityIds;
    }

    public List<Integer> getWifiIds() {
        return wifiIds;
    }

    public void setWifiIds(List<Integer> wifiIds) {
        this.wifiIds = wifiIds;
    }

    public List<Integer> getBathroomIds() {
        return bathroomIds;
    }

    public void setBathroomIds(List<Integer> bathroomIds) {
        this.bathroomIds = bathroomIds;
    }

    public List<Integer> getPetPolicyIds() {
        return petPolicyIds;
    }

    public void setPetPolicyIds(List<Integer> petPolicyIds) {
        this.petPolicyIds = petPolicyIds;
    }
	public List<CampSiteDTO> getCampSites() {
		return campSites;
	}

	public void setCampSites(List<CampSiteDTO> campSites) {
		this.campSites = campSites;
	}
	public List<String> getFacilityNames() {
		return facilityNames;
	}
	public void setFacilityNames(List<String> facilityNames) {
		this.facilityNames = facilityNames;
	}
	public List<String> getFeatureTagNames() {
		return featureTagNames;
	}
	public void setFeatureTagNames(List<String> featureTagNames) {
		this.featureTagNames = featureTagNames;
	}
	public List<String> getWifiNames() {
		return wifiNames;
	}
	public void setWifiNames(List<String> wifiNames) {
		this.wifiNames = wifiNames;
	}
	public List<String> getParkingNames() {
		return parkingNames;
	}
	public void setParkingNames(List<String> parkingNames) {
		this.parkingNames = parkingNames;
	}
	public List<String> getPetPolicyNames() {
		return petPolicyNames;
	}
	public void setPetPolicyNames(List<String> petPolicyNames) {
		this.petPolicyNames = petPolicyNames;
	}
	public List<String> getBathroomNames() {
		return bathroomNames;
	}
	public void setBathroomNames(List<String> bathroomNames) {
		this.bathroomNames = bathroomNames;
	}







	public Integer getCampAreaId() {
		return campAreaId;
	}







	public void setCampAreaId(Integer campAreaId) {
		this.campAreaId = campAreaId;
	}







	public String getCampAreaName() {
		return campAreaName;
	}







	public void setCampAreaName(String campAreaName) {
		this.campAreaName = campAreaName;
	}







	public String getWebsiteUrl() {
		return websiteUrl;
	}







	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
    
    
}
