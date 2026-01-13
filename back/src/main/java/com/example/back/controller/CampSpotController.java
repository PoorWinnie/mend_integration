package com.example.back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.CampAreaResponse;
import com.example.back.dto.CampSpotAvailabilityDTO;
import com.example.back.dto.CampSpotInfoDTO;
import com.example.back.dto.CampSpotSearchRequest;
import com.example.back.service.AvailableCampSpotService;
import com.example.back.service.CampSpotService;

@RestController
@RequestMapping("/api/public/camp-spots")
public class CampSpotController {

    @Autowired
    private AvailableCampSpotService availableCampSpotService;

    @PostMapping("/search")
    public ResponseEntity<CampAreaResponse<CampSpotAvailabilityDTO>> searchAvailableSpots(
            @RequestBody CampSpotSearchRequest request) {
        List<CampSpotAvailabilityDTO> result = availableCampSpotService.findAvailableSpotsWithInfo(request);
        long count = availableCampSpotService.countAvailableSpots(request);
        
        CampAreaResponse<CampSpotAvailabilityDTO> response = new CampAreaResponse<>();
        response.setSuccess(true);
        response.setMessage("查詢成功");
        response.setList(result);
        response.setCount(count);

        return ResponseEntity.ok(response);
        
    }
    
    //智謙
    @Autowired
    private CampSpotService campSpotService;

    @GetMapping("/{campSpotId}")
    public ResponseEntity<CampSpotInfoDTO> getCampSpotInfo(@PathVariable Integer campSpotId) {
        CampSpotInfoDTO info = campSpotService.getCampSpotInfo(campSpotId);
        return ResponseEntity.ok(info);
    }
    
    
    @PostMapping("/count")
    public ResponseEntity<CampAreaResponse<Object>> count(@RequestBody CampSpotSearchRequest req) {
        long count = availableCampSpotService.findAvailableSpotsWithInfo(req).size();

        CampAreaResponse<Object> response = new CampAreaResponse<>();
        response.setSuccess(true);
        response.setMessage("查詢成功");
        response.setCount(count);
        response.setList(null); // 不回傳清單

        return ResponseEntity.ok(response);
    }

    


}
