package com.example.booking.repository;

import com.example.booking.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    List<ServiceOffering> findByBusinessId(Long businessId);
}