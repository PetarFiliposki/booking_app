package com.example.booking.dto.workinghours;

import com.example.booking.entity.WorkingHours;
import java.time.LocalTime;

public record WorkingHoursResponse(Long id, int dayOfWeek, LocalTime openTime, LocalTime closeTime) {
    public static WorkingHoursResponse from(WorkingHours w) {
        return new WorkingHoursResponse(w.getId(), w.getDayOfWeek(), w.getOpenTime(), w.getCloseTime());
    }
}