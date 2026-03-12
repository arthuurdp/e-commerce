package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.modules.address.AddressRepository;
import com.arthuurdp.e_commerce.modules.address.AddressService;
import com.arthuurdp.e_commerce.modules.address.CityRepository;
import com.arthuurdp.e_commerce.modules.address.CityService;
import com.arthuurdp.e_commerce.modules.address.dtos.*;
import com.arthuurdp.e_commerce.modules.address.entity.Address;
import com.arthuurdp.e_commerce.modules.address.entity.City;
import com.arthuurdp.e_commerce.modules.address.mapper.AddressMapper;
import com.arthuurdp.e_commerce.modules.user.entity.User;
import com.arthuurdp.e_commerce.modules.user.enums.Gender;
import com.arthuurdp.e_commerce.modules.user.enums.Role;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock private AddressRepository addressRepository;
    @Mock private CityRepository cityRepository;
    @Mock private CityService cityService;
    @Mock private AddressMapper mapper;

    @InjectMocks
    private AddressService addressService;

    private User user;
    private City city;
    private Address address;
    private AddressResponse addressResponse;
    private CepLookupResponse cepLookup;

    @BeforeEach
    void setUp() {
        user = new User(
                "John", "Doe", "john@example.com", "encoded",
                "52998224725", "11987654321",
                LocalDate.of(1995, 6, 15), Gender.MALE, Role.ROLE_USER
        );
        user.setId(1L);

        city = new City();
        city.setId(10L);
        city.setName("São Paulo");

        address = new Address("Home", "Main St", 100, "Apt 1", "Downtown", "01310100");
        address.setId(1L);
        address.setCity(city);
        address.setUser(user);

        addressResponse = new AddressResponse(
                1L, "Home", "Main St", 100, "Apt 1", "Downtown",
                new CityResponse(10L, "São Paulo"),
                new StateResponse(1L, "São Paulo", "SP")
        );

        cepLookup = new CepLookupResponse(
                "01310100", "Main St", "Downtown",
                10L, "São Paulo",
                1L, "São Paulo", "SP"
        );
    }

    @Nested
    @DisplayName("findById()")
    class FindById {
        @Test
        @DisplayName("returns AddressResponse when address belongs to user")
        void shouldReturnAddressResponse() {
            when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
            when(mapper.toAddressResponse(address)).thenReturn(addressResponse);

            AddressResponse response = addressService.findById(1L, 1L);

            assertThat(response).isEqualTo(addressResponse);
            verify(addressRepository).findByIdAndUserId(1L, 1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when address does not exist or does not belong to user")
        void shouldThrowWhenAddressNotFound() {
            when(addressRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.findById(99L, 1L)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Address not found");
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {
        @Test
        @DisplayName("returns paginated AddressResponse for the user")
        void shouldReturnPagedAddresses() {
            Page<Address> addressPage = new PageImpl<>(List.of(address));

            when(addressRepository.findByUserId(any(PageRequest.class), eq(1L))).thenReturn(addressPage);
            when(mapper.toAddressResponse(address)).thenReturn(addressResponse);

            Page<AddressResponse> result = addressService.findAll(0, 10, 1L);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0)).isEqualTo(addressResponse);
        }

        @Test
        @DisplayName("returns empty page when user has no addresses")
        void shouldReturnEmptyPage() {
            when(addressRepository.findByUserId(any(PageRequest.class), eq(1L))).thenReturn(Page.empty());

            Page<AddressResponse> result = addressService.findAll(0, 10, 1L);

            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {
        @Test
        @DisplayName("creates address using CEP lookup and returns AddressResponse")
        void shouldCreateAddressSuccessfully() {
            CreateAddressRequest req = new CreateAddressRequest(
                    "Home", "Main St", 100, "Apt 1", "Downtown", "01310100"
            );

            when(cityService.lookupByCep("01310100")).thenReturn(cepLookup);
            when(cityRepository.findById(10L)).thenReturn(Optional.of(city));
            when(addressRepository.save(any(Address.class))).thenReturn(address);
            when(mapper.toAddressResponse(address)).thenReturn(addressResponse);

            AddressResponse response = addressService.create(req, user);

            assertThat(response).isEqualTo(addressResponse);
            verify(cityService).lookupByCep("01310100");
            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("falls back to CEP lookup street and neighborhood when not provided in request")
        void shouldFallbackToCepLookupStreetAndNeighborhood() {
            CreateAddressRequest req = new CreateAddressRequest(
                    "Home", null, 100, null, null, "01310100"
            );

            when(cityService.lookupByCep("01310100")).thenReturn(cepLookup);
            when(cityRepository.findById(10L)).thenReturn(Optional.of(city));
            when(addressRepository.save(any(Address.class))).thenReturn(address);
            when(mapper.toAddressResponse(any())).thenReturn(addressResponse);

            addressService.create(req, user);

            ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
            verify(addressRepository).save(captor.capture());

            assertThat(captor.getValue().getStreet()).isEqualTo("Main St");
            assertThat(captor.getValue().getNeighborhood()).isEqualTo("Downtown");
        }

        @Test
        @DisplayName("assigns city and user to address before saving")
        void shouldAssignCityAndUserBeforeSaving() {
            CreateAddressRequest req = new CreateAddressRequest(
                    "Home", "Main St", 100, "Apt 1", "Downtown", "01310100"
            );

            when(cityService.lookupByCep("01310100")).thenReturn(cepLookup);
            when(cityRepository.findById(10L)).thenReturn(Optional.of(city));
            when(addressRepository.save(any(Address.class))).thenReturn(address);
            when(mapper.toAddressResponse(any())).thenReturn(addressResponse);

            addressService.create(req, user);

            ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
            verify(addressRepository).save(captor.capture());

            assertThat(captor.getValue().getCity()).isEqualTo(city);
            assertThat(captor.getValue().getUser()).isEqualTo(user);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when CEP lookup returns unknown city")
        void shouldThrowWhenCityNotFound() {
            CreateAddressRequest req = new CreateAddressRequest(
                    "Home", "Main St", 100, "Apt 1", "Downtown", "01310100"
            );

            when(cityService.lookupByCep("01310100")).thenReturn(cepLookup);
            when(cityRepository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.create(req, user)).isInstanceOf(ResourceNotFoundException.class).hasMessage("City not found");

            verify(addressRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when CEP is not found")
        void shouldThrowWhenCepNotFound() {
            CreateAddressRequest req = new CreateAddressRequest(
                    "Home", "Main St", 100, "Apt 1", "Downtown", "00000000"
            );

            when(cityService.lookupByCep("00000000")).thenThrow(new ResourceNotFoundException("CEP not found"));

            assertThatThrownBy(() -> addressService.create(req, user)).isInstanceOf(ResourceNotFoundException.class).hasMessage("CEP not found");

            verify(addressRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {
        @Test
        @DisplayName("updates only non-null fields without postalCode")
        void shouldUpdateNonNullFieldsWithoutPostalCode() {
            UpdateAddressRequest req = new UpdateAddressRequest(
                    "Work", "Second Ave", null, null, null, null
            );

            when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
            when(addressRepository.save(address)).thenReturn(address);
            when(mapper.toAddressResponse(address)).thenReturn(addressResponse);

            AddressResponse response = addressService.update(1L, req, user);

            assertThat(response).isEqualTo(addressResponse);
            assertThat(address.getName()).isEqualTo("Work");
            assertThat(address.getStreet()).isEqualTo("Second Ave");
            assertThat(address.getNumber()).isEqualTo(100);
        }

        @Test
        @DisplayName("updates city and street via CEP lookup when postalCode is provided")
        void shouldUpdateCityAndStreetWhenPostalCodeProvided() {
            City newCity = new City();
            newCity.setId(20L);
            newCity.setName("Rio de Janeiro");

            CepLookupResponse newLookup = new CepLookupResponse(
                    "20040020", "Av. Rio Branco", "Centro",
                    20L, "Rio de Janeiro",
                    2L, "Rio de Janeiro", "RJ"
            );

            UpdateAddressRequest req = new UpdateAddressRequest(
                    null, null, null, null, null, "20040020"
            );

            when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
            when(cityService.lookupByCep("20040020")).thenReturn(newLookup);
            when(cityRepository.findById(20L)).thenReturn(Optional.of(newCity));
            when(addressRepository.save(address)).thenReturn(address);
            when(mapper.toAddressResponse(address)).thenReturn(addressResponse);

            addressService.update(1L, req, user);

            assertThat(address.getCity()).isEqualTo(newCity);
            assertThat(address.getStreet()).isEqualTo("Av. Rio Branco");
            assertThat(address.getNeighborhood()).isEqualTo("Centro");
            assertThat(address.getPostalCode()).isEqualTo("20040020");
        }

        @Test
        @DisplayName("uses request street over CEP lookup street when postalCode and street are both provided")
        void shouldPreferRequestStreetOverCepLookup() {
            CepLookupResponse newLookup = new CepLookupResponse(
                    "20040020", "Av. Rio Branco", "Centro",
                    20L, "Rio de Janeiro",
                    2L, "Rio de Janeiro", "RJ"
            );

            City newCity = new City();
            newCity.setId(20L);

            UpdateAddressRequest req = new UpdateAddressRequest(
                    null, "My Custom Street", null, null, null, "20040020"
            );

            when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
            when(cityService.lookupByCep("20040020")).thenReturn(newLookup);
            when(cityRepository.findById(20L)).thenReturn(Optional.of(newCity));
            when(addressRepository.save(address)).thenReturn(address);
            when(mapper.toAddressResponse(address)).thenReturn(addressResponse);

            addressService.update(1L, req, user);

            assertThat(address.getStreet()).isEqualTo("My Custom Street");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when address does not belong to user")
        void shouldThrowWhenAddressNotFound() {
            UpdateAddressRequest req = new UpdateAddressRequest(
                    "Work", null, null, null, null, null
            );

            when(addressRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.update(99L, req, user)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Address not found");

            verify(addressRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {
        @Test
        @DisplayName("deletes address when it belongs to user")
        void shouldDeleteAddressSuccessfully() {
            when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));

            addressService.delete(1L, user);

            verify(addressRepository).delete(address);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when address does not belong to user")
        void shouldThrowWhenAddressNotFound() {
            when(addressRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.delete(99L, user)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Address not found");

            verify(addressRepository, never()).delete(any());
        }
    }
}