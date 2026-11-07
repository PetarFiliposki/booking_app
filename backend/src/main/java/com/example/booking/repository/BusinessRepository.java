package com.example.booking.repository;

import com.example.booking.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    List<Business> findByOwnerId(Long ownerId);
}
