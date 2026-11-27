package com.onclass.ms_bootcamp.application.usecase;

import com.onclass.ms_bootcamp.domain.exception.BusinessException;
import com.onclass.ms_bootcamp.domain.exception.ErrorCode;
import com.onclass.ms_bootcamp.domain.repository.BootcampRepository;
import com.onclass.ms_bootcamp.domain.repository.CapabilityRepository;
import reactor.core.publisher.Mono;

public class DeleteBootcampUseCase {

    private final BootcampRepository bootcampRepository;
    private final CapabilityRepository capabilityRepository;

    public DeleteBootcampUseCase(
        BootcampRepository bootcampRepository,
        CapabilityRepository capabilityRepository
    ) {
        this.bootcampRepository = bootcampRepository;
        this.capabilityRepository = capabilityRepository;
    }

    public Mono<Void> deleteBootcamp(Long id) {
        if(id == null) {
            return Mono.error(new BusinessException(ErrorCode.BOOTCAMP_NOT_FOUND));
        }

        return bootcampRepository.findById(id)
            .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.BOOTCAMP_NOT_FOUND)))
            .flatMap(bootcamp -> bootcampRepository.deleteById(id)
                    .then(Mono.defer(() -> capabilityRepository.deleteCapabilitiesByBootcampId(id)))
                .onErrorResume(error -> bootcampRepository.saveWithId(bootcamp)
                    .then(Mono.error(error)))
            );
    }
}
