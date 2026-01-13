package com.example.back.dto;

import java.util.List;

public class CampSiteUpdateRequest {
    private CampSiteRequest campSite;
    private List<CampSpotRequest> campSpots;

    public CampSiteRequest getCampSite() {
        return campSite;
    }

    public void setCampSite(CampSiteRequest campSite) {
        this.campSite = campSite;
    }

    public List<CampSpotRequest> getCampSpots() {
        return campSpots;
    }

    public void setCampSpots(List<CampSpotRequest> campSpots) {
        this.campSpots = campSpots;
    }
}
