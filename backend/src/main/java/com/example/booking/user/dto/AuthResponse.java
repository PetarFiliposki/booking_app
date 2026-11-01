package com.example.booking.user.dto;

public record AuthResponse(
        String token,
        UserResponse user)
{}