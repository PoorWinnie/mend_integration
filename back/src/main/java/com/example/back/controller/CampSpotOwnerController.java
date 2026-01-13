package com.example.back.controller;

import java.nio.file.AccessDeniedException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.CampSiteSpotEditResponse;
import com.example.back.service.CampSpotService;
import com.example.back.service.UserDetailsImpl;

@RestController
@RequestMapping("/api/owner/camp-spots")
@PreAuthorize("hasRole('CAMP_OWNER')")
public class CampSpotOwnerController {

    @Autowired
    private CampSpotService campSpotService;
    
    
    //查詢一筆營地位
    @GetMapping("/{campSpotId}")
    public ResponseEntity<CampSiteSpotEditResponse> getCampSpotDetail(
            @PathVariable Integer campSpotId,
            @AuthenticationPrincipal UserDetailsImpl user) throws AccessDeniedException {

    	CampSiteSpotEditResponse response = campSpotService.getCampSiteSpotEditInfo(campSpotId, user.getId());
        return ResponseEntity.ok(response);
    }
    
    
    //刪除一筆營地營位
    @DeleteMapping("/{campSpotId}")
    public ResponseEntity<?> deleteCampSpotAndSite(@PathVariable Integer campSpotId, @AuthenticationPrincipal UserDetailsImpl user) {
        campSpotService.deleteCampSpotAndParent(campSpotId, user.getId());
        return ResponseEntity.ok(Map.of("message", "帳位與營地已刪除"));
    }

    
    
}