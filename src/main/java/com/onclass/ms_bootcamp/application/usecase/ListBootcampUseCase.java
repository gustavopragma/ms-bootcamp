package com.onclass.ms_bootcamp.application.usecase;

import com.onclass.ms_bootcamp.domain.exception.BusinessException;
import com.onclass.ms_bootcamp.domain.exception.ErrorCode;
import com.onclass.ms_bootcamp.domain.model.Bootcamp;
import com.onclass.ms_bootcamp.domain.repository.BootcampRepository;
import com.onclass.ms_bootcamp.domain.repository.CapabilityRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

public class ListBootcampUseCase {
    private static final Integer CONCURRENCY_LIMIT = 10;
    private static final String NAME_PARAM = "name";
    private static final String TECHNOLOGY_COUNT_PARAM = "capabilityCount";
    private static final String ASC_SORT_DIRECTION = "ASC";
    private static final String DESC_SORT_DIRECTION = "DESC";

    private final BootcampRepository bootcampRepository;
    private final CapabilityRepository capabilityRepository;

    public ListBootcampUseCase(
        BootcampRepository bootcampRepository,
        CapabilityRepository capabilityRepository
    ) {
        this.bootcampRepository = bootcampRepository;
        this.capabilityRepository = capabilityRepository;
    }

    public Flux<Bootcamp> getBootcamps(Integer page, Integer size, String sortBy, String sortDirection) {
        if (page == null || size == null || page < 0 || size <= 0) {
            return Flux.error(new BusinessException(ErrorCode.INVALID_PAGINATION_PARAMS));
        }
        if (!NAME_PARAM.equalsIgnoreCase(sortBy) && !TECHNOLOGY_COUNT_PARAM.equalsIgnoreCase(sortBy)) {
            return Flux.error(new BusinessException(ErrorCode.INVALID_SORT_PARAMS));
        }
        if (!ASC_SORT_DIRECTION.equalsIgnoreCase(sortDirection) && !DESC_SORT_DIRECTION.equalsIgnoreCase(sortDirection)) {
            return Flux.error(new BusinessException(ErrorCode.INVALID_SORT_PARAMS));
        }

        return bootcampRepository.findAll()
            .flatMap(this::enrichWithCapabilities, CONCURRENCY_LIMIT)
            .sort(buildComparator(sortBy, sortDirection))
            .skip((long) page * size)
            .take(size);

    }

    private Mono<Bootcamp> enrichWithCapabilities(Bootcamp bootcamp) {
        return capabilityRepository.findCapabilitiesByBootcampId(bootcamp.getId())
            .collectList()
            .map(capabilities -> {
                bootcamp.setCapabilities(capabilities);
                return bootcamp;
            });
    }

    private Comparator<Bootcamp> buildComparator(String sortBy, String sortDirection) {
        Comparator<Bootcamp> comparator;

        if(TECHNOLOGY_COUNT_PARAM.equalsIgnoreCase(sortBy)){
            comparator = Comparator.comparingInt(bootcamp ->
                bootcamp.getCapabilities() == null ? 0 : bootcamp.getCapabilities().size());
        } else {
            comparator =  Comparator.comparing(Bootcamp::getName, String.CASE_INSENSITIVE_ORDER);
        }

        if (DESC_SORT_DIRECTION.equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}
