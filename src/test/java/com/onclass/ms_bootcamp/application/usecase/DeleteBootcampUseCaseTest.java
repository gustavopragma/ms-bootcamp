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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteBootcampUseCaseTest {

    @Mock
    private BootcampRepository bootcampRepository;

    @Mock
    private CapabilityRepository capabilityRepository;

    @InjectMocks
    private DeleteBootcampUseCase deleteBootcampUseCase;

    private Bootcamp bootcamp;

    @BeforeEach
    void setUp() {
        bootcamp = new Bootcamp(
            1L,
            "Bootcamp Java",
            "Fullstack Java",
            LocalDate.of(2026, 12, 1),
            12,
            null
            , null
        );
    }

    @Test
    void deleteBootcampSuccess() {
        Long bootcampId = bootcamp.getId();

        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.just(bootcamp));
        when(bootcampRepository.deleteById(bootcampId)).thenReturn(Mono.empty());
        when(capabilityRepository.deleteCapabilitiesByBootcampId(bootcampId)).thenReturn(Mono.empty());

        StepVerifier.create(deleteBootcampUseCase.deleteBootcamp(bootcampId))
            .verifyComplete();

        verify(bootcampRepository).deleteById(bootcampId);
        verify(capabilityRepository).deleteCapabilitiesByBootcampId(bootcampId);
        verify(bootcampRepository, never()).saveWithId(any());
    }

    @Test
    void errorWhenBootcampNotExists() {
        Long bootcampId = 99L;
        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.empty());

        StepVerifier.create(deleteBootcampUseCase.deleteBootcamp(bootcampId))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.BOOTCAMP_NOT_FOUND.getCode()))
            .verify();

        verify(bootcampRepository, never()).deleteById(anyLong());
        verify(capabilityRepository, never()).deleteCapabilitiesByBootcampId(anyLong());
    }

    @Test
    void errorWhenIdIsNull() {
        StepVerifier.create(deleteBootcampUseCase.deleteBootcamp(null))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.BOOTCAMP_NOT_FOUND.getCode()))
            .verify();

        verify(bootcampRepository, never()).findById(anyLong());
    }

    @Test
    void saveBootcampWhenCapabilityServiceFail() {
        Long bootcampId = bootcamp.getId();

        when(bootcampRepository.findById(bootcampId)).thenReturn(Mono.just(bootcamp));
        when(bootcampRepository.deleteById(bootcampId)).thenReturn(Mono.empty());
        when(capabilityRepository.deleteCapabilitiesByBootcampId(bootcampId))
            .thenReturn(Mono.error(new BusinessException(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE)));
        when(bootcampRepository.saveWithId(bootcamp)).thenReturn(Mono.empty());

        StepVerifier.create(deleteBootcampUseCase.deleteBootcamp(bootcampId))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE.getCode()))
            .verify();

        verify(bootcampRepository).deleteById(bootcampId);
        verify(bootcampRepository).saveWithId(bootcamp);
    }
}
