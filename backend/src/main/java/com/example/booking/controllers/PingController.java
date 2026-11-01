package com.example.booking.controllers;


import com.example.booking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PingController {

    private final UserRepository userRepository;

    @GetMapping("/ping")
    public Map<String, Object> ping () {
        return Map.of(
                "status", "ok",
                "users", userRepository.count()
        );
    }


}
