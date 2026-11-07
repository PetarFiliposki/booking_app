package com.example.booking.repository;

import com.example.booking.entity.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {
    List<WorkingHours> findByBusinessIdOrderByDayOfWeek(Long businessId);
    Optional<WorkingHours> findByBusinessIdAndDayOfWeek(Long businessId, int dayOfWeek);
}