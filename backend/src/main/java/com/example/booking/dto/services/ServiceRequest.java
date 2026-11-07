package com.example.booking.dto.services;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ServiceRequest(
        @NotBlank @Size(max = 255) String name,
        @Positive int durationMinutes,
        @NotNull @PositiveOrZero BigDecimal price
) {}