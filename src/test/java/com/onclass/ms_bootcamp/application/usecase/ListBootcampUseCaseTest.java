package com.onclass.ms_bootcamp.application.usecase;

import com.onclass.ms_bootcamp.domain.exception.BusinessException;
import com.onclass.ms_bootcamp.domain.exception.ErrorCode;
import com.onclass.ms_bootcamp.domain.model.Bootcamp;
import com.onclass.ms_bootcamp.domain.model.Capability;
import com.onclass.ms_bootcamp.domain.model.Technology;
import com.onclass.ms_bootcamp.domain.repository.BootcampRepository;
import com.onclass.ms_bootcamp.domain.repository.CapabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListBootcampUseCaseTest {

    @Mock
    private BootcampRepository bootcampRepository;

    @Mock
    private CapabilityRepository capabilityRepository;

    @InjectMocks
    private ListBootcampUseCase listBootcampUseCase;

    private Bootcamp bootcampA;
    private Bootcamp bootcampB;
    private Capability backend;
    private Capability frontend;
    private Capability mobile;

    @BeforeEach
    void setUp() {
        bootcampA = new Bootcamp();
        bootcampA.setId(1L);
        bootcampA.setName("Bootcamp A");
        bootcampA.setDescription("Bootcamp A Desc");
        bootcampA.setLaunchDate(LocalDate.of(2026, 10, 1));
        bootcampA.setDuration(10);

        bootcampB = new Bootcamp();
        bootcampB.setId(2L);
        bootcampB.setName("Bootcamp B");
        bootcampB.setDescription("Bootcamp B Desc");
        bootcampB.setLaunchDate(LocalDate.of(2026, 11, 1));
        bootcampB.setDuration(12);

        backend = new Capability(10L, "Backend", List.of(new Technology(100L, "Java")));
        frontend = new Capability(20L, "Frontend", List.of(new Technology(200L, "Angular")));
        mobile = new Capability(30L, "Mobile", List.of(new Technology(300L, "Kotlin")));
    }

    @Test
    void getBootcampsSortByNameDESCWithTechnologies() {
        when(bootcampRepository.findAll()).thenReturn(Flux.just(bootcampA, bootcampB));
        when(capabilityRepository.findCapabilitiesByBootcampId(1L)).thenReturn(Flux.just(backend));
        when(capabilityRepository.findCapabilitiesByBootcampId(2L)).thenReturn(Flux.just(frontend, mobile));

        StepVerifier.create(listBootcampUseCase.getBootcamps(0, 10, "name", "DESC"))
            .assertNext(second -> {
                assertEquals("Bootcamp B", second.getName());
                assertEquals(2, second.getCapabilities().size());
            })
            .assertNext(first -> {
                assertEquals("Bootcamp A", first.getName());
                assertEquals(1, first.getCapabilities().size());
                assertEquals("Java", first.getCapabilities().get(0).getTechnologies().get(0).getName());
            })
            .verifyComplete();
    }

    @Test
    void listBootcampsSortByCapabilitiesSizeAndDESC() {
        when(bootcampRepository.findAll()).thenReturn(Flux.just(bootcampA, bootcampB));
        when(capabilityRepository.findCapabilitiesByBootcampId(1L)).thenReturn(Flux.just(backend));
        when(capabilityRepository.findCapabilitiesByBootcampId(2L)).thenReturn(Flux.just(frontend, mobile));

        StepVerifier.create(listBootcampUseCase.getBootcamps(0, 10, "capabilityCount", "ASC"))
            .assertNext(first -> assertEquals("Bootcamp A", first.getName()))
            .assertNext(second -> assertEquals("Bootcamp B", second.getName()))
            .verifyComplete();
    }

    @Test
    void getBootcampsWithCorrectPagination() {
        when(bootcampRepository.findAll()).thenReturn(Flux.just(bootcampA, bootcampB));
        when(capabilityRepository.findCapabilitiesByBootcampId(1L)).thenReturn(Flux.just(backend));
        when(capabilityRepository.findCapabilitiesByBootcampId(2L)).thenReturn(Flux.just(frontend, mobile));

        StepVerifier.create(listBootcampUseCase.getBootcamps(1, 1, "name", "ASC"))
            .assertNext(onlySecondPageItem -> assertEquals("Bootcamp B", onlySecondPageItem.getName()))
            .verifyComplete();
    }

    @Test
    void exceptionWhenParamsAreIncorrect() {
        StepVerifier.create(listBootcampUseCase.getBootcamps(-1, 10, "name", "ASC"))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_PAGINATION_PARAMS.getCode()))
            .verify();

        StepVerifier.create(listBootcampUseCase.getBootcamps(0, 0, "name", "ASC"))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_PAGINATION_PARAMS.getCode()))
            .verify();

        verify(bootcampRepository, never()).findAll();
    }

    @Test
    void exceptionWhenSortAreIncorrect() {
        StepVerifier.create(listBootcampUseCase.getBootcamps(0, 10, "invalidField", "ASC"))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_SORT_PARAMS.getCode()))
            .verify();

        StepVerifier.create(listBootcampUseCase.getBootcamps(0, 10, "name", "random"))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.INVALID_SORT_PARAMS.getCode()))
            .verify();

        verify(bootcampRepository, never()).findAll();
    }

    @Test
    void failWhenCapabilityServiceIsDown() {
        when(bootcampRepository.findAll()).thenReturn(Flux.just(bootcampA));
        when(capabilityRepository.findCapabilitiesByBootcampId(anyLong()))
            .thenReturn(Flux.error(new BusinessException(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE)));

        StepVerifier.create(listBootcampUseCase.getBootcamps(0, 10, "name", "asc"))
            .expectErrorMatches(ex -> ex instanceof BusinessException &&
                ((BusinessException) ex).getCode().equals(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE.getCode()))
            .verify();
    }
}
