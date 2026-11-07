package com.example.booking.service;

import com.example.booking.dto.business.BusinessRequest;
import com.example.booking.dto.business.BusinessResponse;
import com.example.booking.dto.services.ServiceRequest;
import com.example.booking.dto.services.ServiceResponse;
import com.example.booking.entity.Business;
import com.example.booking.entity.ServiceOffering;
import com.example.booking.entity.User;
import com.example.booking.exceptions.ForbiddenException;
import com.example.booking.exceptions.NotFoundException;
import com.example.booking.repository.BusinessRepository;
import com.example.booking.repository.ServiceOfferingRepository;
import com.example.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final ServiceOfferingRepository serviceRepository;
    private final UserRepository userRepository;

    @Transactional
    public BusinessResponse create(String ownerEmail, BusinessRequest req) {
        User owner = userRepository.findByEmail(ownerEmail).orElseThrow();
        Business b = new Business();
        b.setOwner(owner);
        b.setName(req.name().trim());
        b.setDescription(req.description());
        b.setAddress(req.address());
        b.setPhone(req.phone());
        return BusinessResponse.from(businessRepository.save(b));
    }

    @Transactional
    public List<BusinessResponse> listAll() {
        return businessRepository.findAll().stream().map(BusinessResponse::from).toList();
    }

    @Transactional
    public BusinessResponse get(Long id) {
        return BusinessResponse.from(find(id));
    }

    @Transactional
    public ServiceResponse addService(String ownerEmail, Long businessId, ServiceRequest req) {
        Business b = find(businessId);
        if (!b.getOwner().getEmail().equals(ownerEmail)) {
            throw new ForbiddenException("Не сте сопственик на овој бизнис");
        }
        ServiceOffering s = new ServiceOffering();
        s.setBusiness(b);
        s.setName(req.name().trim());
        s.setDurationMinutes(req.durationMinutes());
        s.setPrice(req.price());
        return ServiceResponse.from(serviceRepository.save(s));
    }

    @Transactional
    public List<ServiceResponse> listServices(Long businessId) {
        find(businessId);
        return serviceRepository.findByBusinessId(businessId).stream()
                .map(ServiceResponse::from).toList();
    }

    private Business find(Long id) {
        return businessRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бизнисот не постои"));
    }
}