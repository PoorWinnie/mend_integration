package com.example.back.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.back.domain.CampSpotBean;
import com.example.back.dto.CampCalendarDTO;
import com.example.back.repository.BookingCalendarRepository;
import com.example.back.repository.CampAreaOffDayRepository;
import com.example.back.repository.CampAreaOffWeekdayRepository;
import com.example.back.repository.CampSpotRepository;

@Service
public class CampCalendarService {

    @Autowired
    private CampAreaOffDayRepository offDayRepo;

    @Autowired
    private CampAreaOffWeekdayRepository offWeekdayRepo;

    @Autowired
    private CampSpotRepository campSpotRepo;

    @Autowired
    private BookingCalendarRepository bookingCalendarRepo;

    public List<CampCalendarDTO> getCampAreaCalendar(Integer campAreaId, String monthStr) {
        YearMonth month = YearMonth.parse(monthStr);
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        List<LocalDate> offDates = offDayRepo.findOffDaysBetween(campAreaId, start, end);
        Set<Integer> offWeekdays = new HashSet<>(offWeekdayRepo.findWeekdaysByCampAreaId(campAreaId));
        List<CampSpotBean> spots = campSpotRepo.findByCampAreaId(campAreaId);
        List<Object[]> bookingList = bookingCalendarRepo.sumTentCountByCampAreaAndDateRange(campAreaId, start, end);

        Map<LocalDate, Integer> bookingMap = new HashMap<>();
        for (Object[] row : bookingList) {
            bookingMap.put((LocalDate) row[0], ((Number) row[1]).intValue());
            
        }

        int totalTents = spots.stream().mapToInt(CampSpotBean::getMaxTents).sum();
        List<CampCalendarDTO> result = new ArrayList<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            boolean isOff = offDates.contains(date) || offWeekdays.contains(date.getDayOfWeek().getValue() % 7);
            int booked = bookingMap.getOrDefault(date, 0);
            int remaining = isOff ? 0 : Math.max(totalTents - booked, 0);
            result.add(new CampCalendarDTO(date, isOff, remaining));
        }

        return result;
    }
}