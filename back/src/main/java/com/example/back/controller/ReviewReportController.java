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
@RequestMapping("/api/public/review-reports")
@CrossOrigin(origins = "*")
public class ReviewReportController {

    @Autowired
    private ReviewReportService reportService;

    // 創建評論舉報
    @PostMapping("/review")
    public ResponseEntity<ReviewReport> createReviewReport(@RequestBody ReviewReport report) {
        ReviewReport createdReport = reportService.createReviewReport(report);
        return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
    }
    
    // 創建回覆舉報
    @PostMapping("/reply")
    public ResponseEntity<ReviewReport> createReplyReport(@RequestBody ReviewReport report) {
        ReviewReport createdReport = reportService.createReplyReport(report);
        return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
    }
    
    // 兼容原有方法，創建舉報
    @PostMapping
    public ResponseEntity<ReviewReport> createReport(@RequestBody ReviewReport report) {
        ReviewReport createdReport = reportService.createReport(report);
        return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
    }

    // 獲取舉報
    @GetMapping("/{id}")
    public ResponseEntity<ReviewReport> getReport(@PathVariable("id") Integer id) {
        ReviewReport report = reportService.getReport(id);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    // 獲取評價的所有舉報
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<ReviewReport>> getReportsByReviewId(
            @PathVariable("reviewId") Integer reviewId,
            @RequestParam(value = "target", required = false) String reportTarget) {
        
        List<ReviewReport> reports;
        if (reportTarget != null && !reportTarget.isEmpty()) {
            // 如果提供了舉報目標參數，則按目標類型過濾
            reports = reportService.getReportsByReviewIdAndTarget(reviewId, reportTarget);
        } else {
            // 否則返回所有舉報
            reports = reportService.getReportsByReviewId(reviewId);
        }
        
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    // 檢查使用者是否已舉報評價
    @GetMapping("/check")
    public ResponseEntity<Boolean> hasUserReportedReview(
            @RequestParam("userId") Integer userId,
            @RequestParam("reviewId") Integer reviewId,
            @RequestParam(value = "target", required = false) String reportTarget) {
        
        boolean hasReported;
        if (reportTarget != null && !reportTarget.isEmpty()) {
            // 檢查特定目標類型的舉報
            hasReported = reportService.hasUserReportedTarget(userId, reviewId, reportTarget);
        } else {
            // 檢查任何類型的舉報
            hasReported = reportService.hasUserReportedReview(userId, reviewId);
        }
        
        return new ResponseEntity<>(hasReported, HttpStatus.OK);
    }
    
    // 重要：保留這個端點以維持向後兼容性
    // 但引導用戶使用管理員API
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingReportsRedirect() {
        Map<String, Object> response = Map.of(
            "error", "請使用管理員API路徑",
            "message", "此端點已移至/api/admin/review-reports/pending",
            "status", 302,
            "requiredAuth", true
        );
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }
    
    // 處理舉報 (保留向後兼容性)
    @PutMapping("/{id}/process")
    public ResponseEntity<?> processReportRedirect(@PathVariable("id") Integer id) {
        Map<String, Object> response = Map.of(
            "error", "請使用管理員API路徑",
            "message", "此端點已移至/api/admin/review-reports/{id}/process",
            "reportId", id,
            "status", 302,
            "requiredAuth", true
        );
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }
    
    // 核准舉報並移除內容 (保留向後兼容性)
    @PutMapping("/{id}/approve-and-remove")
    public ResponseEntity<?> approveAndRemoveRedirect(@PathVariable("id") Integer id) {
        Map<String, Object> response = Map.of(
            "error", "請使用管理員API路徑",
            "message", "此端點已移至/api/admin/review-reports/{id}/approve-and-remove",
            "reportId", id,
            "status", 302,
            "requiredAuth", true
        );
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }
    
    // 駁回舉報 (保留向後兼容性)
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectReportRedirect(@PathVariable("id") Integer id) {
        Map<String, Object> response = Map.of(
            "error", "請使用管理員API路徑",
            "message", "此端點已移至/api/admin/review-reports/{id}/reject",
            "reportId", id,
            "status", 302,
            "requiredAuth", true
        );
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }
}