package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.clients.ViaCepClient;
import com.arthuurdp.e_commerce.domain.dtos.address.CepLookupResponse;
import com.arthuurdp.e_commerce.domain.dtos.address.CityResponse;
import com.arthuurdp.e_commerce.domain.entities.City;
import com.arthuurdp.e_commerce.domain.entities.State;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CityRepository;
import com.arthuurdp.e_commerce.repositories.StateRepository;
import com.arthuurdp.e_commerce.services.mappers.AddressMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;
    private final AddressMapper mapper;
    private final ViaCepClient viaCepClient;

    public CityService(CityRepository cityRepository, StateRepository stateRepository, AddressMapper mapper, ViaCepClient viaCepClient) {
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
        this.mapper = mapper;
        this.viaCepClient = viaCepClient;
    }

    public Page<CityResponse> searchCities(Long stateId, String query, int page, int size) {
        return cityRepository.findByStateIdAndNameContainingIgnoreCase(PageRequest.of(page, size), stateId, query).map(mapper::toCityResponse);
    }

    public CityResponse findById(Long id) {
        return cityRepository.findById(id).map(mapper::toCityResponse).orElseThrow(() -> new ResourceNotFoundException("City not found"));
    }

    public CepLookupResponse lookupByCep(String cep) {
        ViaCepClient.ViaCepResponse viaCep = viaCepClient.lookup(cep);

        if (viaCep == null || viaCep.erro()) {
            throw new ResourceNotFoundException("CEP not found");
        }

        State state = stateRepository.findByUf(viaCep.stateUf()).orElseThrow(() -> new ResourceNotFoundException("State not found"));
        City city = cityRepository.findByNameIgnoreCaseAndStateId(viaCep.city(), state.getId()).orElseThrow(() -> new ResourceNotFoundException("City not found"));

        return new CepLookupResponse(
                viaCep.cep(),
                viaCep.logradouro(),
                viaCep.bairro(),
                city.getId(),
                city.getName(),
                state.getId(),
                state.getName(),
                state.getUf()
        );
    }
}
