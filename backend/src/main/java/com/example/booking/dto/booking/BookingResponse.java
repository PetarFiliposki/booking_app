package com.example.booking.dto.booking;

import com.example.booking.entity.Booking;
import com.example.booking.entity.BookingStatus;
import java.time.Instant;

public record BookingResponse(Long id, Long businessId, Long serviceId, String serviceName,
                              Instant startTime, Instant endTime, BookingStatus status) {
    public static BookingResponse from(Booking b) {
        return new BookingResponse(b.getId(), b.getBusiness().getId(), b.getService().getId(),
                b.getService().getName(), b.getStartTime(), b.getEndTime(), b.getStatus());
    }
}