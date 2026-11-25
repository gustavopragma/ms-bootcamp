package com.onclass.ms_bootcamp.application.usecase;

import com.onclass.ms_bootcamp.domain.exception.BusinessException;
import com.onclass.ms_bootcamp.domain.exception.ErrorCode;
import com.onclass.ms_bootcamp.domain.model.Bootcamp;
import com.onclass.ms_bootcamp.domain.repository.BootcampRepository;
import com.onclass.ms_bootcamp.domain.repository.CapabilityRepository;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegisterBootcampUseCase {
    private static final int MIN_CAPABILITIES = 1;
    private static final int MAX_CAPABILITIES = 4;

    private final BootcampRepository bootcampRepository;
    private final CapabilityRepository capabilityRepository;

    public RegisterBootcampUseCase(BootcampRepository bootcampRepository, CapabilityRepository capabilityRepository) {
        this.bootcampRepository = bootcampRepository;
        this.capabilityRepository = capabilityRepository;
    }

    public Mono<Bootcamp> register(Bootcamp bootcamp) {
        List<Long> capabilityIds = bootcamp.getCapabilityIds();

        if (capabilityIds == null) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_CAPABILITIES_COUNT));
        }

        Set<Long> uniqueIds = new HashSet<>(capabilityIds);
        if (uniqueIds.size() != capabilityIds.size()) {
            return Mono.error(new BusinessException(ErrorCode.DUPLICATE_CAPABILITIES));
        }

        if (uniqueIds.size() < MIN_CAPABILITIES || uniqueIds.size() > MAX_CAPABILITIES) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_CAPABILITIES_COUNT));
        }

        return bootcampRepository.existsByName(bootcamp.getName())
            .flatMap(exists -> {
                if(exists) {
                    return Mono.error(new BusinessException(ErrorCode.BOOTCAMP_ALREADY_EXISTS));
                }
                return bootcampRepository.save(bootcamp);
            })
            .flatMap(savedBootcamp ->
                capabilityRepository.associateCapabilities(savedBootcamp.getId(), capabilityIds)
                    .thenReturn(savedBootcamp)
                    .onErrorResume(error ->
                        bootcampRepository.deleteById(savedBootcamp.getId())
                            .then(Mono.error(error))
                    )
            );
    }
}
