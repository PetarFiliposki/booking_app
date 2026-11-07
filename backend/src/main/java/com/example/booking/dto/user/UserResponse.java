package com.example.booking.dto.user;

import com.example.booking.entity.Role;
import com.example.booking.entity.User;

public record UserResponse(Long id, String email, String fullName, Role role) {
    public static UserResponse from(User user){
        return new UserResponse(user.getId(), user.getEmail(), user.getFull_name(), user.getRole());
    }
}
