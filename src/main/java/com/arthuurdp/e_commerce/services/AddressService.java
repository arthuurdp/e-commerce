package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.dtos.address.CepLookupResponse;
import com.arthuurdp.e_commerce.domain.entities.Address;
import com.arthuurdp.e_commerce.domain.entities.City;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.domain.dtos.address.AddressResponse;
import com.arthuurdp.e_commerce.domain.dtos.address.CreateAddressRequest;
import com.arthuurdp.e_commerce.domain.dtos.address.UpdateAddressRequest;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.AddressRepository;
import com.arthuurdp.e_commerce.repositories.CityRepository;
import com.arthuurdp.e_commerce.services.mappers.AddressMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;
    private final CityService cityService;
    private final AddressMapper mapper;

    public AddressService(AddressRepository addressRepository, CityRepository cityRepository, CityService cityService, AddressMapper mapper) {
        this.addressRepository = addressRepository;
        this.cityRepository = cityRepository;
        this.cityService = cityService;
        this.mapper = mapper;
    }

    public AddressResponse findById(Long id, Long userId) {
        return addressRepository.findByIdAndUserId(id, userId).map(mapper::toAddressResponse).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    public Page<AddressResponse> findAll(int page, int size, Long userId) {
        return addressRepository.findByUserId(PageRequest.of(page, size), userId).map(mapper::toAddressResponse);
    }

    @Transactional
    public AddressResponse create(CreateAddressRequest req, User user) {
        CepLookupResponse lookup = cityService.lookupByCep(req.postalCode());

        City city = cityRepository.findById(lookup.cityId()).orElseThrow(() -> new ResourceNotFoundException("City not found"));

        Address address = new Address(
                req.name(),
                req.street() != null ? req.street() : lookup.street(),
                req.number(),
                req.complement(),
                req.neighborhood() != null ? req.neighborhood() : lookup.neighborhood(),
                req.postalCode()
        );

        address.setCity(city);
        address.setUser(user);

        return mapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse update(Long id, UpdateAddressRequest req, User user) {
        Address address = addressRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (req.name() != null) {
            address.setName(req.name());
        }
        if (req.number() != null) {
            address.setNumber(req.number());
        }
        if (req.complement() != null) {
            address.setComplement(req.complement());
        }
        if (req.postalCode() != null) {
            CepLookupResponse lookup = cityService.lookupByCep(req.postalCode());
            City city = cityRepository.findById(lookup.cityId()).orElseThrow(() -> new ResourceNotFoundException("City not found"));

            address.setPostalCode(req.postalCode());
            address.setCity(city);
            address.setStreet(req.street() != null ? req.street() : lookup.street());
            address.setNeighborhood(req.neighborhood() != null ? req.neighborhood() : lookup.neighborhood());
        } else {
            if (req.street() != null) {
                address.setStreet(req.street());
            }
            if (req.neighborhood() != null) {
                address.setNeighborhood(req.neighborhood());
            }
        }
        return mapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public void delete(Long id, User user) {
        addressRepository.delete(addressRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() -> new ResourceNotFoundException("Address not found")));
    }
}
