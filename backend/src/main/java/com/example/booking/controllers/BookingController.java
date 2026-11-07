package com.example.booking.controllers;

import com.example.booking.dto.booking.BookingResponse;
import com.example.booking.service.BookingService;
import lombok.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/my")
    public List<BookingResponse> mine(@AuthenticationPrincipal UserDetails principal) {
        return bookingService.myBookings(principal.getUsername());
    }

    @PostMapping("/{id}/cancel")
    public BookingResponse cancel(@AuthenticationPrincipal UserDetails principal,
                                  @PathVariable Long id) {
        return bookingService.cancel(principal.getUsername(), id);
    }
}