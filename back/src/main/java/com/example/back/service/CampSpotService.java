	package com.example.back.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.back.domain.CampAreaBean;
import com.example.back.domain.CampSiteBean;
import com.example.back.domain.CampSpotBean;
import com.example.back.dto.CampSiteSpotEditResponse;
import com.example.back.dto.CampSpotInfoDTO;
import com.example.back.repository.CampSiteRepository;
import com.example.back.repository.CampSpotRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CampSpotService {
    @Autowired
    private CampSiteRepository campSiteRepo;
    @Autowired
    private CampSpotRepository campSpotRepo;

    public List<CampSpotBean> findAll() {
        return campSpotRepo.findAll();
    }

    public Optional<CampSpotBean> findById(Integer id) {
        return campSpotRepo.findById(id);
    }

    public CampSpotBean save(CampSpotBean bean) {
        return campSpotRepo.save(bean);
    }

    public void deleteById(Integer id) {
    	campSpotRepo.deleteById(id);
    }
    
    public CampSiteSpotEditResponse getCampSiteSpotEditInfo(Integer campSpotId, Integer userId)  {
    	CampSpotBean spot = campSpotRepo.findById(campSpotId)
    	        .orElseThrow(() -> new RuntimeException("找不到營位"));

    	    CampSiteBean site = spot.getCampSite();
    	    CampAreaBean area = site.getCampArea();

    	    CampSiteSpotEditResponse response = new CampSiteSpotEditResponse();
    	    response.setCampAreaId(area.getCampAreaId());
    	    response.setCampSiteId(site.getCampSiteId());
    	    response.setCampSiteName(site.getCampSiteName());
    	    response.setSiteType(site.getSiteType());

    	    CampSiteSpotEditResponse.CampSpotInnerDTO dto = new CampSiteSpotEditResponse.CampSpotInnerDTO();
    	    dto.setCampSpotId(spot.getCampSpotId());
    	    dto.setSpotName(spot.getSpotName());
    	    dto.setAreaSize(spot.getAreaSize());
    	    dto.setMaxTents(spot.getMaxTents());
    	    dto.setPricePerNight(spot.getPricePerNight());
    	    dto.setPriceHoliday(spot.getPriceHoliday());
    	    dto.setHasElectricity(spot.getHasElectricity());
    	    dto.setHasWater(spot.getHasWater());
    	    dto.setDescription(spot.getDescription());
    	    dto.setToiletImage(spot.getToiletImage());

    	    response.setCampSpot(dto);
    	    return response;
    }
    
    
    @Transactional
    public void deleteCampSpotAndParent(Integer campSpotId, Integer userId) {
        CampSpotBean spot = campSpotRepo.findById(campSpotId)
            .orElseThrow(() -> new RuntimeException("找不到帳位"));

        CampSiteBean site = spot.getCampSite();

        // 權限驗證
        if (!site.getCampArea().getUser().getId().equals(userId)) {
            throw new RuntimeException("無權限刪除");
        }

        // 先刪掉 spot
        campSpotRepo.delete(spot);

        // 判斷該 CampSite 底下是否還有其他帳位
        List<CampSpotBean> remainingSpots = campSpotRepo.findByCampSite_CampSiteId(site.getCampSiteId());

        if (remainingSpots.isEmpty()) {
            campSiteRepo.delete(site);
        }
    }
    
    //-----------------------------------------------------------
    public CampSpotInfoDTO getCampSpotInfo(Integer campSpotId) {
        CampSpotBean spot = campSpotRepo.findFullSpotInfoById(campSpotId);

        String spotName = spot.getSpotName();
        String campSiteName = spot.getCampSite().getCampSiteName();
        String campAreaName = spot.getCampSite().getCampArea().getCampAreaName();
        BigDecimal priceWeekend = spot.getPriceWeekend();

        String imageUrl = null;
        if (!spot.getCampSite().getCampArea().getCampAreaImages().isEmpty()) {
            imageUrl = spot.getCampSite().getCampArea().getCampAreaImages().get(0).getImageUrl();
        }

        return new CampSpotInfoDTO(spotName, campSiteName, campAreaName, priceWeekend, imageUrl);
    }
    //--------------------------------------------------------------
    
    

}
