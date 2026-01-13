package com.example.back.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.back.dto.CampSiteRequest;
import com.example.back.dto.CampSiteSpotEditResponse;
import com.example.back.dto.CampSiteUpdateRequest;
import com.example.back.dto.CampSpotInfoDTO;
import com.example.back.service.CampSiteService;
import com.example.back.service.CampSpotService;
import com.example.back.service.UserDetailsImpl;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/api/owner/camp-sites")
@PreAuthorize("hasRole('CAMP_OWNER')")
public class CampSiteOwnerController {

    @Autowired
    private CampSiteService campSiteService;
    
    //更新營地營位
    @PutMapping(value = "/{campSiteId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCampSiteAndSpots(
        @PathVariable Integer campSiteId,
        @RequestPart("request") CampSiteUpdateRequest request,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        Integer currentUserId = userDetails.getId();

        campSiteService.updateFromRequest(campSiteId, request, images, currentUserId);

        return ResponseEntity.ok().body(Map.of("message", "更新成功"));
    }
    
    
    //新增營地營位
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCampSiteWithSpots(
        @RequestPart("request") CampSiteRequest request,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @AuthenticationPrincipal UserDetailsImpl user
    ) {
        try {
            campSiteService.createWithSpots(request, images, user.getId());
            return ResponseEntity.ok(Map.of("message", "新增成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    


}
