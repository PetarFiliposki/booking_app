package com.example.booking.repository;

import com.example.booking.entity.Booking;
import com.example.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBusinessIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(Long businessId, BookingStatus status, Instant to, Instant from);
    List<Booking> findByCustomerIdOrderByStartTimeDesc(Long customerId);
}