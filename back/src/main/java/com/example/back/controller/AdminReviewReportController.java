package com.example.back.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.domain.ReviewReport;
import com.example.back.service.ReviewReportService;

@RestController
@RequestMapping("/api/admin/review-reports")
@CrossOrigin(origins = "*")
public class AdminReviewReportController {

    @Autowired
    private ReviewReportService reportService;
    
    // 處理舉報
    @PutMapping("/{id}/process")
    public ResponseEntity<ReviewReport> processReport(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, String> payload) {
        
        String status = payload.get("status");
        String handlerNote = payload.get("handlerNote");
        
        ReviewReport updatedReport = reportService.processReport(id, status, handlerNote);
        return new ResponseEntity<>(updatedReport, HttpStatus.OK);
    }
    
    // 核准舉報並移除內容
    @PutMapping("/{id}/approve-and-remove")
    public ResponseEntity<ReviewReport> approveAndRemoveContent(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, String> payload) {
        
        String handlerNote = getHandlerNoteFromPayload(payload, "管理員已核准檢舉並刪除內容。");
        
        ReviewReport updatedReport = reportService.approveAndRemoveReportedContent(id, handlerNote);
        return new ResponseEntity<>(updatedReport, HttpStatus.OK);
    }
    
    // 也支援POST方法
    @PostMapping("/{id}/approve-and-remove")
    public ResponseEntity<ReviewReport> approveAndRemoveContentPost(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, String> payload) {
        
        String handlerNote = getHandlerNoteFromPayload(payload, "管理員已核准檢舉並刪除內容。");
        
        ReviewReport updatedReport = reportService.approveAndRemoveReportedContent(id, handlerNote);
        return new ResponseEntity<>(updatedReport, HttpStatus.OK);
    }
    
    // 駁回舉報
    @PutMapping("/{id}/reject")
    public ResponseEntity<ReviewReport> rejectReport(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, String> payload) {
        
        String handlerNote = getHandlerNoteFromPayload(payload, "管理員判斷檢舉無效，予以駁回。");
        
        ReviewReport updatedReport = reportService.processReport(id, "REJECTED", handlerNote);
        return new ResponseEntity<>(updatedReport, HttpStatus.OK);
    }
    
    // 也支援POST方法
    @PostMapping("/{id}/reject")
    public ResponseEntity<ReviewReport> rejectReportPost(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, String> payload) {
        
        String handlerNote = getHandlerNoteFromPayload(payload, "管理員判斷檢舉無效，予以駁回。");
        
        ReviewReport updatedReport = reportService.processReport(id, "REJECTED", handlerNote);
        return new ResponseEntity<>(updatedReport, HttpStatus.OK);
    }

    // 獲取待處理的舉報
    @GetMapping("/pending")
    public ResponseEntity<List<ReviewReport>> getPendingReports(
            @RequestParam(value = "target", required = false) String reportTarget) {
        
        List<ReviewReport> pendingReports;
        if (reportTarget != null && !reportTarget.isEmpty()) {
            // 根據目標類型過濾待處理舉報
            pendingReports = reportService.getPendingReportsByTarget(reportTarget);
        } else {
            // 獲取所有待處理舉報
            pendingReports = reportService.getPendingReports();
        }
        
        return new ResponseEntity<>(pendingReports, HttpStatus.OK);
    }
    
    // 獲取特定目標類型的所有舉報
    @GetMapping("/by-target")
    public ResponseEntity<List<ReviewReport>> getReportsByTarget(
            @RequestParam("target") String reportTarget) {
        
        List<ReviewReport> reports = reportService.getPendingReportsByTarget(reportTarget);
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }
    
    // 輔助方法：從不同可能的鍵中獲取處理備註
    private String getHandlerNoteFromPayload(Map<String, String> payload, String defaultValue) {
        String[] possibleKeys = {"handlerNote", "note", "reason", "content"};
        
        for (String key : possibleKeys) {
            String value = payload.get(key);
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        
        return defaultValue;
    }
}