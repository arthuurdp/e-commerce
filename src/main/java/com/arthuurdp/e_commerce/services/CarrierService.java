package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.entities.Carrier;
import com.arthuurdp.e_commerce.domain.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.domain.dtos.carrier.CreateCarrierRequest;
import com.arthuurdp.e_commerce.domain.dtos.carrier.UpdateCarrierRequest;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.domain.enums.CarrierStatus;
import com.arthuurdp.e_commerce.domain.enums.Region;
import com.arthuurdp.e_commerce.domain.enums.Role;
import com.arthuurdp.e_commerce.exceptions.ConflictException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CarrierRepository;
import com.arthuurdp.e_commerce.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CarrierService {
    private final CarrierRepository carrierRepository;
    private final EntityMapperService entityMapperService;

    public CarrierService(CarrierRepository carrierRepository, EntityMapperService entityMapperService) {
        this.carrierRepository = carrierRepository;
        this.entityMapperService = entityMapperService;
    }

    public CarrierResponse findById(Long id) {
        Carrier carrier = carrierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
        return entityMapperService.toCarrierResponse(carrier);
    }

    public Page<CarrierResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return carrierRepository.findAll(pageable).map(entityMapperService::toCarrierResponse);
    }

    public Page<CarrierResponse> findAllByRegion(int page, int size, Region region, CarrierStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        if (status != null) {
            return carrierRepository.findByRegionAndStatus(region, status, pageable).map(entityMapperService::toCarrierResponse);
        }
        return carrierRepository.findByRegion(region, pageable).map(entityMapperService::toCarrierResponse);
    }

    @Transactional
    public CarrierResponse create(CreateCarrierRequest req) {
        if (carrierRepository.existsByEmail(req.email())) {
            throw new ConflictException("Carrier already exists");
        }

        Carrier carrier = new Carrier(
                req.name(),
                req.cnpj(),
                req.email(),
                req.phone(),
                req.region()
        );

        return entityMapperService.toCarrierResponse(carrierRepository.save(carrier));
    }

    @Transactional
    public CarrierResponse update(Long id, UpdateCarrierRequest req) {
        Carrier carrier = carrierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));

        if (req.name() != null) {
            carrier.setName(req.name());
        }

        return entityMapperService.toCarrierResponse(carrierRepository.save(carrier));
    }

    public void delete(Long id) {
        Carrier carrier = carrierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carrier not found"));
        carrierRepository.deleteById(carrier.getId());
    }
}
