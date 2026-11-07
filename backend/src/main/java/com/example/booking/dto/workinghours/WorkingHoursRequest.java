package com.example.booking.dto.workinghours;

import jakarta.validation.constraints.*;
import java.time.LocalTime;

public record WorkingHoursRequest(
        @Min(1) @Max(7) int dayOfWeek,
        @NotNull LocalTime openTime,
        @NotNull LocalTime closeTime
) {
}
