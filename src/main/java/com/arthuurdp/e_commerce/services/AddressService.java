package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.entities.Address;
import com.arthuurdp.e_commerce.entities.City;
import com.arthuurdp.e_commerce.entities.User;
import com.arthuurdp.e_commerce.entities.dtos.address.AddressResponse;
import com.arthuurdp.e_commerce.entities.dtos.address.CreateAddressRequest;
import com.arthuurdp.e_commerce.entities.dtos.address.UpdateAddressRequest;
import com.arthuurdp.e_commerce.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.AddressRepository;
import com.arthuurdp.e_commerce.repositories.CityRepository;
import com.arthuurdp.e_commerce.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;
    private final UserRepository userRepository;
    private final EntityMapperService entityMapperService;
    private final AuthService authService;

    public AddressService(AddressRepository addressRepository, CityRepository cityRepository, UserRepository userRepository, EntityMapperService entityMapperService, AuthService authService) {
        this.addressRepository = addressRepository;
        this.cityRepository = cityRepository;
        this.userRepository = userRepository;
        this.entityMapperService = entityMapperService;
        this.authService = authService;
    }

    @PreAuthorize("hasRole('USER')")
    public AddressResponse findById(Long id) {
        User user = authService.getCurrentUser();
        return addressRepository.findByIdAndUserId(id, user.getId()).map(entityMapperService::toAddressResponse).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    @PreAuthorize("hasRole('USER')")
    public Page<AddressResponse> findAll(int page, int size) {
        User user = authService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return addressRepository.findByUserId(pageable, user.getId()).map(entityMapperService::toAddressResponse);
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public AddressResponse create(CreateAddressRequest req) {
        User user = authService.getCurrentUser();
        City city = cityRepository.findById(req.cityId()).orElseThrow(() -> new ResourceNotFoundException("City not found"));

        Address address = new Address();
        address.setName((req.name() != null) ? req.name() : "Default");
        address.setStreet(req.street());
        address.setNumber(req.number());
        address.setComplement(req.complement());
        address.setNeighborhood(req.neighborhood());
        address.setCity(city);
        address.setUser(user);

        return entityMapperService.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public AddressResponse update(Long id, UpdateAddressRequest req) {
        User user = authService.getCurrentUser();
        Address address = addressRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        boolean updated = false;

        if (req.name() != null) {
            address.setName(req.name());
            updated = true;
        }
        if (req.street() != null) {
            address.setStreet(req.street());
            updated = true;
        }
        if (req.number() != null) {
            address.setNumber(req.number());
            updated = true;
        }
        if (req.complement() != null) {
            address.setComplement(req.complement());
            updated = true;
        }
        if (req.neighborhood() != null) {
            address.setNeighborhood(req.neighborhood());
            updated = true;
        }
        if (!updated) {
            throw new BadRequestException("No valid fields provided");
        }
        return entityMapperService.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public void delete(Long id) {
        User user = authService.getCurrentUser();
        Address address = addressRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressRepository.delete(address);
    }
}
