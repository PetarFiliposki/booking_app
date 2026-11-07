package com.example.booking.dto.user;

public record AuthResponse(
        String token,
        UserResponse user)
{}