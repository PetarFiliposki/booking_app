package com.example.booking.dto.business;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BusinessRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 1000) String description,
        @Size(max = 255) String address,
        @Size(max = 50) String phone
) {
}
