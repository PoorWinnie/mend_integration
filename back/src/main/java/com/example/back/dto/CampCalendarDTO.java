package com.example.back.dto;

import java.time.LocalDate;

public class CampCalendarDTO {
    private final LocalDate date;
    private final boolean isOffDay;
    private final int availableTents;

    public CampCalendarDTO(LocalDate date, boolean isOffDay, int availableTents) {
        this.date = date;
        this.isOffDay = isOffDay;
        this.availableTents = availableTents;
    }

    public LocalDate date() { return date; }
    public boolean isOffDay() { return isOffDay; }
    public int availableTents() { return availableTents; }
}

