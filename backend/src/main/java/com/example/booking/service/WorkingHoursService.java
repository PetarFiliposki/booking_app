package com.example.booking.service;

import com.example.booking.dto.workinghours.WorkingHoursRequest;
import com.example.booking.dto.workinghours.WorkingHoursResponse;
import com.example.booking.entity.Business;
import com.example.booking.entity.WorkingHours;
import com.example.booking.exceptions.BadRequestException;
import com.example.booking.exceptions.ForbiddenException;
import com.example.booking.exceptions.NotFoundException;
import com.example.booking.repository.BusinessRepository;
import com.example.booking.repository.WorkingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkingHoursService {

    private final WorkingHoursRepository workingHoursRepository;
    private final BusinessRepository businessRepository;

    @Transactional
    public WorkingHoursResponse set(String ownerEmail, Long businessId, WorkingHoursRequest req) {
        Business b = businessRepository.findById(businessId)
                .orElseThrow(() -> new NotFoundException("Бизнисот не постои"));
        if (!b.getOwner().getEmail().equals(ownerEmail)) {
            throw new ForbiddenException("Не сте сопственик на овој бизнис");
        }
        if (!req.closeTime().isAfter(req.openTime())) {
            throw new BadRequestException("Времето на затворање мора да е по времето на отворање");
        }

        WorkingHours w = workingHoursRepository
                .findByBusinessIdAndDayOfWeek(businessId, req.dayOfWeek())
                .orElseGet(WorkingHours::new);
        w.setBusiness(b);
        w.setDayOfWeek(req.dayOfWeek());
        w.setOpenTime(req.openTime());
        w.setCloseTime(req.closeTime());
        return WorkingHoursResponse.from(workingHoursRepository.save(w));
    }

    @Transactional(readOnly = true)
    public List<WorkingHoursResponse> list(Long businessId) {
        if (!businessRepository.existsById(businessId)) {
            throw new NotFoundException("Бизнисот не постои");
        }
        return workingHoursRepository.findByBusinessIdOrderByDayOfWeek(businessId)
                .stream().map(WorkingHoursResponse::from).toList();
    }
}