package com.onclass.ms_bootcamp.application.usecase;

import com.onclass.ms_bootcamp.domain.exception.BusinessException;
import com.onclass.ms_bootcamp.domain.exception.ErrorCode;
import com.onclass.ms_bootcamp.domain.model.Bootcamp;
import com.onclass.ms_bootcamp.domain.repository.BootcampRepository;
import com.onclass.ms_bootcamp.domain.repository.CapabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterBootcampUseCaseTest {

    @Mock
    private BootcampRepository bootcampRepository;

    @Mock
    private CapabilityRepository capabilityRepository;

    @InjectMocks
    private RegisterBootcampUseCase registerBootcampUseCase;

    private Bootcamp validBootcamp;

    @BeforeEach
    void setUp() {
        validBootcamp = new Bootcamp();
        validBootcamp.setName("Bootcamp Java");
        validBootcamp.setDescription("Desarrollo con SpringBoot");
        validBootcamp.setLaunchDate(LocalDate.of(2026, 12, 1));
        validBootcamp.setDuration(12);
        validBootcamp.setCapabilityIds(List.of(1L, 2L));
    }

    @Test
    void successRegisterBootcamp() {
        Bootcamp savedBootcamp = new Bootcamp();
        savedBootcamp.setId(10L);
        savedBootcamp.setName(validBootcamp.getName());

        when(bootcampRepository.existsByName(validBootcamp.getName())).thenReturn(Mono.just(false));
        when(bootcampRepository.save(validBootcamp)).thenReturn(Mono.just(savedBootcamp));
        when(capabilityRepository.associateCapabilities(10L, List.of(1L, 2L))).thenReturn(Mono.empty());

        StepVerifier.create(registerBootcampUseCase.register(validBootcamp))
            .assertNext(result -> {
                assertEquals(10L, result.getId());
                assertEquals("Bootcamp Java", result.getName());
            })
            .verifyComplete();

        verify(bootcampRepository, never()).deleteById(anyLong());
    }

    @Test
    void failWhenLessThanOneCapabilities() {
        validBootcamp.setCapabilityIds(List.of());

        StepVerifier.create(registerBootcampUseCase.register(validBootcamp))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_CAPABILITIES_COUNT.getCode()))
            .verify();
    }

    @Test
    void failWhenMoreThanFourCapabilities() {
        validBootcamp.setCapabilityIds(List.of(1L, 2L, 3L, 4L, 5L));

        StepVerifier.create(registerBootcampUseCase.register(validBootcamp))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_CAPABILITIES_COUNT.getCode()))
            .verify();

        verify(bootcampRepository, never()).existsByName(any());
    }

    @Test
    void failWhenCapabilityIdsAreRepeated() {
        validBootcamp.setCapabilityIds(List.of(1L, 1L));

        StepVerifier.create(registerBootcampUseCase.register(validBootcamp))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.DUPLICATE_CAPABILITIES.getCode()))
            .verify();

        verify(bootcampRepository, never()).existsByName(any());
    }

    @Test
    void failWhenBootcampNameExists() {
        when(bootcampRepository.existsByName(validBootcamp.getName())).thenReturn(Mono.just(true));

        StepVerifier.create(registerBootcampUseCase.register(validBootcamp))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.BOOTCAMP_ALREADY_EXISTS.getCode()))
            .verify();

        verify(bootcampRepository, never()).save(any());
    }

    @Test
    void rollbackSavedBootcampWhenCapabilityServiceFails() {
        Bootcamp savedBootcamp = new Bootcamp();
        savedBootcamp.setId(10L);
        savedBootcamp.setName(validBootcamp.getName());

        when(bootcampRepository.existsByName(validBootcamp.getName())).thenReturn(Mono.just(false));
        when(bootcampRepository.save(validBootcamp)).thenReturn(Mono.just(savedBootcamp));
        when(capabilityRepository.associateCapabilities(anyLong(), anyList()))
            .thenReturn(Mono.error(new BusinessException(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE)));
        when(bootcampRepository.deleteById(10L)).thenReturn(Mono.empty());

        StepVerifier.create(registerBootcampUseCase.register(validBootcamp))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE.getCode()))
            .verify();

        verify(bootcampRepository).deleteById(10L);
    }
}
