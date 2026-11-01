package com.example.booking.user.dto;

import com.example.booking.user.Role;
import com.example.booking.user.User;

public record UserResponse(Long id, String email, String fullName, Role role) {
    public static UserResponse from(User user){
        return new UserResponse(user.getId(), user.getEmail(), user.getFull_name(), user.getRole());
    }
}
