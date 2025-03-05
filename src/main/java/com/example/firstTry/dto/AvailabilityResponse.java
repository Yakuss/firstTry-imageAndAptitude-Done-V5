package com.example.firstTry.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AvailabilityResponse {
    private LocalDate date;
    private List<LocalTime> availableSlots;
    private List<LocalTime> breakTimes;
}