package com.example.booking.dto.business;

import com.example.booking.entity.Business;

public record BusinessResponse(Long id, String name, String description,
                               String address, String phone, Long ownerId) {
    public static BusinessResponse from(Business b) {
        return new BusinessResponse(b.getId(), b.getName(), b.getDescription(),
                b.getAddress(), b.getPhone(), b.getOwner().getId());
    }
}