package com.example.booking.controllers;

import com.example.booking.dto.business.BusinessRequest;
import com.example.booking.dto.business.BusinessResponse;
import com.example.booking.dto.services.ServiceRequest;
import com.example.booking.dto.services.ServiceResponse;
import com.example.booking.dto.workinghours.AvailabilityResponse;
import com.example.booking.dto.workinghours.WorkingHoursRequest;
import com.example.booking.dto.workinghours.WorkingHoursResponse;
import com.example.booking.service.AvailabilityService;
import com.example.booking.service.BusinessService;
import com.example.booking.service.WorkingHoursService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;
    private final WorkingHoursService workingHoursService;
    private final AvailabilityService availabilityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public BusinessResponse create(@AuthenticationPrincipal UserDetails principal,
                                   @Valid @RequestBody BusinessRequest req) {
        return businessService.create(principal.getUsername(), req);
    }

    @GetMapping
    public List<BusinessResponse> list() {
        return businessService.listAll();
    }

    @GetMapping("/{id}")
    public BusinessResponse get(@PathVariable Long id) {
        return businessService.get(id);
    }

    @PostMapping("/{id}/services")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public ServiceResponse addService(@AuthenticationPrincipal UserDetails principal,
                                      @PathVariable Long id,
                                      @Valid @RequestBody ServiceRequest req) {
        return businessService.addService(principal.getUsername(), id, req);
    }

    @GetMapping("/{id}/services")
    public List<ServiceResponse> services(@PathVariable Long id) {
        return businessService.listServices(id);
    }

    @PutMapping("/{id}/working-hours")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public WorkingHoursResponse setWorkingHours(@AuthenticationPrincipal UserDetails principal,
                                                @PathVariable Long id,
                                                @Valid @RequestBody WorkingHoursRequest req) {
        return workingHoursService.set(principal.getUsername(), id, req);
    }

    @GetMapping("/{id}/working-hours")
    public List<WorkingHoursResponse> workingHours(@PathVariable Long id) {
        return workingHoursService.list(id);
    }

    @GetMapping("/{id}/availability")
    public AvailabilityResponse availability(@PathVariable Long id,
                                             @RequestParam Long serviceId,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return availabilityService.getAvailability(id, serviceId, date);
    }
}