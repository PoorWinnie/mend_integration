package com.example.back.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.back.dto.CampCalendarDTO;
import com.example.back.service.CampCalendarService;

@RestController
@RequestMapping("/api/public/camp-areas")
public class CampCalendarController {

    @Autowired
    private CampCalendarService campCalendarService;

    @GetMapping("/{id}/calendar")
    public Map<String, List<CampCalendarDTO>> getCampCalendar(
            @PathVariable Integer id,
            @RequestParam String month) {
        List<CampCalendarDTO> calendar = campCalendarService.getCampAreaCalendar(id, month);
        return Map.of("calendar", calendar);
    }
}