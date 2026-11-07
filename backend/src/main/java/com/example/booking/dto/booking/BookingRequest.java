package com.example.booking.dto.booking;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record BookingRequest(
        @NotNull Long serviceId,
        @NotNull LocalDate date,
        @NotNull LocalTime time
) {}