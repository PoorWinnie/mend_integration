package com.example.back.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.back.domain.CampAreaBean;
import com.example.back.dto.CampAreaDTO;
import com.example.back.dto.CampAreaRequest;
import com.example.back.dto.CampAreaResponse;
import com.example.back.dto.CampAreaSimpleDTO;
import com.example.back.dto.CampAreaStatusRequest;
import com.example.back.service.CampAreaService;
import com.example.back.service.UserDetailsImpl;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/api/owner/camp-areas")
public class CampAreaOwnerController {

    @Autowired
    private CampAreaService campAreaService;
    
//    
//    @PutMapping("/{id}/status")
//    public ResponseEntity<?> updateCampAreaStatus(@PathVariable Integer id,
//                                                  @RequestBody CampAreaStatusRequest request,
//                                                  @AuthenticationPrincipal UserDetails userDetails) {
//        String username = userDetails.getUsername();
//        boolean success = campAreaService.updateCampAreaStatus(id, request.getStatus(), username);
//        if (success) {
//            return ResponseEntity.ok().body("Status updated");
//        } else {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized or not found");
//        }
//    }

    
    @GetMapping("/own")
    public ResponseEntity<CampAreaResponse<CampAreaDTO>> getMyCampAreas(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Integer userId = userDetails.getId();
        List<CampAreaBean> list = campAreaService.findByUserIdWithSites(userId);
        List<CampAreaDTO> dtoList = list.stream()
                .map(CampAreaDTO::new)
                .toList();

        return ResponseEntity.ok(new CampAreaResponse<>(true, "查詢成功", dtoList));
    }


    
    //新增營區(Create)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CAMP_OWNER')")
    public ResponseEntity<CampAreaResponse<CampAreaSimpleDTO>> createCampArea(
        @RequestPart("request") CampAreaRequest request,  // 將 JSON 包在 FormData 的 request 欄位
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @AuthenticationPrincipal UserDetailsImpl user
    ) {
        CampAreaBean entity = campAreaService.createCampAreaForUser(user.getId(), request, images);
        CampAreaSimpleDTO dto = new CampAreaSimpleDTO(entity);
        return ResponseEntity.ok(new CampAreaResponse<>(true, "新增成功", List.of(dto)));
    }



    // 查詢自己的營區 (Read)
    @GetMapping("/{id}")
    public ResponseEntity<CampAreaResponse<CampAreaDTO>> getMyCampArea(@PathVariable Integer id) {
        CampAreaBean entity = campAreaService.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到該營區"));

        
        CampAreaDTO dto = new CampAreaDTO(entity);
        return ResponseEntity.ok(new CampAreaResponse<>(true, "查詢成功", List.of(dto)));
    }
    
    //更新營區
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CampAreaResponse<CampAreaSimpleDTO>> updateMyCampArea(
            @PathVariable Integer id,
            @RequestPart("request") CampAreaRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "removeImageIds", required = false) List<Integer> removeImageIds,
            @RequestParam(value = "removeMainImage", required = false, defaultValue = "false") boolean removeMainImage,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws IllegalStateException, IOException {

        Integer userId = userDetails.getId();

        CampAreaBean entity = campAreaService.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到該營區"));

        if (!entity.getUser().getId().equals(userId)) {
            throw new RuntimeException("無權限修改此營區");
        }

        // 加進去 Request DTO，後續在 service 裡會處理
        request.setRemoveImageIds(removeImageIds);
        request.setRemoveMainImage(removeMainImage);

        CampAreaBean updated = campAreaService.updateFromRequest(id, request, images);
        CampAreaSimpleDTO dto = new CampAreaSimpleDTO(updated);
        return ResponseEntity.ok(new CampAreaResponse<>(true, "更新成功", List.of(dto)));
    }


    
    
    // 刪除自己的營區 (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<CampAreaResponse<Void>> deleteMyCampArea(@PathVariable Integer id) {
        campAreaService.deleteById(id);
        return ResponseEntity.ok(new CampAreaResponse<>(true, "刪除成功", null));
    }
    
    
    //變更營區狀態
    @PutMapping("/{campAreaId}/status")
    public ResponseEntity<?> updateCampAreaStatus(
            @PathVariable Integer campAreaId,
            @RequestBody Map<String, String> requestBody) {

        String newStatus = requestBody.get("status");

        // 驗證狀態值是否合法
        if (!"Active".equalsIgnoreCase(newStatus) && !"Inactive".equalsIgnoreCase(newStatus)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "無效的狀態值，僅接受 'Active' 或 'Inactive'"
            ));
        }

        CampAreaBean campArea = campAreaService.findById(campAreaId)
                .orElseThrow(() -> new RuntimeException("找不到營區 ID: " + campAreaId));

        campArea.setStatus(newStatus);
        campArea.setUpdatedDate(LocalDateTime.now());
        campAreaService.save(campArea);

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "營區狀態已更新為 " + newStatus
        ));
    }

}
