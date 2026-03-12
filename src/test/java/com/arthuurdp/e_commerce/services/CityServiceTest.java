package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.modules.address.dtos.CityResponse;
import com.arthuurdp.e_commerce.modules.address.entity.City;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.address.CityService;
import com.arthuurdp.e_commerce.modules.address.CityRepository;
import com.arthuurdp.e_commerce.modules.address.mapper.AddressMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock private CityRepository repo;
    @Mock private AddressMapper mapper;

    @InjectMocks
    private CityService cityService;

    private City city;
    private CityResponse cityResponse;

    @BeforeEach
    void setUp() {
        city = new City();
        city.setId(1L);
        city.setName("São Paulo");

        cityResponse = new CityResponse(1L, "São Paulo");
    }

    @Nested
    @DisplayName("searchCities()")
    class SearchCities {

        @Test
        @DisplayName("returns paginated cities matching query and stateId")
        void shouldReturnMatchingCities() {
            Page<City> page = new PageImpl<>(List.of(city));

            when(repo.findByStateIdAndNameContainingIgnoreCase(any(PageRequest.class), eq(1L), eq("São"))).thenReturn(page);
            when(mapper.toCityResponse(city)).thenReturn(cityResponse);

            Page<CityResponse> result = cityService.searchCities(1L, "São", 0, 10);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0)).isEqualTo(cityResponse);
            verify(repo).findByStateIdAndNameContainingIgnoreCase(any(PageRequest.class), eq(1L), eq("São"));
        }

        @Test
        @DisplayName("returns empty page when no cities match")
        void shouldReturnEmptyPageWhenNoMatch() {
            when(repo.findByStateIdAndNameContainingIgnoreCase(any(PageRequest.class), eq(1L), eq("xyz"))).thenReturn(Page.empty());

            Page<CityResponse> result = cityService.searchCities(1L, "xyz", 0, 10);

            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("returns CityResponse when city exists")
        void shouldReturnCityResponse() {
            when(repo.findById(1L)).thenReturn(Optional.of(city));
            when(mapper.toCityResponse(city)).thenReturn(cityResponse);

            CityResponse result = cityService.findById(1L);

            assertThat(result).isEqualTo(cityResponse);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when city does not exist")
        void shouldThrowWhenCityNotFound() {
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cityService.findById(99L)).isInstanceOf(ResourceNotFoundException.class).hasMessage("City not found");
        }
    }
}