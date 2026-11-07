package com.example.booking;

import com.example.booking.dto.booking.BookingRequest;
import com.example.booking.entity.*;
import com.example.booking.exceptions.SlotUnavailableException;
import com.example.booking.repository.*;
import com.example.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class BookingIntegrationTest {

    // Свој PostgreSQL само за тестот
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired BookingService bookingService;
    @Autowired UserRepository userRepository;
    @Autowired BusinessRepository businessRepository;
    @Autowired ServiceOfferingRepository serviceRepository;
    @Autowired WorkingHoursRepository workingHoursRepository;
    @Autowired BookingRepository bookingRepository;

    Business business;
    ServiceOffering service;
    LocalDate monday;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        workingHoursRepository.deleteAll();
        serviceRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();

        User owner = newUser("owner@test.com", Role.BUSINESS_OWNER);
        newUser("c1@test.com", Role.CUSTOMER);
        newUser("c2@test.com", Role.CUSTOMER);

        business = new Business();
        business.setOwner(owner);
        business.setName("Test Salon");
        business = businessRepository.save(business);

        service = new ServiceOffering();
        service.setBusiness(business);
        service.setName("Cut");
        service.setDurationMinutes(30);
        service.setPrice(BigDecimal.valueOf(400));
        service = serviceRepository.save(service);

        monday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        WorkingHours wh = new WorkingHours();
        wh.setBusiness(business);
        wh.setDayOfWeek(1);
        wh.setOpenTime(LocalTime.of(9, 0));
        wh.setCloseTime(LocalTime.of(17, 0));
        workingHoursRepository.save(wh);
    }

    private User newUser(String email, Role role) {
        User u = new User();
        u.setEmail(email);
        u.setPassword_hash("x");
        u.setFull_name(email);
        u.setRole(role);
        return userRepository.save(u);
    }

    @Test
    void secondBookingInSameSlot_isRejected() {
        var req = new BookingRequest(service.getId(), monday, LocalTime.of(10, 0));

        assertNotNull(bookingService.create("c1@test.com", business.getId(), req));
        assertThrows(SlotUnavailableException.class,
                () -> bookingService.create("c2@test.com", business.getId(), req));
    }

    @Test
    void overlappingBooking_isRejected() {
        bookingService.create("c1@test.com", business.getId(),
                new BookingRequest(service.getId(), monday, LocalTime.of(10, 0)));

        var overlap = new BookingRequest(service.getId(), monday, LocalTime.of(10, 15));
        assertThrows(SlotUnavailableException.class,
                () -> bookingService.create("c2@test.com", business.getId(), overlap));
    }

    @Test
    void adjacentBooking_isAllowed() {
        bookingService.create("c1@test.com", business.getId(),
                new BookingRequest(service.getId(), monday, LocalTime.of(10, 0)));

        assertNotNull(bookingService.create("c2@test.com", business.getId(),
                new BookingRequest(service.getId(), monday, LocalTime.of(10, 30))));
    }
    @Test
    void concurrentBookings_onlyOneSucceeds() throws Exception {
        int threads = 10;
        var executor = java.util.concurrent.Executors.newFixedThreadPool(threads);
        var ready = new java.util.concurrent.CountDownLatch(threads);
        var start = new java.util.concurrent.CountDownLatch(1);
        var success = new java.util.concurrent.atomic.AtomicInteger();
        var conflicts = new java.util.concurrent.atomic.AtomicInteger();

        var req = new BookingRequest(service.getId(), monday, LocalTime.of(11, 0));

        for (int i = 0; i < threads; i++) {
            String email = (i % 2 == 0) ? "c1@test.com" : "c2@test.com";
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();                       // сите чекаат на истиот сигнал
                    bookingService.create(email, business.getId(), req);
                    success.incrementAndGet();
                } catch (SlotUnavailableException e) {
                    conflicts.incrementAndGet();
                } catch (Exception ignored) { }
            });
        }

        ready.await();
        start.countDown();                               // старт на сите одеднаш
        executor.shutdown();
        executor.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS);

        assertEquals(1, success.get(), "Точно една резервација смее да успее");
        assertEquals(threads - 1, conflicts.get());
        assertEquals(1, bookingRepository.count());
    }
}