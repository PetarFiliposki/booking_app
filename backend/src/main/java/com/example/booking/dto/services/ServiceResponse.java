package com.example.booking.dto.services;

import com.example.booking.entity.ServiceOffering;

import java.math.BigDecimal;

public record ServiceResponse(Long id, Long businessId, String name,
                              int durationMinutes, BigDecimal price) {
    public static ServiceResponse from(ServiceOffering s) {
        return new ServiceResponse(s.getId(), s.getBusiness().getId(), s.getName(),
                s.getDurationMinutes(), s.getPrice());
    }
}