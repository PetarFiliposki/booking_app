package com.example.booking.service;

import com.example.booking.dto.booking.BookingRequest;
import com.example.booking.dto.booking.BookingResponse;
import com.example.booking.entity.*;
import com.example.booking.exceptions.*;
import com.example.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final ZoneId ZONE = ZoneId.of("Europe/Skopje");

    private final BookingRepository bookingRepository;
    private final BusinessRepository businessRepository;
    private final ServiceOfferingRepository serviceRepository;
    private final UserRepository userRepository;
    private final WorkingHoursRepository workingHoursRepository;

    @Transactional
    public BookingResponse create(String customerEmail, Long businessId, BookingRequest req) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new NotFoundException("Бизнисот не постои"));
        ServiceOffering service = serviceRepository.findById(req.serviceId())
                .orElseThrow(() -> new NotFoundException("Услугата не постои"));
        if (!service.getBusiness().getId().equals(businessId)) {
            throw new BadRequestException("Услугата не припаѓа на овој бизнис");
        }
        User customer = userRepository.findByEmail(customerEmail).orElseThrow();

        LocalDateTime start = LocalDateTime.of(req.date(), req.time());
        LocalDateTime end = start.plusMinutes(service.getDurationMinutes());

        if (!start.isAfter(LocalDateTime.now(ZONE))) {
            throw new BadRequestException("Терминот е во минатото");
        }

        // мора да е во работно време
        WorkingHours wh = workingHoursRepository
                .findByBusinessIdAndDayOfWeek(businessId, req.date().getDayOfWeek().getValue())
                .orElseThrow(() -> new BadRequestException("Бизнисот не работи тој ден"));
        boolean withinHours = !req.time().isBefore(wh.getOpenTime())
                && !end.toLocalTime().isAfter(wh.getCloseTime())
                && end.toLocalDate().equals(req.date());
        if (!withinHours) {
            throw new BadRequestException("Терминот е надвор од работното време");
        }

        Booking booking = new Booking();
        booking.setBusiness(business);
        booking.setService(service);
        booking.setCustomer(customer);
        booking.setStartTime(start.atZone(ZONE).toInstant());
        booking.setEndTime(end.atZone(ZONE).toInstant());
        booking.setStatus(BookingStatus.CONFIRMED);

        try {
            // saveAndFlush го праќа INSERT веднаш, па ограничувањето во базата се проверува тука
            return BookingResponse.from(bookingRepository.saveAndFlush(booking));
        } catch (DataIntegrityViolationException e) {
            throw new SlotUnavailableException("Терминот е веќе зафатен");
        }
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> myBookings(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail).orElseThrow();
        return bookingRepository.findByCustomerIdOrderByStartTimeDesc(customer.getId())
                .stream().map(BookingResponse::from).toList();
    }

    @Transactional
    public BookingResponse cancel(String email, Long bookingId) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Резервацијата не постои"));

        boolean isCustomer = b.getCustomer().getEmail().equals(email);
        boolean isOwner = b.getBusiness().getOwner().getEmail().equals(email);
        if (!isCustomer && !isOwner) {
            throw new ForbiddenException("Немате дозвола");
        }
        if (b.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Резервацијата е веќе откажана");
        }
        // правило: клиентот не може да откаже помалку од 2 часа пред терминот
        if (isCustomer && !isOwner
                && Instant.now().isAfter(b.getStartTime().minusSeconds(2 * 3600))) {
            throw new BadRequestException("Не може да се откаже помалку од 2 часа пред терминот");
        }
        b.setStatus(BookingStatus.CANCELLED);
        return BookingResponse.from(b);
    }
}