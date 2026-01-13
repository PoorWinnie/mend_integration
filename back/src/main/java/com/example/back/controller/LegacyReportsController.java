package com.example.back.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports")
@CrossOrigin(origins = "*")
public class LegacyReportsController {

    // 捕獲舊的pending端點請求並重定向
    @GetMapping("/pending")
    public ResponseEntity<?> redirectPendingReports() {
        Map<String, Object> response = Map.of(
            "error", "API路徑已更改",
            "message", "請使用新路徑: /api/admin/review-reports/pending",
            "status", 301,
            "newPath", "/api/admin/review-reports/pending"
        );
        return new ResponseEntity<>(response, HttpStatus.MOVED_PERMANENTLY);
    }
}