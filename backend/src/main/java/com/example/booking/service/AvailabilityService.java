package com.example.booking.service;

import com.example.booking.dto.workinghours.AvailabilityResponse;
import com.example.booking.entity.ServiceOffering;
import com.example.booking.entity.WorkingHours;
import com.example.booking.exceptions.NotFoundException;
import com.example.booking.repository.BusinessRepository;
import com.example.booking.repository.ServiceOfferingRepository;
import com.example.booking.repository.WorkingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private static final int SLOT_STEP_MINUTES = 15;
    private static final ZoneId ZONE = ZoneId.of("Europe/Skopje");

    private final BusinessRepository businessRepository;
    private final ServiceOfferingRepository serviceRepository;
    private final WorkingHoursRepository workingHoursRepository;

    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailability(Long businessId, Long serviceId, LocalDate date) {
        if (!businessRepository.existsById(businessId)) {
            throw new NotFoundException("Бизнисот не постои");
        }
        ServiceOffering service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Услугата не постои"));
        if (!service.getBusiness().getId().equals(businessId)) {
            throw new NotFoundException("Услугата не припаѓа на овој бизнис");
        }

        int duration = service.getDurationMinutes();
        List<LocalTime> slots = new ArrayList<>();

        int dayOfWeek = date.getDayOfWeek().getValue();
        WorkingHours wh = workingHoursRepository
                .findByBusinessIdAndDayOfWeek(businessId, dayOfWeek).orElse(null);

        if (wh != null) {
            LocalDateTime now = LocalDateTime.now(ZONE);
            LocalTime start = wh.getOpenTime();

            while (!start.plusMinutes(duration).isAfter(wh.getCloseTime())
                    && start.plusMinutes(duration).isAfter(start)) {

                LocalDateTime candidate = LocalDateTime.of(date, start);
                if (candidate.isAfter(now)) {
                    slots.add(start);
                }
                start = start.plusMinutes(SLOT_STEP_MINUTES);
                if (start.isBefore(wh.getOpenTime())) break;
            }
        }
        return new AvailabilityResponse(businessId, serviceId, date, duration, slots);
    }
}