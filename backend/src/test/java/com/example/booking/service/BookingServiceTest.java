package com.example.booking.service;

import com.example.booking.dto.booking.BookingRequest;
import com.example.booking.entity.*;
import com.example.booking.exceptions.BadRequestException;
import com.example.booking.exceptions.NotFoundException;
import com.example.booking.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)   // вклучува Mockito
class BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock BusinessRepository businessRepository;
    @Mock ServiceOfferingRepository serviceRepository;
    @Mock UserRepository userRepository;
    @Mock WorkingHoursRepository workingHoursRepository;

    @InjectMocks BookingService bookingService;   // добива лажни зависности

    @Test
    void create_throwsNotFound_whenBusinessMissing() {
        when(businessRepository.findById(99L)).thenReturn(Optional.empty());

        var req = new BookingRequest(1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0));

        assertThrows(NotFoundException.class,
                () -> bookingService.create("a@mail.com", 99L, req));
    }

    @Test
    void create_throwsBadRequest_whenSlotInPast() {
        Business business = new Business();
        business.setId(1L);
        ServiceOffering service = new ServiceOffering();
        service.setBusiness(business);
        service.setDurationMinutes(30);
        User customer = new User();

        when(businessRepository.findById(1L)).thenReturn(Optional.of(business));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(userRepository.findByEmail("a@mail.com")).thenReturn(Optional.of(customer));

        var req = new BookingRequest(1L, LocalDate.now().minusDays(1), LocalTime.of(10, 0));

        assertThrows(BadRequestException.class,
                () -> bookingService.create("a@mail.com", 1L, req));
    }
}