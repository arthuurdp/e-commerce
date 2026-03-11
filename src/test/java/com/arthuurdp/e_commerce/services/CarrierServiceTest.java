package com.arthuurdp.e_commerce.services;

import com.arthuurdp.e_commerce.domain.dtos.carrier.CarrierResponse;
import com.arthuurdp.e_commerce.domain.dtos.carrier.CreateCarrierRequest;
import com.arthuurdp.e_commerce.domain.dtos.carrier.UpdateCarrierRequest;
import com.arthuurdp.e_commerce.domain.entities.Carrier;
import com.arthuurdp.e_commerce.domain.enums.CarrierStatus;
import com.arthuurdp.e_commerce.domain.enums.Region;
import com.arthuurdp.e_commerce.exceptions.ConflictException;
import com.arthuurdp.e_commerce.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.repositories.CarrierRepository;
import com.arthuurdp.e_commerce.services.mappers.CarrierMapper;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrierServiceTest {

    @Mock private CarrierRepository repo;
    @Mock private CarrierMapper mapper;

    @InjectMocks
    private CarrierService carrierService;

    private Carrier carrier;
    private CarrierResponse carrierResponse;

    @BeforeEach
    void setUp() {
        carrier = new Carrier("Fast Carrier", "12345678000199", "fast@carrier.com", "11999999999", Region.SOUTH);
        carrier.setId(1L);
        carrier.setStatus(CarrierStatus.AVAILABLE);

        carrierResponse = new CarrierResponse(1L, "Fast Carrier", CarrierStatus.AVAILABLE, Region.SOUTH);
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("returns CarrierResponse when carrier exists")
        void shouldReturnCarrierResponse() {
            when(repo.findById(1L)).thenReturn(Optional.of(carrier));
            when(mapper.toCarrierResponse(carrier)).thenReturn(carrierResponse);

            CarrierResponse response = carrierService.findById(1L);

            assertThat(response).isEqualTo(carrierResponse);
            verify(repo).findById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when carrier does not exist")
        void shouldThrowWhenCarrierNotFound() {
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> carrierService.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Carrier not found");
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("returns paginated CarrierResponse list")
        void shouldReturnPagedCarriers() {
            Page<Carrier> page = new PageImpl<>(List.of(carrier));

            when(repo.findAll(any(PageRequest.class))).thenReturn(page);
            when(mapper.toCarrierResponse(carrier)).thenReturn(carrierResponse);

            Page<CarrierResponse> result = carrierService.findAll(0, 10);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0)).isEqualTo(carrierResponse);
        }

        @Test
        @DisplayName("returns empty page when no carriers exist")
        void shouldReturnEmptyPage() {
            when(repo.findAll(any(PageRequest.class))).thenReturn(Page.empty());

            Page<CarrierResponse> result = carrierService.findAll(0, 10);

            assertThat(result.getContent()).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // findAllByRegion()
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("findAllByRegion()")
    class FindAllByRegion {

        @Test
        @DisplayName("filters by region and status when status is provided")
        void shouldFilterByRegionAndStatus() {
            Page<Carrier> page = new PageImpl<>(List.of(carrier));

            when(repo.findByRegionAndStatus(eq(Region.SOUTH), eq(CarrierStatus.AVAILABLE), any(PageRequest.class)))
                    .thenReturn(page);
            when(mapper.toCarrierResponse(carrier)).thenReturn(carrierResponse);

            Page<CarrierResponse> result = carrierService.findAllByRegion(0, 10, Region.SOUTH, CarrierStatus.AVAILABLE);

            assertThat(result.getContent()).hasSize(1);
            verify(repo).findByRegionAndStatus(eq(Region.SOUTH), eq(CarrierStatus.AVAILABLE), any(PageRequest.class));
            verify(repo, never()).findByRegion(any(), any());
        }

        @Test
        @DisplayName("filters by region only when status is null")
        void shouldFilterByRegionOnly() {
            Page<Carrier> page = new PageImpl<>(List.of(carrier));

            when(repo.findByRegion(eq(Region.SOUTH), any(PageRequest.class))).thenReturn(page);
            when(mapper.toCarrierResponse(carrier)).thenReturn(carrierResponse);

            Page<CarrierResponse> result = carrierService.findAllByRegion(0, 10, Region.SOUTH, null);

            assertThat(result.getContent()).hasSize(1);
            verify(repo).findByRegion(eq(Region.SOUTH), any(PageRequest.class));
            verify(repo, never()).findByRegionAndStatus(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("creates carrier and returns CarrierResponse")
        void shouldCreateCarrierSuccessfully() {
            CreateCarrierRequest req = new CreateCarrierRequest(
                    "Fast Carrier", "12345678000199", "fast@carrier.com", "11999999999", Region.SOUTH
            );

            when(repo.existsByEmail(req.email())).thenReturn(false);
            when(repo.save(any(Carrier.class))).thenReturn(carrier);
            when(mapper.toCarrierResponse(carrier)).thenReturn(carrierResponse);

            CarrierResponse response = carrierService.create(req);

            assertThat(response).isEqualTo(carrierResponse);
            verify(repo).save(any(Carrier.class));
        }

        @Test
        @DisplayName("saves carrier with correct fields")
        void shouldSaveCarrierWithCorrectFields() {
            CreateCarrierRequest req = new CreateCarrierRequest(
                    "Fast Carrier", "12345678000199", "fast@carrier.com", "11999999999", Region.SOUTH
            );

            when(repo.existsByEmail(any())).thenReturn(false);
            when(repo.save(any(Carrier.class))).thenReturn(carrier);
            when(mapper.toCarrierResponse(any())).thenReturn(carrierResponse);

            carrierService.create(req);

            ArgumentCaptor<Carrier> captor = ArgumentCaptor.forClass(Carrier.class);
            verify(repo).save(captor.capture());

            assertThat(captor.getValue().getName()).isEqualTo("Fast Carrier");
            assertThat(captor.getValue().getEmail()).isEqualTo("fast@carrier.com");
            assertThat(captor.getValue().getRegion()).isEqualTo(Region.SOUTH);
        }

        @Test
        @DisplayName("throws ConflictException when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {
            CreateCarrierRequest req = new CreateCarrierRequest(
                    "Fast Carrier", "12345678000199", "fast@carrier.com", "11999999999", Region.SOUTH
            );

            when(repo.existsByEmail(req.email())).thenReturn(true);

            assertThatThrownBy(() -> carrierService.create(req)).isInstanceOf(ConflictException.class).hasMessage("Carrier already exists");

            verify(repo, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("updates only non-null fields and returns CarrierResponse")
        void shouldUpdateNonNullFields() {
            UpdateCarrierRequest req = new UpdateCarrierRequest("New Name", null, null, null, null);

            when(repo.findById(1L)).thenReturn(Optional.of(carrier));
            when(repo.save(carrier)).thenReturn(carrier);
            when(mapper.toCarrierResponse(carrier)).thenReturn(carrierResponse);

            CarrierResponse response = carrierService.update(1L, req);

            assertThat(response).isEqualTo(carrierResponse);
            assertThat(carrier.getName()).isEqualTo("New Name");
            assertThat(carrier.getEmail()).isEqualTo("fast@carrier.com");
        }

        @Test
        @DisplayName("updates email when it is not in use by another carrier")
        void shouldUpdateEmailWhenNotInUse() {
            UpdateCarrierRequest req = new UpdateCarrierRequest(null, "new@carrier.com", null, null, null);

            when(repo.findById(1L)).thenReturn(Optional.of(carrier));
            when(repo.existsByEmail("new@carrier.com")).thenReturn(false);
            when(repo.save(carrier)).thenReturn(carrier);
            when(mapper.toCarrierResponse(carrier)).thenReturn(carrierResponse);

            carrierService.update(1L, req);

            assertThat(carrier.getEmail()).isEqualTo("new@carrier.com");
        }

        @Test
        @DisplayName("does not throw when email is the same as the carrier's current email")
        void shouldNotThrowWhenEmailIsUnchanged() {
            UpdateCarrierRequest req = new UpdateCarrierRequest(null, "fast@carrier.com", null, null, null);

            when(repo.findById(1L)).thenReturn(Optional.of(carrier));
            when(repo.existsByEmail("fast@carrier.com")).thenReturn(true);
            when(repo.save(carrier)).thenReturn(carrier);
            when(mapper.toCarrierResponse(carrier)).thenReturn(carrierResponse);

            carrierService.update(1L, req);

            assertThat(carrier.getEmail()).isEqualTo("fast@carrier.com");
        }

        @Test
        @DisplayName("throws ConflictException when email is already used by another carrier")
        void shouldThrowWhenEmailInUseByAnotherCarrier() {
            UpdateCarrierRequest req = new UpdateCarrierRequest(null, "taken@carrier.com", null, null, null);

            when(repo.findById(1L)).thenReturn(Optional.of(carrier));
            when(repo.existsByEmail("taken@carrier.com")).thenReturn(true);

            assertThatThrownBy(() -> carrierService.update(1L, req)).isInstanceOf(ConflictException.class).hasMessage("Email already in use by another carrier");

            verify(repo, never()).save(any());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when carrier does not exist")
        void shouldThrowWhenCarrierNotFound() {
            UpdateCarrierRequest req = new UpdateCarrierRequest("Name", null, null, null, null);

            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> carrierService.update(99L, req)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Carrier not found");

            verify(repo, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("deletes carrier when it exists")
        void shouldDeleteCarrierSuccessfully() {
            when(repo.findById(1L)).thenReturn(Optional.of(carrier));

            carrierService.delete(1L);

            verify(repo).delete(carrier);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when carrier does not exist")
        void shouldThrowWhenCarrierNotFound() {
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> carrierService.delete(99L)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Carrier not found");

            verify(repo, never()).delete(any());
        }
    }
}