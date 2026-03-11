package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.dtos.address.AddressResponse;
import com.arthuurdp.e_commerce.domain.dtos.address.CreateAddressRequest;
import com.arthuurdp.e_commerce.domain.dtos.address.UpdateAddressRequest;
import com.arthuurdp.e_commerce.domain.dtos.address.CityResponse;
import com.arthuurdp.e_commerce.domain.dtos.address.StateResponse;
import com.arthuurdp.e_commerce.domain.entities.Address;
import com.arthuurdp.e_commerce.domain.entities.City;
import com.arthuurdp.e_commerce.domain.entities.User;
import com.arthuurdp.e_commerce.domain.enums.Gender;
import com.arthuurdp.e_commerce.domain.enums.Role;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.AddressRepository;
import com.arthuurdp.e_commerce.repositories.CityRepository;
import com.arthuurdp.e_commerce.services.mappers.AddressMapper;
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
    @Mock private AddressMapper mapper;

    @InjectMocks
    private AddressService addressService;

    private User user;
    private City city;
    private Address address;
    private AddressResponse addressResponse;

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
            when(addressRepository.findByUserId(any(PageRequest.class), eq(1L)))
                    .thenReturn(Page.empty());

            Page<AddressResponse> result = addressService.findAll(0, 10, 1L);

            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("creates address and returns AddressResponse")
        void shouldCreateAddressSuccessfully() {
            CreateAddressRequest req = new CreateAddressRequest(
                    "Home", "Main St", 100, "Apt 1", "Downtown", 1L, "01310100", 10L
            );

            when(cityRepository.findById(10L)).thenReturn(Optional.of(city));
            when(addressRepository.save(any(Address.class))).thenReturn(address);
            when(mapper.toAddressResponse(address)).thenReturn(addressResponse);

            AddressResponse response = addressService.create(req, user);

            assertThat(response).isEqualTo(addressResponse);
            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("assigns city and user to address before saving")
        void shouldAssignCityAndUserBeforeSaving() {
            CreateAddressRequest req = new CreateAddressRequest(
                    "Home", "Main St", 100, "Apt 1", "Downtown", 1L, "01310100", 10L
            );

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
        @DisplayName("throws ResourceNotFoundException when city does not exist")
        void shouldThrowWhenCityNotFound() {
            CreateAddressRequest req = new CreateAddressRequest(
                    "Home", "Main St", 100, "Apt 1", "Downtown", 1L, "01310100", 99L
            );

            when(cityRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.create(req, user)).isInstanceOf(ResourceNotFoundException.class).hasMessage("City not found");

            verify(addressRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("updates only non-null fields and returns AddressResponse")
        void shouldUpdateNonNullFields() {
            UpdateAddressRequest req = new UpdateAddressRequest(
                    "Work", "Second Ave", null, null, null, null, null
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
        @DisplayName("updates city when cityId is provided and exists")
        void shouldUpdateCityWhenProvided() {
            City newCity = new City();
            newCity.setId(20L);
            newCity.setName("Rio de Janeiro");

            UpdateAddressRequest req = new UpdateAddressRequest(
                    null, null, null, null, null, 20L, null
            );

            when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
            when(cityRepository.existsById(20L)).thenReturn(true);
            when(cityRepository.findById(20L)).thenReturn(Optional.of(newCity));
            when(addressRepository.save(address)).thenReturn(address);
            when(mapper.toAddressResponse(address)).thenReturn(addressResponse);

            addressService.update(1L, req, user);

            assertThat(address.getCity()).isEqualTo(newCity);
        }

        @Test
        @DisplayName("does not update city when cityId does not exist")
        void shouldNotUpdateCityWhenCityIdNotFound() {
            UpdateAddressRequest req = new UpdateAddressRequest(
                    null, null, null, null, null, 99L, null
            );

            when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
            when(cityRepository.existsById(99L)).thenReturn(false);
            when(addressRepository.save(address)).thenReturn(address);
            when(mapper.toAddressResponse(address)).thenReturn(addressResponse);

            addressService.update(1L, req, user);

            assertThat(address.getCity()).isEqualTo(city);
            verify(cityRepository, never()).findById(any());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when address does not belong to user")
        void shouldThrowWhenAddressNotFound() {
            UpdateAddressRequest req = new UpdateAddressRequest(
                    "Work", null, null, null, null, null, null
            );

            when(addressRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> addressService.update(99L, req, user)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Address not found");

            verify(addressRepository, never()).save(any());
        }}


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

            assertThatThrownBy(() -> addressService.delete(99L, user))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Address not found");

            verify(addressRepository, never()).delete(any());
        }
    }
}