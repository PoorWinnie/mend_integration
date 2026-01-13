package com.example.back.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.domain.CampAreaBean;
import com.example.back.domain.CampAreaImageBean;
import com.example.back.repository.CampAreaImageRepository;
import com.example.back.repository.CampAreaRepository;

@Service
public class CampAreaImageService {

    @Autowired
    private CampAreaImageRepository imageRepo;

    @Autowired
    private CampAreaRepository areaRepo;

    
    public Optional<CampAreaImageBean> findByCampAreaId(Integer areaId) {
        return imageRepo.findById(areaId);
        		}

    public CampAreaImageBean addImage(Integer areaId, String url, String desc) {
        CampAreaBean area = areaRepo.findById(areaId)
            .orElseThrow(() -> new RuntimeException("找不到營地區域"));

        CampAreaImageBean image = new CampAreaImageBean();
        image.setCampArea(area);
        image.setImageUrl(url);
        image.setDescription(desc);
        return imageRepo.save(image);
    }

    public void deleteById(Integer id) {
        imageRepo.deleteById(id);
    }
}
