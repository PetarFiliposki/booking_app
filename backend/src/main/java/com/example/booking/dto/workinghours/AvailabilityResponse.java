package com.example.booking.dto.workinghours;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AvailabilityResponse(Long businessId, Long serviceId, LocalDate date,
                                   int durationMinutes, List<LocalTime> slots) {}